package fr.herman.metatype.processor.meta;

import static java.lang.String.format;
import java.util.List;
import javax.lang.model.type.TypeMirror;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassMeta
{
    TypeMirror         originalType;

    ClassMeta          superType;

    List<PropertyMeta> properties;

    String             simpleName;

    String             packageName;

    public String getCanonicalName()
    {
        return format("%s.%s", packageName, simpleName);
    }

}
