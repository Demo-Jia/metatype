package fr.herman.metatype.processor;

import static java.lang.String.format;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import javax.lang.model.element.Modifier;
import com.squareup.javawriter.JavaWriter;

public class JavaWriter2 extends JavaWriter
{

    public static final String       NO_IMPORT     = "";

    private final Collection<String> importedTypes = new HashSet<String>();

    public JavaWriter2(Writer out)
    {
        super(out);
    }

    public JavaWriter beginClass(String type) throws IOException
    {
        return super.beginType(type, "class");
    }

    public JavaWriter beginDeclaredClass(String type, String extendsType, String... implementsTypes) throws IOException
    {
        return super.beginType(type, "class", EnumSet.of(Modifier.PUBLIC), extendsType, implementsTypes);
    }

    public JavaWriter beginClass(String type, Modifier... modifiers) throws IOException
    {
        return super.beginType(type, "class", EnumSet.of(modifiers[0], modifiers));
    }

    public JavaWriter beginClass(String type, Set<Modifier> modifiers, String extendsType, String... implementsTypes) throws IOException
    {
        return super.beginType(type, "class", modifiers, extendsType, implementsTypes);
    }

    public JavaWriter endClass() throws IOException
    {
        return super.endType();
    }

    @Override
    public JavaWriter emitImports(Collection<String> types) throws IOException
    {
        Collection<String> filtered = new ArrayList<String>(types.size());
        for (String type : types)
        {
            if (!NO_IMPORT.equals(type) && !importedTypes.contains(type) && type.equals(compressType(type)))
            {
                filtered.add(type);
                importedTypes.add(type);
            }
        }
        return super.emitImports(filtered);
    }

    public JavaWriter emitConstant(String type, String name, Modifier modifier, String initialValue) throws IOException
    {
        return super.emitField(type, name, EnumSet.of(modifier, Modifier.STATIC, Modifier.FINAL), initialValue);
    }

    public JavaWriter emitStatement2(MessageFormat pattern, Object... args) throws IOException
    {
        return super.emitStatement(pattern.format(args));
    }

    public JavaWriter emitSimpleMethod(String returnType, String name, String statement, String... parameters) throws IOException
    {
        beginMethod(returnType, name, EnumSet.of(Modifier.PUBLIC), parameters);
        emitStatement(statement);
        return endMethod();
    }

    public JavaWriter emitCustomGetter(String propertyType, String propertyName, String methodName) throws IOException
    {
        return emitSimpleMethod(propertyType, methodName, format("return %s", propertyName));
    }

    public JavaWriter emitGetter(String propertyType, String propertyName) throws IOException
    {
        return emitSimpleMethod(propertyType, format("get%s", WordUtils.capitalize(propertyName)), format("return %s", propertyName));
    }
}
