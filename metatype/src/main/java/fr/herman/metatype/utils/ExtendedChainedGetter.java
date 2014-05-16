package fr.herman.metatype.utils;

import java.util.LinkedList;
import java.util.List;
import fr.herman.metatype.model.MetaProperty;
import fr.herman.metatype.model.method.Getter;
import fr.herman.metatype.model.method.HasGetter;

public class ExtendedChainedGetter<FROM, CURRENT, TO> implements Getter<FROM, TO>
{
    private static final class ExtendedGetter<O, E extends O, V> implements Getter<O, V>
    {
        private final Class<E>     clazz;
        private final Getter<E, V> getter;

        public ExtendedGetter(Getter<E, V> getter, Class<E> clazz)
        {
            this.getter = getter;
            this.clazz = clazz;
        }

        @Override
        @SuppressWarnings("unchecked")
        public V getValue(O o)
        {
            if (clazz.isAssignableFrom(o.getClass()))
            {
                return getter.getValue((E) o);
            }
            return null;
        }
    }

    private final List<Getter<?, ?>> getters;

    protected ExtendedChainedGetter()
    {
        this.getters = new LinkedList<Getter<?, ?>>();
    }

    protected ExtendedChainedGetter(List<Getter<?, ?>> getters)
    {
        this.getters = new LinkedList<Getter<?, ?>>(getters);
    }

    public static <O, V> ExtendedChainedGetter<O, O, V> from(Getter<O, V> getter)
    {
        return new ExtendedChainedGetter<O, O, V>().add(getter);
    }

    public <X, E extends TO, P extends HasGetter<E, X> & MetaProperty<E, X>> ExtendedChainedGetter<FROM, TO, X> to(P property)
    {
        return to(property.getter(), property.modelType());
    }

    public <E extends TO, X> ExtendedChainedGetter<FROM, TO, X> to(Getter<E, X> getter, Class<E> clazz)
    {
        return new ExtendedChainedGetter<FROM, TO, X>(getters).add(new ExtendedGetter<TO, E, X>(getter, clazz));
    }

    private ExtendedChainedGetter<FROM, CURRENT, TO> add(Getter<CURRENT, TO> getter)
    {
        getters.add(getter);
        return this;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public TO getValue(FROM o)
    {
        Object current = o;
        for (Getter getter : getters)
        {
            if (current == null)
            {
                return null;
            }
            current = getter.getValue(current);
        }
        return (TO) current;
    }
}