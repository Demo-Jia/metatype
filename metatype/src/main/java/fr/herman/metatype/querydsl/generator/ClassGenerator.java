package fr.herman.metatype.querydsl.generator;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import com.mysema.codegen.CodegenException;
import com.mysema.codegen.JavaWriter;
import com.mysema.codegen.model.ClassType;
import com.mysema.codegen.model.Parameter;
import com.mysema.codegen.model.SimpleType;
import com.mysema.codegen.model.Type;
import com.mysema.codegen.model.TypeCategory;
import com.mysema.codegen.support.ClassUtils;
import fr.herman.metatype.querydsl.generator.id.EncodedIdProvider;
import fr.herman.metatype.querydsl.generator.id.EncodedIdProvider.Algo;
import fr.herman.metatype.querydsl.generator.id.IdProvider;

public abstract class ClassGenerator
{
    private final Map<String, Class<?>> cache = new WeakHashMap<String, Class<?>>();

    protected ClassLoader               loader = ClassLoader.getSystemClassLoader();

    protected IdProvider                idProvider = new EncodedIdProvider(Algo.SHA1);

    // protected IdProvider idProvider = new HashCodeIdProvider();

    protected abstract void compile(String source, String id);

    protected String createSource(Class<?> interfaze, String source, ClassType projectionType, String[] names, Type[] types, String id, Map<String, Object> constants) throws IOException
    {
        // create source
        StringWriter writer = new StringWriter();
        JavaWriter javaw = new JavaWriter(writer);
        SimpleType idType = new SimpleType(id, "", id);
        Type[] parameterTypes = new Type[types.length + 1];
        for (int i = 0; i < types.length; i++)
        {
            parameterTypes[i] = types[i];
        }
        parameterTypes[parameterTypes.length - 1] = projectionType;
        javaw.beginClass(idType, null, new ClassType(interfaze, parameterTypes));
        Parameter[] params = new Parameter[names.length + constants.size()];
        for (int i = 0; i < names.length; i++)
        {
            params[i] = new Parameter(names[i], types[i]);
        }
        int i = names.length;
        for (Map.Entry<String, Object> entry : constants.entrySet())
        {
            Type type = new ClassType(TypeCategory.SIMPLE, ClassUtils.normalize(entry.getValue().getClass()));
            params[i++] = new Parameter(entry.getKey(), type);
        }
        Method method = interfaze.getMethods()[0];
        javaw.beginPublicMethod(projectionType, method.getName(), params);
        javaw.append(source);
        javaw.end();
        javaw.end();
        return writer.toString();
    }

    public <INTERFACE, CLASS extends INTERFACE> Class<CLASS> createPseudoLambda(Class<INTERFACE> interfaze, String source, Class<?> projectionType, String[] names, Class<?>[] classes, Map<String, Object> constants)
    {
        Type[] types = new Type[classes.length];
        for (int i = 0; i < types.length; i++)
        {
            types[i] = new ClassType(TypeCategory.SIMPLE, classes[i]);
        }
        return createPseudoLambda(interfaze, source, new ClassType(TypeCategory.SIMPLE, projectionType), names, types, classes, constants);
    }

    @SuppressWarnings("unchecked")
    public <INTERFACE, CLASS extends INTERFACE> Class<CLASS> createPseudoLambda(Class<INTERFACE> interfaze, String source, ClassType projection, String[] names, Type[] types, Class<?>[] classes, Map<String, Object> constants)
    {
        try
        {
            Collection<Class<?>> interfaces = new ArrayList<Class<?>>(Collections.singleton(interfaze));
            final String id = idProvider.toId(source, projection.getJavaClass(), types, constants.values(), interfaces);
            System.err.println(id);
            Class<CLASS> clazz = (Class<CLASS>) cache.get(id);

            if (clazz == null)
            {
                try
                {
                    clazz = (Class<CLASS>) loader.loadClass(id);
                }
                catch (ClassNotFoundException e)
                {
                    // create source
                    source = createSource(interfaze, source, projection, names, types, id, constants);
                    compile(source, id);
                    // reload
                    clazz = (Class<CLASS>) loader.loadClass(id);
                }
                // /method = findEvalMethod(clazz);
                cache.put(id, clazz);
            }
            return clazz;
        }
        catch (ClassNotFoundException e)
        {
            throw new CodegenException(e);
        }
        catch (SecurityException e)
        {
            throw new CodegenException(e);
        }
        catch (IOException e)
        {
            throw new CodegenException(e);
        }
    }

}
