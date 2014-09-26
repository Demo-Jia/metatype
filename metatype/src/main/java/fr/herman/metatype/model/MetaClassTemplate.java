package fr.herman.metatype.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import fr.herman.metatype.model.method.Getter;

public class MetaClassTemplate<ROOT, CURRENT, VALUE> implements MetaClass<ROOT, CURRENT, VALUE>, MetaProperty<ROOT, CURRENT, VALUE>
{
    private final MetaClassNode<ROOT, CURRENT, VALUE> node;

    private final Map<String, MetaProperty<ROOT, VALUE, ?>> properties = new HashMap<>();

    public MetaClassTemplate(MetaClassNode<ROOT, CURRENT, VALUE> node)
    {
        this.node = node;
        node.addProperty(this);
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

    void addProperty(MetaProperty<ROOT, VALUE, ?> property)
    {
        properties.put(property.name(), property);
    }

    public Collection<MetaProperty<ROOT, VALUE, ?>> properties()
    {
        return Collections.unmodifiableCollection(properties.values());
    }

    @Override
    public Class<CURRENT> modelType()
    {
        return node.modelType();
    }

    @Override
    public boolean hasGetter()
    {
        return node.hasGetter();
    }

    @Override
    public boolean hasSetter()
    {
        return node.hasSetter();
    }

}
