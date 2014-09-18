package fr.herman.metatype.model;

import fr.herman.metatype.model.method.Getter;

public class MetaClassTemplate<ROOT, CURRENT, VALUE> implements MetaClass<ROOT, CURRENT, VALUE>
{
    private final MetaClassNode<ROOT, CURRENT, VALUE> node;


    public MetaClassTemplate(MetaClassNode<ROOT, CURRENT, VALUE> node)
    {
        this.node = node;
    }

    @Override
    public Class<VALUE> type()
    {
        return node.type();
    }

    @Override
    public Class<ROOT> rootType()
    {
        return node.rootType();
    }

    @Override
    public String name()
    {
        return node.name();
    }

    @Override
    public Getter<ROOT, VALUE> getter()
    {
        return this;
    }

    @Override
    public VALUE getValue(ROOT o)
    {
        return node.getValue(o);
    }


}
