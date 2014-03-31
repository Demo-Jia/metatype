package fr.herman.metatype.model.method;

public interface Setter<O, V> {
    void setValue(O object, V value);
}
