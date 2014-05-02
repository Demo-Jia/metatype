package fr.herman.metatype.model;


public interface MetaProperty<O, T> {
    Class<?> type();

    Class<O> modelType();

    String name();
}
