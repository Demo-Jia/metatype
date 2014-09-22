package fr.herman.metatype.processor;

import static com.squareup.javawriter.JavaWriter.rawType;
import static com.squareup.javawriter.JavaWriter.stringLiteral;
import static com.squareup.javawriter.JavaWriter.type;
import static java.lang.String.format;
import static java.util.EnumSet.of;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import lombok.Data;
import com.squareup.javawriter.JavaWriter;
import fr.herman.metatype.model.MetaClass;
import fr.herman.metatype.model.MetaClassNode;
import fr.herman.metatype.model.MetaClassNode.PropertySetterNode;
import fr.herman.metatype.model.MetaClassTemplate;
import fr.herman.metatype.model.MetaProperty;
import fr.herman.metatype.model.MetaPropertyGetter;
import fr.herman.metatype.model.MetaPropertyGetterSetter;
import fr.herman.metatype.model.MetaPropertyGetterSetterTemplate;
import fr.herman.metatype.model.MetaPropertyGetterTemplate;
import fr.herman.metatype.model.MetaPropertySetter;
import fr.herman.metatype.model.MetaPropertySetterTemplate;
import fr.herman.metatype.model.method.Getter;
import fr.herman.metatype.model.method.HasSetter;
import fr.herman.metatype.model.method.Setter;
import fr.herman.metatype.processor.meta.ClassMeta;
import fr.herman.metatype.processor.meta.GetterMeta;
import fr.herman.metatype.processor.meta.PropertyMeta;
import fr.herman.metatype.processor.meta.SetterMeta;

