package fr.herman.metatype.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Retention(RetentionPolicy.CLASS)
public @interface MetaGetter
{
    String value();
}
