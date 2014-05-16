package fr.herman.metatype.utils;

import java.util.LinkedList;
import java.util.List;
import fr.herman.metatype.model.method.Getter;

public class ChainedGetter<FROM, CURRENT, TO> implements Getter<FROM, TO>
{
    private final List<Getter<?, ?>> getters;

    protected ChainedGetter()
    {
        this.getters = new LinkedList<Getter<?, ?>>();
    }

    protected ChainedGetter(List<Getter<?, ?>> getters)
    {
        this.getters = new LinkedList<Getter<?, ?>>(getters);
    }

    public static <O, V> ChainedGetter<O, O, V> from(Getter<O, V> getter)
    {
        return new ChainedGetter<O, O, V>().add(getter);
    }

    public <X> ChainedGetter<FROM, TO, X> to(Getter<TO, X> getter)
    {
        return new ChainedGetter<FROM, TO, X>(getters).add(getter);
    }

    private ChainedGetter<FROM, CURRENT, TO> add(Getter<CURRENT, TO> getter)
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