@Data
public class SourceGenerator
{
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
                writer.emitImports(Getter.class, Setter.class);
            }
            else if (property.getGetter() != null)
            {
                writer.emitImports(Getter.class);
            }
            else if (property.getSetter() != null)
            {
                writer.emitImports(Setter.class);
            }

        }
        writer.emitImports(MetaClass.class, MetaProperty.class, Collection.class);
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
        String superTypeFormat = "%s<ROOT,CURRENT,VALUE>";
        String superType = classMeta.getSuperType() != null ? classMeta.getSuperType().getCanonicalName() : MetaClassTemplate.class.getCanonicalName();

        String metaClassName = format("%s<ROOT,CURRENT,VALUE extends %s>", classMeta.getCanonicalName(), classMeta.getOriginalType());
        writer.beginDeclaredClass(metaClassName, format(superTypeFormat, superType));
        writer.beginConstructor(of(PUBLIC), JavaWriter.type(MetaClassNode.class, "ROOT", "CURRENT", "VALUE"), "node");
        writer.emitStatement("super(node)");
        writer.endConstructor();
        for (PropertyMeta property : classMeta.getProperties())
        {
            writeProperty(property);
        }

        writer.beginClass(format("%sSetter<ROOT,CURRENT,VALUE extends %s>", classMeta.getCanonicalName(), classMeta.getOriginalType()), EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL),
            format("%s<ROOT,CURRENT,VALUE>", classMeta.getCanonicalName(), classMeta.getOriginalType()), type(Setter.class, "ROOT", "VALUE"), type(HasSetter.class, "ROOT", "VALUE"));
        writer.emitField(type(PropertySetterNode.class, "ROOT", "CURRENT", "VALUE"), "node", of(PRIVATE, FINAL));

        writer.beginConstructor(of(PUBLIC), JavaWriter.type(PropertySetterNode.class, "ROOT", "CURRENT", "VALUE"), "node");
        writer.emitStatement("super(node)");
        writer.emitStatement("this.node=node");
        writer.endConstructor();

        writer.beginMethod("void", "setValue", EnumSet.of(Modifier.PUBLIC), "ROOT", "o", "VALUE", "v");
        writer.emitStatement("node.setValue(o,v)");
        writer.endMethod();
        writer.beginMethod(type(Setter.class, "ROOT", "VALUE"), "setter", EnumSet.of(Modifier.PUBLIC));
        writer.emitStatement("return this");
        writer.endMethod();

        writer.endClass();

        String metaClassNameConstant = MessageFormat.format("{0}<{1},{1},{1}>", classMeta.getSimpleName(), classMeta.getOriginalType());
        writer.emitConstant(metaClassNameConstant, "$", PUBLIC, format("new %s(fr.herman.metatype.model.MetaClassNode.root(%s.class))", metaClassNameConstant, classMeta.getOriginalType()));
        writer.endClass();
    }

    protected void writeProperty(PropertyMeta property) throws IOException
    {
        boolean hasGetter = property.getGetter() != null;
        boolean hasSetter = property.getSetter() != null;

        String name = property.getName();
        String originalType = classMeta.getOriginalType().toString();
        String metaType = format("%sAcessor", WordUtils.capitalize(name));
        List<String> interfaces = new ArrayList<String>();
        String propertyType = property.getType().toString();
        if (hasGetter)
        {
            interfaces.add(JavaWriter.type(Getter.class, "TYPE", propertyType));
        }
        if (hasSetter)
        {
            interfaces.add(JavaWriter.type(Setter.class, "TYPE", propertyType));
        }

        writer.beginClass(format("%s<TYPE extends %s>", metaType, originalType), EnumSet.of(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL), null, interfaces.toArray(new String[interfaces.size()]));
        if (hasGetter)
        {
            GetterMeta getter = property.getGetter();
            writer.beginMethod(getter.getValueType().toString(), "getValue", EnumSet.of(Modifier.PUBLIC), "TYPE", "o");
            writer.emitStatement("return o.%s()", getter.getDelegateMethodName());
            writer.endMethod();
        }
        if (hasSetter)
        {
            SetterMeta setter = property.getSetter();
            writer.beginMethod("void", "setValue", EnumSet.of(Modifier.PUBLIC), "TYPE", "o", setter.getValueType().toString(), "v");
            writer.emitStatement("o.%s(v)", setter.getDelegateMethodName());
            writer.endMethod();
        }
        writer.endClass();
        String accessor = name.toUpperCase() + "ACESSOR";
        writer.emitConstant(metaType, accessor, Modifier.PRIVATE, format("new %s()", writer.compressType(metaType)));
        String propertyTypeClass = !propertyType.contains("<") ? propertyType : format("(Class<%s>)(Class) %s", propertyType, rawType(propertyType));

        if (hasGetter && hasSetter)
        {
            if (context.hasMeta(property.getType()))
            {
                ClassMeta meta = context.getMeta(property.getType());
                writer.emitField(
                    format("%s.%sSetter<ROOT,VALUE,%s>", rawType(meta.getCanonicalName()), meta.getSimpleName(), propertyType),
                    name,
                    of(PUBLIC, FINAL),
                    format("new %s.%sSetter<ROOT,VALUE,%s>(fr.herman.metatype.model.MetaClassNode.property(this,%s.class,%s,%s,%s))", rawType(meta.getCanonicalName()), meta.getSimpleName(), propertyType, propertyTypeClass,
                        stringLiteral(name), accessor, accessor));
            }
            else
            {
            writer.emitField(JavaWriter.type(MetaPropertyGetterSetter.class, "ROOT", "VALUE", propertyType), name, of(PUBLIC, FINAL),
                format("new %s<ROOT,VALUE,%s>(this,%s.class,%s,%s)", MetaPropertyGetterSetterTemplate.class.getCanonicalName(), propertyType, propertyTypeClass, stringLiteral(name), accessor));
            }
        }
        else if (hasGetter)
        {
            if (context.hasMeta(property.getType()))
            {
                ClassMeta meta = context.getMeta(property.getType());
                writer.emitField(format("%s<ROOT,VALUE,%s>", rawType(meta.getCanonicalName()), propertyType), name, of(PUBLIC, FINAL),
                    format("new %s<ROOT,VALUE,%s>(fr.herman.metatype.model.MetaClassNode.property(this,%s.class,%s,%s))", rawType(meta.getCanonicalName()), propertyType, propertyTypeClass, stringLiteral(name), accessor));
            }
            else
            {
            writer.emitField(JavaWriter.type(MetaPropertyGetter.class, "ROOT", "VALUE", propertyType), name, of(PUBLIC, FINAL),
                format("new %s<ROOT,VALUE,%s>(this,%s.class,%s,%s)", MetaPropertyGetterTemplate.class.getCanonicalName(), propertyType, propertyTypeClass, stringLiteral(name), accessor));
            }
        }
        else if (hasSetter)
        {
            writer.emitField(JavaWriter.type(MetaPropertySetter.class, "ROOT", "VALUE", propertyType), name, of(PUBLIC, FINAL),
                format("new %s<ROOT,VALUE,%s>(this,%s.class,%s,%s)", MetaPropertySetterTemplate.class.getCanonicalName(), propertyType, propertyTypeClass, stringLiteral(name), accessor));
        }
    }

}
