package fr.herman.metatype.model;


public interface MetaProperty<ROOT, CURRENT, VALUE> extends Node<ROOT, CURRENT, VALUE>
{
    Class<CURRENT> modelType();


}
