package fr.herman.metatype.processor;

import static java.lang.String.format;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import lombok.Data;
import com.squareup.javawriter.JavaWriter;
import fr.herman.metatype.model.MetaClass;
import fr.herman.metatype.model.MetaProperty;
import fr.herman.metatype.model.method.Getter;
import fr.herman.metatype.model.method.HasGetter;
import fr.herman.metatype.model.method.HasGetterSetter;
import fr.herman.metatype.model.method.HasSetter;
import fr.herman.metatype.model.method.Setter;
import fr.herman.metatype.processor.meta.ClassMeta;
import fr.herman.metatype.processor.meta.GetterMeta;
import fr.herman.metatype.processor.meta.PropertyMeta;
import fr.herman.metatype.processor.meta.SetterMeta;

@Data
public class SourceGenerator
{
    private static final String PROPERTIES = "PROPERTIES";

    private static interface Types
    {
        String STRING = String.class.getCanonicalName();
    }

    private final Context       context;
    private final JavaWriter2   writer;
    private final ClassMeta     classMeta;

    public SourceGenerator(Context context, JavaWriter2 writer, ClassMeta classMeta)
    {
        this.context = context;
        this.writer = writer;
        this.classMeta = classMeta;
        writer.setIndent("    ");
    }

    public void generate() throws Exception
    {
        writePackage();
        writeImports();
        writeClass();
    }

    private void writeImports() throws IOException
    {
        writer.emitImports(rawForImport(classMeta.getOriginalType()));
        if (classMeta.getSuperType() != null)
        {
            writer.emitImports(classMeta.getSuperType().getCanonicalName());
        }
        for (PropertyMeta property : classMeta.getProperties())
        {
            writer.emitImports(rawForImport(property.getType()));
            if (property.getGetter() != null && property.getGetter() != null)
            {
                writer.emitImports(HasGetterSetter.class, Getter.class, Setter.class);
            }
            else if (property.getGetter() != null)
            {
                writer.emitImports(HasGetter.class, Getter.class);
            }
            else if (property.getSetter() != null)
            {
                writer.emitImports(HasSetter.class, Setter.class);
            }

        }
        writer.emitImports(MetaClass.class, MetaProperty.class, Collection.class, Collections.class, LinkedList.class);
    }

    private void writePackage() throws IOException
    {
        writer.emitPackage(classMeta.getPackageName());
    }

    private String rawForImport(TypeMirror type)
    {
        if (type.getKind() != TypeKind.DECLARED)
        {
            return JavaWriter2.NO_IMPORT;
        }
        return raw(type);
    }

    private String raw(TypeMirror type)
    {
        return JavaWriter.rawType(type.toString());
    }

