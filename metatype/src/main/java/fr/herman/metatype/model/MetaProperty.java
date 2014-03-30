package fr.herman.metatype.model;


public interface MetaProperty<O, T> {
    Class<T> type();

    String name();
}
