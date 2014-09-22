package fr.herman.metatype.model;

import fr.herman.metatype.model.method.Getter;
import fr.herman.metatype.model.method.Setter;
import fr.herman.metatype.utils.Getters;

public abstract class MetaClassNode<ROOT, CURRENT, VALUE> implements GetterNode<ROOT, CURRENT, VALUE>
{
    private final String         name;
    protected final Class<VALUE> type;

    private MetaClassNode(Class<VALUE> type, String name)
    {
        this.type = type;
        this.name = name;
    }

    @Override
    public Getter<ROOT, VALUE> getter()
    {
        return this;
    }

    @Override
    public String name()
    {
        return name;
    }

    @Override
    public Class<VALUE> type()
    {
        return type;
    }

    private static class RootNode<CLASS> extends MetaClassNode<CLASS, CLASS, CLASS>
    {

        private RootNode(Class<CLASS> type, String name)
        {
            super(type, name);
        }

        @Override
        public Class<CLASS> rootType()
        {
            return type;
        }


        @Override
        public CLASS getValue(CLASS o)
        {
            return o;
        }
    }

    public static final <CLASS> RootNode<CLASS> root(Class<CLASS> type, String name)
    {
        return new RootNode<CLASS>(type, name);
    }

    public static final <CLASS> RootNode<CLASS> root(Class<CLASS> type)
    {
        return root(type, "this");
    }

    public static class PropertyNode<ROOT, CURRENT, VALUE> extends MetaClassNode<ROOT, CURRENT, VALUE>
    {
        protected final GetterNode<ROOT, ?, CURRENT> parent;
        private final Getter<CURRENT, VALUE>       getter;

        public PropertyNode(GetterNode<ROOT, ?, CURRENT> parent, Class<VALUE> type, String name, Getter<CURRENT, VALUE> getter)
        {
            super(type, name);
            this.parent = parent;
            this.getter = getter;
        }

        @Override
        public Class<ROOT> rootType()
        {
            return parent.rootType();
        }

        @Override
        public VALUE getValue(ROOT o)
        {
            return Getters.get(parent, getter, o);
        }
    }

    public static class PropertySetterNode<ROOT, CURRENT, VALUE> extends PropertyNode<ROOT, CURRENT, VALUE> implements Setter<ROOT, VALUE>
    {
        protected final Setter<CURRENT, VALUE> setter;

        public PropertySetterNode(GetterNode<ROOT, ?, CURRENT> parent, Class<VALUE> type, String name, Getter<CURRENT, VALUE> getter, Setter<CURRENT, VALUE> setter)
        {
            super(parent, type, name, getter);
            this.setter = setter;
        }

        @Override
        public void setValue(ROOT object, VALUE value)
        {
            setter.setValue(parent.getValue(object), value);
        }

    }

    public static final <ROOT, CURRENT, VALUE> PropertyNode<ROOT, CURRENT, VALUE> property(GetterNode<ROOT, ?, CURRENT> parent, Class<VALUE> type, String name, Getter<CURRENT, VALUE> getter)
    {
        return new PropertyNode<ROOT, CURRENT, VALUE>(parent, type, name, getter);
    }

    public static final <ROOT, CURRENT, VALUE> PropertySetterNode<ROOT, CURRENT, VALUE> property(GetterNode<ROOT, ?, CURRENT> parent, Class<VALUE> type, String name, Getter<CURRENT, VALUE> getter, Setter<CURRENT, VALUE> setter)
    {
        return new PropertySetterNode<ROOT, CURRENT, VALUE>(parent, type, name, getter, setter);
    }
}
