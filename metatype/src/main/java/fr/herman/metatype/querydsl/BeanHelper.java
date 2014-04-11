package fr.herman.metatype.querydsl;

import java.util.Collection;
import com.mysema.query.collections.CollQueryTemplates;
import com.mysema.query.types.Expression;
import com.mysema.query.types.Path;
import fr.herman.metatype.model.method.Getter;

public class BeanHelper
{

    private static final CodeFactory             CODE_FACTORY     = new CodeFactory(CollQueryTemplates.DEFAULT);


    @SuppressWarnings("unchecked")
    public static <BEAN, FIELD, PATH extends Expression<FIELD> & Path<FIELD>> Getter<BEAN, FIELD> getter(Path<BEAN> root, PATH path)
    {
        return CODE_FACTORY.createLambda(Getter.class, root, path);
    }

    public static <BEAN, FIELD, PATH extends Expression<FIELD> & Path<FIELD>> Collection<FIELD> collect(Collection<BEAN> input, Collection<FIELD> output, Path<BEAN> root, PATH path)
    {
        return collect(input, output, getter(root, path));
    }

    public static <T, O> Collection<T> collect(Collection<O> input, Collection<T> output, Getter<O, T> getter)
    {
        for (O object : input)
        {
            output.add(getter.getValue(object));
        }
        return output;
    }
}
