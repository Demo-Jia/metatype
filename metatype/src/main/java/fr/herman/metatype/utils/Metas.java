package fr.herman.metatype.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import fr.herman.metatype.model.MetaProperty;
import fr.herman.metatype.model.method.Getter;
import fr.herman.metatype.model.method.HasGetterSetter;
import fr.herman.metatype.model.method.Setter;

public final class Metas
{
    private static final double FACTOR = 0.75;

    private static class Counter
    {
        public Counter(int start)
        {
            i = start;
        }

        private int i;

        public void add()
        {
            ++i;
        }

        public int getCount()
        {
            return i;
        }
    }

    private Metas()
    {

    }

    /**
     * Collect all values of a specific property of object contained in collection</br>
     * @param input the collection which contains input objects
     * @param output the result of collecting properties
     * @param getter define the property to collect
     */
    public static <T, O> void collect(Collection<O> input, Collection<T> output, Getter<? super O, T> getter)
    {
        for (O object : input)
        {
            output.add(getter.getValue(object));
        }
    }

    /**
     * Collect property values in a collection
     * @see Metas#collect(Collection, Collection, Getter)
     * @param input the collection which contains input objects
     * @param getter define the property to collect
     * @return a collection of all values collected
     */
    public static <T, O> Collection<T> collect(Collection<O> input, Getter<? super O, T> getter)
    {
        List<T> output = new ArrayList<T>(input.size());
        collect(input, output, getter);
        return output;
    }

    /**
     * Collect all different values in a Collection (remove duplicated values)
     * @see Metas#collect(Collection, Collection, Getter)
     * @param input the collection which contains input objects
     * @param getter define the property to collect
     * @return a collection of distinct values
     */
    public static <T, O> Collection<T> distinct(Collection<O> input, Getter<? super O, T> getter)
    {
        return distinct(input, getter, (int) (input.size() * FACTOR));
    }

    /**
     * Collect all different values in a Collection (remove duplicated values)
     * @see Metas#collect(Collection, Collection, Getter)
     * @param input the collection which contains input objects
     * @param getter define the property to collect
     * @param estimatedSize specify the estimated size of the result
     * @return a collection of distinct values
     */
    public static <T, O> Collection<T> distinct(Collection<O> input, Getter<? super O, T> meta, int estimatedSize)
    {
        Set<T> output = new HashSet<T>(estimatedSize);
        collect(input, output, meta);
        return output;
    }

    /**
     * Collection all distinct values and count the number of occurrences
     * @param input the collection which contains input objects
     * @param getter define the property to collect
     * @return a map of value/number of occurrences
     */
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
                frequency.put(value, new Counter(1));
            }
        }
        Map<T, Integer> output = new HashMap<T, Integer>(frequency.size());
        for (Map.Entry<T, Counter> entry : frequency.entrySet())
        {
            output.put(entry.getKey(), Integer.valueOf(entry.getValue().getCount()));
        }
        return output;
    }

    /**
     * Transform a bean to a map of properties
     * @see #toMap(Object, Collection)
     * @param object the input object to map
     * @param metas the list of field to map
     * @return a map of property name/value
     */
    @SafeVarargs
    public static <O, P extends Getter<? super O, ?> & MetaProperty<? super O, ?, ?>> Map<String, ?> toMap(O object, P... metas)
    {
        return toMap(object, Arrays.asList(metas));
    }

    /**
     * Transform a bean to a map of properties
     * @param object the input object to map
     * @param metas the collection of field to map
     * @return a map of property name/value
     */
    public static <O, P extends Getter<? super O, ?> & MetaProperty<? super O, ?, ?>> Map<String, ?> toMap(O object, Collection<P> metas)
    {
        Map<String, Object> map = new HashMap<>(metas.size());
        for (P meta : metas)
        {
            map.put(meta.name(), meta.getValue(object));
        }
        return map;
    }

    /**
     * Apply all values corresponding to properties from an object to another
     * @param from the source object
     * @param to the target object
     * @param properties the list of properties to apply
     */
    @SafeVarargs
    public static <O> void copyValues(O from, O to, HasGetterSetter<? super O, ?>... properties)
    {
        if (from != null && to != null && properties != null)
        {
            for (HasGetterSetter<? super O, ?> property : properties)
            {
                copyValue(from, to, property);
            }
        }
    }

    /**
     * Copy value of a property from an object to another
     * @param from the source object
     * @param to the target object
     * @param property the property to copy
     */
    public static <O, V> void copyValue(O from, O to, HasGetterSetter<? super O, V> property)
    {
        copyValue(from, to, property.getter(), property.setter());
    }

    /**
     * Copy value of a property from an object to another
     * @param from the source object
     * @param to the target object
     * @param property the property to copy
     */
    public static <FROM, TO, V> void copyValue(FROM from, TO to, Getter<? super FROM, V> getter, Setter<? super TO, V> setter)
    {
        setter.setValue(to, getter.getValue(from));
    }

    /**
     * Default a value of a bean property if the value is null
     * @param property the property to default
     * @param object the input object
     * @param defaultValue the default value
     */
    public static <O, V, P extends Getter<? super O, V> & Setter<? super O, V>> void defaultValue(P property, O object, V defaultValue)
    {
        if (property.getValue(object) == null)
        {
            property.setValue(object, defaultValue);
        }
    }
}
