package org.jannocessor.config;

import org.jannocessor.model.structure.JavaClass;
import org.jannocessor.processor.annotation.Annotated;
import org.jannocessor.processor.annotation.Types;

import fr.herman.metatype.annotation.Bean;
import fr.herman.metatype.codegen.MetaTypeGenerator;

public class Processors {

    @Annotated(Bean.class)
    @Types(JavaClass.class)
    public MetaTypeGenerator metaTypeGenerator() {
        return new MetaTypeGenerator("fr.herman.meta");
    }
}
