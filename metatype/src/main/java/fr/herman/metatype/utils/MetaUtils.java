package fr.herman.metatype.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import fr.herman.metatype.model.MetaProperty;
import fr.herman.metatype.model.method.Getter;
import fr.herman.metatype.model.method.HasGetter;
import fr.herman.metatype.model.method.HasSetter;
import fr.herman.metatype.model.method.Setter;

public final class MetaUtils
{
    private static final double FACTOR = 0.75;

    private static class Counter
    {
        private int i = 0;

        public void add()
        {
            ++i;
        }

        public int getCount()
        {
            return i;
        }
    }

    private MetaUtils()
    {

    }

    public static <T, O> Collection<T> collect(Collection<O> input, Collection<T> output, HasGetter<O, T> meta)
    {
        return collect(input, output, meta.getter());
    }

    public static <T, O> Collection<T> collect(Collection<O> input, Collection<T> output, Getter<O, T> getter)
    {
        for (O object : input)
        {
            output.add(getter.getValue(object));
        }
        return output;
    }

    public static <T, O> Collection<T> collect(Collection<O> input, HasGetter<O, T> meta)
    {
        return collect(input, new ArrayList<T>(input.size()), meta);
    }

    public static <T, O> Collection<T> collectSet(Collection<O> input, HasGetter<O, T> meta)
    {
        return collect(input, new HashSet<T>((int) (input.size() * FACTOR)), meta);
    }

    public static <T, O> Map<T, Integer> frequency(Collection<O> input, HasGetter<O, T> meta)
    {
        Map<T, Counter> frequency = new HashMap<T, Counter>((int) (input.size() * FACTOR));
        Getter<O, T> getter = meta.getter();
        for (O object : input)
        {
            T value = getter.getValue(object);
            Counter counter = frequency.get(value);
            if (counter != null)
            {
                counter.add();
            }
            else
            {
                frequency.put(value, new Counter());
            }
        }
        Map<T, Integer> output = new HashMap<T, Integer>(frequency.size());
        for (Map.Entry<T, Counter> entry : frequency.entrySet())
        {
            output.put(entry.getKey(), Integer.valueOf(entry.getValue().getCount()));
        }
        return output;
    }

    public static <T, O, P extends HasGetter<O, T> & MetaProperty<O, T>> Map<String, T> toMap(O object, Collection<P> metas)
    {
        Map<String, T> map = new HashMap<String, T>(metas.size());
        for (P meta : metas)
        {
            map.put(meta.name(), meta.getter().getValue(object));
        }
        return map;
    }

    @SafeVarargs
    public static <O, P extends HasGetter<O, ?> & HasSetter<O, ?>> void applyTo(O from, O to, P... properties)
    {
        if (from != null && to != null && properties != null)
        {
            for (P property : properties)
            {

                Object value = property.getter().getValue(from);
                Setter<O, Object> setter = (Setter<O, Object>) property.setter();
                setter.setValue(to, value);
            }
        }
    }
}
