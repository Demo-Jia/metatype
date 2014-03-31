package fr.herman.metatype.codegen;

import static java.lang.String.format;
import java.util.EnumMap;
import java.util.Map;
import org.jannocessor.collection.api.PowerList;
import org.jannocessor.model.executable.JavaMethod;
import org.jannocessor.model.structure.AbstractJavaClass;
import org.jannocessor.model.type.JavaType;
import org.jannocessor.model.type.JavaTypeKind;
import org.jannocessor.model.util.New;
import org.jannocessor.model.variable.JavaParameter;

public final class Helper
{
    private static final Map<JavaTypeKind, Class<?>> PRIMITIVE_MAPPER = new EnumMap<JavaTypeKind, Class<?>>(JavaTypeKind.class);
    static
    {
        PRIMITIVE_MAPPER.put(JavaTypeKind.BOOLEAN, Boolean.class);
        PRIMITIVE_MAPPER.put(JavaTypeKind.CHAR, Character.class);
        PRIMITIVE_MAPPER.put(JavaTypeKind.BYTE, Byte.class);
        PRIMITIVE_MAPPER.put(JavaTypeKind.SHORT, Short.class);
        PRIMITIVE_MAPPER.put(JavaTypeKind.INT, Integer.class);
        PRIMITIVE_MAPPER.put(JavaTypeKind.LONG, Long.class);
        PRIMITIVE_MAPPER.put(JavaTypeKind.FLOAT, Float.class);
        PRIMITIVE_MAPPER.put(JavaTypeKind.DOUBLE, Double.class);
        PRIMITIVE_MAPPER.put(JavaTypeKind.VOID, Void.class);
    }

    private Helper()
    {
    }

    public static JavaMethod findMethod(AbstractJavaClass model, String name, JavaType... paramterTypes)
    {
        for (JavaMethod method : model.getMethods())
        {
            if (method.getName().toString().equals(name) && hasSameParams(method, paramterTypes))
            {
                return method;
            }
        }
        return null;
    }

    private static boolean hasSameParams(JavaMethod method,JavaType... paramterTypes){
        if(method.getParameters().size()!=paramterTypes.length)
        {
            return false;
        }
        PowerList<JavaParameter> parameters = method.getParameters();
        for(int i = 0;i<paramterTypes.length;i++){
            if (!parameters.get(i).getType().equals(paramterTypes[i]))
            {
                return false;
            }
        }
        return true;
    }

    public static JavaType type(Class<?> clazz, Class<?>... params)
    {
        StringBuilder sb = new StringBuilder(clazz.getCanonicalName());
        if (params != null && params.length > 0)
        {
            sb.append('<');
            int i = 0;
            for (Class<?> param : params)
            {
                sb.append(param.getCanonicalName());
                if (++i < params.length)
                {
                    sb.append(',');
                }
            }
            sb.append('>');
        }
        return New.type(sb.toString());
    }

    public static String typeSimple(Class<?> clazz, Class<?>... params)
    {
        StringBuilder sb = new StringBuilder(clazz.getSimpleName());
        if (params != null && params.length > 0)
        {
            sb.append('<');
            int i = 0;
            for (Class<?> param : params)
            {
                sb.append(param.getSimpleName());
                if (++i < params.length)
                {
                    sb.append(',');
                }
            }
            sb.append('>');
        }
        return sb.toString();
    }

    public static JavaType fromPrimitive(JavaType type)
    {
        Class<?> clazz = PRIMITIVE_MAPPER.get(type.getKind());
        if (clazz != null)
        {
            return New.type(clazz);
        }
        throw new RuntimeException(format("type %s is not a primitive", type));
    }
}
