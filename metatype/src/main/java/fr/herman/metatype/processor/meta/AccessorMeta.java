package fr.herman.metatype.processor.meta;

import javax.lang.model.type.TypeMirror;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class AccessorMeta
{
    TypeMirror objectType, ValueType;
    String     delegateMethodName;
}
