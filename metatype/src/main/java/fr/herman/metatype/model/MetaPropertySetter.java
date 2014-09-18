package fr.herman.metatype.model;

import fr.herman.metatype.model.method.HasSetter;
import fr.herman.metatype.model.method.Setter;

public interface MetaPropertySetter<ROOT, CURRENT, VALUE> extends MetaProperty<ROOT, CURRENT, VALUE>, HasSetter<ROOT, VALUE>, Setter<ROOT, VALUE>
{

}
