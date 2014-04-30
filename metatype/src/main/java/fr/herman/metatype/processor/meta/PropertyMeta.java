package fr.herman.metatype.processor.meta;

import javax.lang.model.type.TypeMirror;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PropertyMeta
{
    TypeMirror type;
    String     name;
    GetterMeta getter;
    SetterMeta setter;
}
