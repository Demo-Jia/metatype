package fr.herman.metatype.model;

public class NodeTemplate<ROOT, CURRENT, VALUE> implements Node<ROOT, CURRENT, VALUE>
{

    private final GetterNode<ROOT, ?, CURRENT> parent;
    private final Class<VALUE>                 type;
    private final String                       name;

    public NodeTemplate(GetterNode<ROOT, ?, CURRENT> parent, Class<VALUE> type, String name)
    {
        this.parent = parent;
        this.type = type;
        this.name = name;
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

    protected CURRENT getParentValue(ROOT root)
    {
        return parent.getValue(root);
    }
}
