package fr.herman.metatype.processor;

import static java.lang.String.format;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumSet;
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
    private static interface Types
    {
        String STRING = String.class.getCanonicalName();
    }

    private static final String GETTER = "new {0}()'{'\npublic {2} getValue({1} o)'{'\nreturn o.{3}();\n'}\n}'";
    private static final String SETTER = "new {0}()'{'\npublic void setValue({1} o,{2} value)'{'\no.{3}(value);\n'}\n}'";

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
        }
        writer.emitImports(MetaClass.class, MetaProperty.class, HasGetter.class, HasSetter.class, HasGetterSetter.class, Getter.class, Setter.class);
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
        writer.endClass();
    }

    private void writeProperty(PropertyMeta property) throws IOException
    {
        boolean hasGetter = property.getGetter() != null;
        boolean hasSetter = property.getSetter() != null;

        String metaType = WordUtils.capitalize(property.getName()) + "Property";
        List<String> interfaces = new ArrayList<String>();
        interfaces.add(JavaWriter.type(MetaProperty.class, classMeta.getOriginalType().toString(), property.getType().toString()));
        if (hasGetter && hasSetter)
        {
            interfaces.add(JavaWriter.type(HasGetterSetter.class, classMeta.getOriginalType().toString(), property.getType().toString()));
        }
        else if (hasGetter)
        {
            interfaces.add(JavaWriter.type(HasGetter.class, classMeta.getOriginalType().toString(), property.getType().toString()));
        }
        else if (hasSetter)
        {
            interfaces.add(JavaWriter.type(HasSetter.class, classMeta.getOriginalType().toString(), property.getType().toString()));
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
        writer.endClass();
        writer.emitConstant(metaType, property.getName(), Modifier.PUBLIC, format("new %s()", writer.compressType(metaType)));
    }

    private void writeGetter(GetterMeta getter) throws IOException
    {
        String type = JavaWriter.type(Getter.class, getter.getObjectType().toString(), getter.getValueType().toString());
        String value = MessageFormat.format(GETTER, writer.compressType(type), writer.compressType(getter.getObjectType().toString()), writer.compressType(getter.getValueType().toString()), getter.getDelegateMethodName());
        writer.emitConstant(type, "GETTER", Modifier.PRIVATE, value);
        writer.emitCustomGetter(type, "GETTER", "getter");
    }

    private void writeSetter(SetterMeta setter) throws IOException
    {
        String type = JavaWriter.type(Setter.class, setter.getObjectType().toString(), setter.getValueType().toString());
        String value = MessageFormat.format(SETTER, writer.compressType(type), writer.compressType(setter.getObjectType().toString()), writer.compressType(setter.getValueType().toString()), setter.getDelegateMethodName());
        writer.emitConstant(type, "SETTER", Modifier.PRIVATE, value);
        writer.emitCustomGetter(type, "SETTER", "setter");
    }

}
