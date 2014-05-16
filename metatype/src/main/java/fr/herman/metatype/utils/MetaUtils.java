package fr.herman.metatype.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import fr.herman.metatype.model.MetaProperty;
import fr.herman.metatype.model.method.Getter;
import fr.herman.metatype.model.method.HasGetterSetter;
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


    public static <T, O> Collection<T> collect(Collection<O> input, Collection<T> output, Getter<? super O, T> getter)
    {
        for (O object : input)
        {
            output.add(getter.getValue(object));
        }
        return output;
    }

    public static <T, O> Collection<T> collect(Collection<O> input, Getter<? super O, T> meta)
    {
        return collect(input, new ArrayList<T>(input.size()), meta);
    }

    public static <T, O> Collection<T> distinct(Collection<O> input, Getter<? super O, T> meta)
    {
        return distinct(input, meta, (int) (input.size() * FACTOR));
    }

    public static <T, O> Collection<T> distinct(Collection<O> input, Getter<? super O, T> meta, int estimatedSize)
    {
        return collect(input, new HashSet<T>(estimatedSize), meta);
    }

    public static <T, O> Map<T, Integer> frequency(Collection<O> input, Getter<? super O, T> getter)
    {
        Map<T, Counter> frequency = new HashMap<T, Counter>((int) (input.size() * FACTOR));
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

    public static <T, O, P extends Getter<? super O, T> & MetaProperty<? super O, T>> Map<String, T> toMap(O object, Collection<P> metas)
    {
        Map<String, T> map = new HashMap<String, T>(metas.size());
        for (P meta : metas)
        {
            map.put(meta.name(), meta.getValue(object));
        }
        return map;
    }

    @SafeVarargs
    public static <O> void applyTo(O from, O to, HasGetterSetter<? super O, ?>... properties)
    {
        if (from != null && to != null && properties != null)
        {
            for (HasGetterSetter<? super O, ?> property : properties)
            {
                copyValue(from, to, property);
            }
        }
    }

    public static <O, V> void copyValue(O from, O to, HasGetterSetter<? super O, V> property)
    {
        copyValue(from, to, property.getter(), property.setter());
    }

    public static <FROM, TO, V> void copyValue(FROM from, TO to, Getter<? super FROM, V> getter, Setter<? super TO, V> setter)
    {
        setter.setValue(to, getter.getValue(from));
    }
}
