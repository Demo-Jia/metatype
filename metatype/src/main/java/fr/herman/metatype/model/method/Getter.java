package fr.herman.metatype.model.method;

public interface Getter<O, V> {
    V getValue(O o);
}
