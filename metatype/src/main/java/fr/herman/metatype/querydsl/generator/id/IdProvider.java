package fr.herman.metatype.querydsl.generator.id;

import java.util.Collection;
import com.mysema.codegen.model.Type;

public interface IdProvider
{
    String toId(String source, Class<?> returnType, Type[] types, Collection<Object> constants, Collection<Class<?>> interfaces);
}
