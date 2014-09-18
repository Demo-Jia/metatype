package fr.herman.metatype.model;

import fr.herman.metatype.model.method.Getter;

public class MetaPropertyGetterTemplate<ROOT, CURRENT, VALUE> implements MetaPropertyGetter<ROOT, CURRENT, VALUE>
{
    private final MetaClass<ROOT, ?, CURRENT> parent;
    private final Class<VALUE>                type;
    private final String                      name;
    private final Getter<CURRENT, VALUE>      getter;

    public MetaPropertyGetterTemplate(MetaClass<ROOT, ?, CURRENT> parent, Class<VALUE> type, String name, Getter<CURRENT, VALUE> getter)
    {
        this.parent = parent;
        this.type = type;
        this.name = name;
        this.getter = getter;
    }

    @Override
    public Class<VALUE> type()
    {
        return type;
    }

    @Override
    public Class<ROOT> rootType()
    {
        return parent.rootType();
    }

    @Override
    public VALUE getValue(ROOT o)
    {
        return getter.getValue(parent.getValue(o));
    }

    @Override
    public Getter<ROOT, VALUE> getter()
    {
        return this;
    }

    @Override
    public Class<CURRENT> modelType()
    {
        return parent.type();
    }

    @Override
    public String name()
    {
        return name;
    }

}
