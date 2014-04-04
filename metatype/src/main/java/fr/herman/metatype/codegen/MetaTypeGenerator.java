package fr.herman.metatype.codegen;

import org.jannocessor.collection.api.PowerList;
import org.jannocessor.extra.processor.AbstractGenerator;
import org.jannocessor.model.executable.JavaMethod;
import org.jannocessor.model.structure.AbstractJavaClass;
import org.jannocessor.model.structure.JavaClass;
import org.jannocessor.model.structure.JavaNestedClass;
import org.jannocessor.model.type.JavaType;
import org.jannocessor.model.util.Classes;
import org.jannocessor.model.util.Fields;
import org.jannocessor.model.util.Methods;
import org.jannocessor.model.util.NestedClasses;
import org.jannocessor.model.util.New;
import org.jannocessor.model.variable.JavaField;
import org.jannocessor.processor.api.ProcessingContext;
import fr.herman.metatype.model.MetaProperty;
import fr.herman.metatype.model.method.Getter;
import fr.herman.metatype.model.method.HasGetter;
import fr.herman.metatype.model.method.HasSetter;
import fr.herman.metatype.model.method.Setter;

public class MetaTypeGenerator extends AbstractGenerator<AbstractJavaClass>
{

    public MetaTypeGenerator(String destPackage)
    {
        super(destPackage, false);
    }

    @Override
    protected void generateCodeFrom(PowerList<AbstractJavaClass> models, ProcessingContext context)
    {
        context.getLogger().info("Start to generate MetaModel");
        for (AbstractJavaClass model : models)
        {
            try
            {
                String metaClassName = model.getName().toString() + "MetaType";
                JavaClass metaClass = New.classs(Classes.PUBLIC, metaClassName);

                for (JavaField property : model.getFields())
                {
                    String propertyName = property.getName().toString();
                    String propertyMetaClassName = propertyName + "MetaProperty";
                    JavaNestedClass propertyMetaClass = New.nestedClass(NestedClasses.PUBLIC_FINAL_STATIC, propertyMetaClassName);
                    boolean isPrimitive = property.getType().getKind().isPrimitive();
                    JavaType type = isPrimitive ? Helper.fromPrimitive(property.getType()) : property.getType();

                    addIsPrimitive(propertyMetaClass, isPrimitive);

                    JavaType metaPropertyType = Helper.type(MetaProperty.class, model.getType(), type);
                    propertyMetaClass.getInterfaces().add(metaPropertyType);
                    metaClass.getNestedClasses().add(propertyMetaClass);

                    addMetaNameProperty(propertyMetaClass, propertyName);
                    addMetaTypeProperty(propertyMetaClass, property, type);
                    addGetter(propertyMetaClass, model, property, type);
                    addSetter(propertyMetaClass, model, property, type);

                    JavaField metaPropertyField = New.field(Fields.PUBLIC_STATIC_FINAL, New.type(propertyMetaClassName), propertyName);
                    metaPropertyField.getValue().setHardcoded("new %s()", propertyMetaClassName);

                    metaClass.getFields().add(metaPropertyField);
                }

                generate(metaClass);
            }
            catch (Throwable t)
            {
                context.getLogger().error(t.getMessage(), t);
                t.printStackTrace();
            }
        }

    }

    private void addIsPrimitive(JavaNestedClass propertyMetaClass, boolean isPrimitive)
    {
        JavaField isPrimitiveField = New.field(Fields.PUBLIC_STATIC_FINAL, boolean.class, "IS_PRIMITIVE", New.literal(isPrimitive));
        propertyMetaClass.getFields().add(isPrimitiveField);

        JavaMethod isPrimitiveMethod = New.method(Methods.PUBLIC, boolean.class, "isPrimitive");
        isPrimitiveMethod.getBody().setHardcoded("return IS_PRIMITIVE;");
        propertyMetaClass.getMethods().add(isPrimitiveMethod);
    }

    private void addGetter(JavaNestedClass propertyMetaClass, AbstractJavaClass model, JavaField property, JavaType type)
    {
        JavaMethod method = Helper.findMethod(model, "get" + property.getName().getCapitalized());
        if (method != null)
        {
            JavaType getterClass = Helper.type(Getter.class, model.getType(), type);

            JavaField metaGetterField = New.field(Fields.PUBLIC_STATIC_FINAL, getterClass, "GETTER");
            metaGetterField.getValue().setHardcoded("new %s(){public %s getValue(%s object){return object.%s();}}", Helper.type(Getter.class, model.getType(), type).getCanonicalName(), type.getCanonicalName(),
                model.getType().getCanonicalName(),
                method.getName().toString());
            propertyMetaClass.getFields().add(metaGetterField);

            JavaType metaPropertyType = Helper.type(HasGetter.class, model.getType(), type);
            propertyMetaClass.getInterfaces().add(metaPropertyType);

            JavaMethod getterMethod = New.method(Methods.PUBLIC, getterClass, "getter");
            getterMethod.getBody().setHardcoded("return GETTER;");
            propertyMetaClass.getMethods().add(getterMethod);
        }
    }

    private void addSetter(JavaNestedClass propertyMetaClass, AbstractJavaClass model, JavaField property, JavaType type)
    {
        JavaMethod method = Helper.findMethod(model, "set" + property.getName().getCapitalized(), property.getType());
        if (method != null)
        {
            JavaType setterClass = Helper.type(Setter.class, model.getType(), type);

            JavaField metaSetterField = New.field(Fields.PUBLIC_STATIC_FINAL, setterClass, "SETTER");
            metaSetterField.getValue().setHardcoded("new %s(){public void setValue(%s object,%s value){object.%s(value);}}", Helper.type(Setter.class, model.getType(), type).getCanonicalName(), model.getType().getCanonicalName(),
                type.getCanonicalName(),
                method.getName().toString());
            propertyMetaClass.getFields().add(metaSetterField);

            JavaType metaPropertyType = Helper.type(HasSetter.class, model.getType(), type);
            propertyMetaClass.getInterfaces().add(metaPropertyType);

            JavaMethod setterMethod = New.method(Methods.PUBLIC, setterClass, "setter");
            setterMethod.getBody().setHardcoded("return SETTER;");
            propertyMetaClass.getMethods().add(setterMethod);
        }
    }

    private void addMetaTypeProperty(JavaNestedClass propertyMetaClass, JavaField property, JavaType type)
    {
        JavaMethod typePropertyMethod = New.method(Methods.PUBLIC, New.type(Class.class), "type");
        typePropertyMethod.getMetadata().add(New.metadata(Override.class));
        typePropertyMethod.getBody().setHardcoded("return %s.class;", type.getSimpleName());
        propertyMetaClass.getMethods().add(typePropertyMethod);
    }

    private void addMetaNameProperty(JavaNestedClass propertyMetaClass, String propertyName)
    {
        JavaField namePropertyField = New.field(Fields.PUBLIC_STATIC_FINAL, String.class, "NAME");
        namePropertyField.getValue().setHardcoded("\"%s\"", propertyName);
        propertyMetaClass.getFields().add(namePropertyField);

        JavaMethod namePropertyMethod = New.method(Methods.PUBLIC, String.class, "name");
        namePropertyMethod.getMetadata().add(New.metadata(Override.class));
        namePropertyMethod.getBody().setHardcoded("return %s;", namePropertyField.getName());
        propertyMetaClass.getMethods().add(namePropertyMethod);
    }




}
