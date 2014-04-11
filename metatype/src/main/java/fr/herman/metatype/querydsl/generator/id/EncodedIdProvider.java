package fr.herman.metatype.querydsl.generator.id;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import com.mysema.codegen.model.Type;

public class EncodedIdProvider implements IdProvider
{
    public static enum Algo
    {
        MD5, SHA1;
    }

    private final MessageDigest digester;

    public EncodedIdProvider(Algo algo)
    {
        try
        {
            digester = MessageDigest.getInstance(algo.name());
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toId(String source, Class<?> returnType, Type[] types, Collection<Object> constants, Collection<Class<?>> interfaces)
    {
        StringBuilder b = new StringBuilder(1024);
        b.append(source.hashCode());
        b.append("_").append(returnType.getName());
        for (Type type : types)
        {
            b.append("_").append(type.getFullName());
        }
        for (Object constant : constants)
        {
            b.append("_").append(constant.getClass().getName());
        }
        for (Class<?> clazz : interfaces)
        {
            b.append("_").append(clazz.getName());
        }
        byte[] bytes = digester.digest(b.toString().getBytes());
        StringBuilder sb = new StringBuilder(42);
        sb.append("Q_");
        for (byte c : bytes)
        {
            sb.append(Integer.toString((c & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}
