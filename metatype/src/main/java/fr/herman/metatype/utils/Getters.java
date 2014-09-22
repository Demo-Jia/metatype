package fr.herman.metatype.utils;

import fr.herman.metatype.model.method.Getter;

public class Getters
{
    public static final <FROM, CURRENT, TO> TO get(Getter<FROM, CURRENT> parent, Getter<CURRENT, TO> current, FROM from)
    {
        CURRENT curVal = parent.getValue(from);
        return curVal != null ? current.getValue(curVal) : null;
    }
}
