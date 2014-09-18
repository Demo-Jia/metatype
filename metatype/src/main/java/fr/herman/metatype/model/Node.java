package fr.herman.metatype.model;


public interface Node<ROOT, CURRENT, VALUE>
{
    Class<VALUE> type();

    Class<ROOT> rootType();

    String name();
}