    private void writeClass() throws IOException
    {
        String superType = classMeta.getSuperType() != null ? classMeta.getSuperType().getCanonicalName() : null;
        writer.beginDeclaredClass(classMeta.getCanonicalName(), superType, MetaClass.class.getCanonicalName());
        for (PropertyMeta property : classMeta.getProperties())
        {
            writeProperty(property);
        }
        String superPropertiesType = "? super " + writer.compressType(classMeta.getOriginalType().toString());
        String propertiesType = JavaWriter.type(MetaProperty.class, superPropertiesType, "?");

        String propertiesCollectionType = JavaWriter.type(Collection.class, propertiesType);
        writer.emitField(propertiesCollectionType, PROPERTIES, EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL));
        writer.beginInitializer(true);
        writer.emitStatement("%s c = new LinkedList<%s>()", writer.compressType(propertiesCollectionType), writer.compressType(propertiesType));
        if (classMeta.getSuperType() != null)
        {
            writer.beginControlFlow("for(MetaProperty<? super %s,?> mp : %s.PROPERTIES)", writer.compressType(classMeta.getSuperType().getOriginalType().toString()), writer.compressType(superType));
            writer.emitStatement("c.add(mp)");
            writer.endControlFlow();
        }
        for (PropertyMeta property : classMeta.getProperties())
        {
            writer.emitStatement("c.add(%s)", property.getName());
        }
        writer.emitStatement("PROPERTIES=Collections.unmodifiableCollection(c)");
        writer.endInitializer();
        // writer.emitCustomGetter(JavaWriter.type(Collection.class, MetaProperty.class.getCanonicalName()), "PROPERTIES", "properties");
        String compressedOriginalType = writer.compressType(raw(classMeta.getOriginalType()));
        writer.emitConstant(JavaWriter.type(Class.class, classMeta.getOriginalType().toString()), "TYPE", Modifier.PUBLIC, format("%s.class", compressedOriginalType));
        if (classMeta.getSuperType() != null)
        {
            writer.emitAnnotation(Override.class);
        }
        writer.emitCustomGetter("Class<?>", "TYPE", "type");
        writer.endClass();
    }

    private void writeProperty(PropertyMeta property) throws IOException
    {
        boolean hasGetter = property.getGetter() != null;
        boolean hasSetter = property.getSetter() != null;

        String metaType = WordUtils.capitalize(property.getName()) + "Property";
        List<String> interfaces = new ArrayList<String>();
        String originalType = classMeta.getOriginalType().toString();
        String propertyType = property.getType().toString();
        interfaces.add(JavaWriter.type(MetaProperty.class, originalType, propertyType));
        if (hasGetter && hasSetter)
        {
            interfaces.add(JavaWriter.type(HasGetterSetter.class, originalType, propertyType));
            interfaces.add(JavaWriter.type(Getter.class, originalType, propertyType));
            interfaces.add(JavaWriter.type(Setter.class, originalType, propertyType));
        }
        else if (hasGetter)
        {
            interfaces.add(JavaWriter.type(HasGetter.class, originalType, propertyType));
            interfaces.add(JavaWriter.type(Getter.class, originalType, propertyType));
        }
        else if (hasSetter)
        {
            interfaces.add(JavaWriter.type(HasSetter.class, originalType, propertyType));
            interfaces.add(JavaWriter.type(Setter.class, originalType, propertyType));
        }

        writer.beginClass(metaType, EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL), null, interfaces.toArray(new String[interfaces.size()]));
        writer.emitConstant(Types.STRING, "NAME", Modifier.PRIVATE, JavaWriter.stringLiteral(property.getName()));
        writer.emitCustomGetter(Types.STRING, "NAME", "name");
        if (hasGetter)
        {
            writeGetter(property.getGetter());
        }
        if (hasSetter)
        {
            writeSetter(property.getSetter());
        }
        writer.emitSimpleMethod("Class<?>", "type", format("return %s.class", writer.compressType(raw(property.getType()))));
        writer.emitCustomGetter(JavaWriter.type(Class.class, originalType), classMeta.getSimpleName() + ".TYPE", "modelType");
        writer.endClass();
        writer.emitConstant(metaType, property.getName(), Modifier.PUBLIC, format("new %s()", writer.compressType(metaType)));
    }

    private void writeGetter(GetterMeta getter) throws IOException
    {
        writer.beginMethod(getter.getValueType().toString(), "getValue", EnumSet.of(Modifier.PUBLIC), getter.getObjectType().toString(), "o");
        writer.emitStatement("return o.%s()", getter.getDelegateMethodName());
        writer.endMethod();
        String type = JavaWriter.type(Getter.class, getter.getObjectType().toString(), getter.getValueType().toString());
        writer.emitCustomGetter(type, "this", "getter");
    }

    private void writeSetter(SetterMeta setter) throws IOException
    {
        writer.beginMethod("void", "setValue", EnumSet.of(Modifier.PUBLIC), setter.getObjectType().toString(), "o", setter.getValueType().toString(), "v");
        writer.emitStatement("o.%s(v)", setter.getDelegateMethodName());
        writer.endMethod();
        String type = JavaWriter.type(Setter.class, setter.getObjectType().toString(), setter.getValueType().toString());
        writer.emitCustomGetter(type, "this", "setter");
    }

}
