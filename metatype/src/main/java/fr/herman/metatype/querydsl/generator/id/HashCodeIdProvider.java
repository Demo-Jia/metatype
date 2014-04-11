package fr.herman.metatype.querydsl.generator.id;

import java.util.Collection;
import com.mysema.codegen.model.Type;

public class HashCodeIdProvider implements IdProvider
{
    @Override
    public String toId(String source, Class<?> returnType, Type[] types, Collection<Object> constants, Collection<Class<?>> interfaces)
    {
        StringBuilder b = new StringBuilder(128);
        b.append("Q");
        b.append("_").append(source.hashCode());
        b.append("_").append(returnType.getName().hashCode());
        for (Type type : types)
        {
            b.append("_").append(type.getFullName().hashCode());
        }
        for (Object constant : constants)
        {
            b.append("_").append(constant.getClass().getName().hashCode());
        }
        for (Class<?> clazz : interfaces)
        {
            b.append("_").append(clazz.getName().hashCode());
        }
        return b.toString().replace('-', '0');
    }
}
