package fr.herman.metatype.processor;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import fr.herman.metatype.annotation.Bean;
import fr.herman.metatype.processor.meta.ClassMeta;
import fr.herman.metatype.processor.meta.GetterMeta;
import fr.herman.metatype.processor.meta.PropertyMeta;
import fr.herman.metatype.processor.meta.SetterMeta;

@SupportedAnnotationTypes({"fr.herman.metatype.annotation.Bean"})
public class MetaTypeProcessor extends AbstractProcessor
{

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        Context context = new Context(processingEnv);
        Map<TypeMirror, BeanWrapper> wrappers = new HashMap<TypeMirror, BeanWrapper>();
        Map<TypeMirror, ClassMeta> metas = new HashMap<TypeMirror, ClassMeta>();
        for (Element element : roundEnv.getElementsAnnotatedWith(Bean.class))
        {
            TypeElement typeElement = (TypeElement) element;
            wrappers.put(element.asType(), new BeanWrapper(context, typeElement));
            metas.put(element.asType(), new ClassMeta());
            while (!typeElement.getSuperclass().toString().equals(Object.class.getCanonicalName()))
            {
                TypeMirror type = typeElement.getSuperclass();
                typeElement = (TypeElement) processingEnv.getTypeUtils().asElement(type);
                if (!wrappers.containsKey(type))
                {
                    wrappers.put(type, new BeanWrapper(context, typeElement));
                    metas.put(type, new ClassMeta());
                }
            }
        }

        for (TypeMirror type : wrappers.keySet())
        {
            BeanWrapper wrapper = wrappers.get(type);
            Iterable<? extends String> fields2 = wrapper.getFields();
            ClassMeta classMeta = metas.get(type);
            classMeta.setOriginalType(wrapper.getType());
            classMeta.setSimpleName(wrapper.typeElement.getSimpleName().toString() + "Meta");
            classMeta.setPackageName(wrapper.getPackage().getQualifiedName().toString());
            classMeta.setSuperType(metas.get(wrapper.typeElement.getSuperclass()));
            List<PropertyMeta> properties = new ArrayList<PropertyMeta>();
            for (String field : fields2)
            {
                PropertyMeta property = new PropertyMeta();
                property.setName(field);
                property.setType(boxed(wrapper.getFieldType(field)));
                MethodWrapper getter = wrapper.getGetterFor(field);
                if (getter != null)
                {
                    GetterMeta getterMeta = new GetterMeta();
                    getterMeta.setDelegateMethodName(getter.getSimpleName());
                    getterMeta.setObjectType(classMeta.getOriginalType());
                    getterMeta.setValueType(boxed(getter.returnType()));
                    property.setGetter(getterMeta);
                }
                MethodWrapper setter = wrapper.getSetterFor(field);
                if (setter != null)
                {
                    SetterMeta setterMeta = new SetterMeta();
                    setterMeta.setDelegateMethodName(setter.getSimpleName());
                    setterMeta.setObjectType(classMeta.getOriginalType());
                    setterMeta.setValueType(boxed(setter.firstParameterType()));
                    property.setSetter(setterMeta);
                }

                properties.add(property);
            }
            classMeta.setProperties(properties);
        }
        for (ClassMeta meta : metas.values())
        {
            Writer writer = null;
            try
            {
                JavaFileObject source = processingEnv.getFiler().createSourceFile(meta.getCanonicalName(), wrappers.get(meta.getOriginalType()).typeElement);
                writer = source.openWriter();
                JavaWriter2 javaWriter2 = new JavaWriter2(writer);
                SourceGenerator generator = new SourceGenerator(context, javaWriter2, meta);
                generator.generate();
                javaWriter2.close();
            }
            catch (Exception e)
            {
                try
                {
                    writer.close();
                }
                catch (Throwable t)
                {

                }
                e.printStackTrace();
            }
        }

        return false;
    }

    private TypeMirror boxed(TypeMirror type)
    {
        if (type.getKind().isPrimitive())
        {
            return processingEnv.getTypeUtils().boxedClass((PrimitiveType) type).asType();
        }
        return type;
    }
}
