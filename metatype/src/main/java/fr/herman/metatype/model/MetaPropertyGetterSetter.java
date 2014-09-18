package fr.herman.metatype.model;

import fr.herman.metatype.model.method.HasGetterSetter;

public interface MetaPropertyGetterSetter<ROOT, CURRENT, VALUE> extends MetaPropertyGetter<ROOT, CURRENT, VALUE>, MetaPropertySetter<ROOT, CURRENT, VALUE>, HasGetterSetter<ROOT, VALUE>
{

}
