package fr.herman.metatype.model;

import fr.herman.metatype.model.method.Getter;
import fr.herman.metatype.model.method.HasGetter;

public interface GetterNode<ROOT, CURRENT, VALUE> extends Node<ROOT, CURRENT, VALUE>, HasGetter<ROOT, VALUE>, Getter<ROOT, VALUE>
{

}
