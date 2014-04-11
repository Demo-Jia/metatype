package fr.herman.metatype.querydsl;

import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import com.google.common.primitives.Primitives;
import com.mysema.codegen.CodegenException;
import com.mysema.codegen.support.ClassUtils;
import com.mysema.query.EmptyMetadata;
import com.mysema.query.QueryMetadata;
import com.mysema.query.collections.CollQuerySerializer;
import com.mysema.query.collections.CollQueryTemplates;
import com.mysema.query.types.Expression;
import com.mysema.query.types.FactoryExpression;
import com.mysema.query.types.ParamExpression;
import com.mysema.query.types.ParamNotSetException;
import com.mysema.query.types.Path;
import fr.herman.metatype.querydsl.generator.ClassGenerator;
import fr.herman.metatype.querydsl.generator.JDKClassGenerator;

public class CodeFactory
{
    private final CollQueryTemplates templates;

    private final ClassGenerator     generator = new JDKClassGenerator((URLClassLoader) Thread.currentThread().getContextClassLoader());

    public CodeFactory(CollQueryTemplates templates)
    {
        this.templates = templates;
    }

    public <T, BEAN, FIELD> T createLambda(Class<T> functionalInterfaceClass, Path<BEAN> bean, Expression<FIELD> projection)
    {
        final CollQuerySerializer serializer = new CollQuerySerializer(templates);
        serializer.append("return ");
        if (projection instanceof FactoryExpression<?>)
        {
            serializer.append("(");
            serializer.append(ClassUtils.getName(projection.getType()));
            serializer.append(")(");
            serializer.handle(projection);
            serializer.append(")");
        }
        else
        {
            serializer.handle(projection);
        }
        serializer.append(";");
        Map<Object, String> constantToLabel = serializer.getConstantToLabel();
        Map<String, Object> constants = getConstants(EmptyMetadata.DEFAULT, constantToLabel);
        Class<?>[] types = new Class<?>[]{bean.getType()};
        String[] names = new String[]{bean.toString()};

        // normalize types
        for (int i = 0; i < types.length; i++)
        {
            if (Primitives.isWrapperType(types[i]))
            {
                types[i] = Primitives.unwrap(types[i]);
            }
        }
        Class<T> clazz = generator.createPseudoLambda(functionalInterfaceClass, serializer.toString(), projection.getType(), names, types, constants);
        try
        {
        return clazz.newInstance();
        }
        catch (Exception e)
        {
            throw new CodegenException(e);
        }
    }

    private Map<String, Object> getConstants(QueryMetadata metadata, Map<Object, String> constantToLabel)
    {
        Map<String, Object> constants = new HashMap<String, Object>();
        for (Map.Entry<Object, String> entry : constantToLabel.entrySet())
        {
            if (entry.getKey() instanceof ParamExpression<?>)
            {
                Object value = metadata.getParams().get(entry.getKey());
                if (value == null)
                {
                    throw new ParamNotSetException((ParamExpression<?>) entry.getKey());
                }
                constants.put(entry.getValue(), value);
            }
            else
            {
                constants.put(entry.getValue(), entry.getKey());
            }
        }
        return constants;
    }
}
