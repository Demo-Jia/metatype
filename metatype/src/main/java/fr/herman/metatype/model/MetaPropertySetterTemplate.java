package fr.herman.metatype.model;

import fr.herman.metatype.model.method.Setter;

public class MetaPropertySetterTemplate<ROOT, CURRENT, VALUE> implements MetaPropertySetter<ROOT, CURRENT, VALUE>
{

    private final MetaClass<ROOT, ?, CURRENT> parent;
    private final Class<VALUE>                type;
    private final String                      name;
    private final Setter<CURRENT, VALUE>      setter;

    public MetaPropertySetterTemplate(MetaClass<ROOT, ?, CURRENT> parent, Class<VALUE> type, String name, Setter<CURRENT, VALUE> setter)
    {
        this.parent = parent;
        this.type = type;
        this.name = name;
        this.setter = setter;
    }

    @Override
    public Class<CURRENT> modelType()
    {
        return parent.type();
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
    public String name()
    {
        return name;
    }

    @Override
    public Setter<ROOT, VALUE> setter()
    {
        return this;
    }

    @Override
    public void setValue(ROOT object, VALUE value)
    {
        setter.setValue(parent.getValue(object), value);
    }

}
