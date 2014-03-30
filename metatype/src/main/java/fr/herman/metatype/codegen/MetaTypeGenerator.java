package fr.herman.metatype.codegen;

import org.jannocessor.collection.api.PowerList;
import org.jannocessor.extra.processor.AbstractGenerator;
import org.jannocessor.model.executable.JavaMethod;
import org.jannocessor.model.structure.AbstractJavaClass;
import org.jannocessor.model.structure.JavaClass;
import org.jannocessor.model.structure.JavaNestedClass;
import org.jannocessor.model.type.JavaType;
import org.jannocessor.model.util.Classes;
import org.jannocessor.model.util.Fields;
import org.jannocessor.model.util.Methods;
import org.jannocessor.model.util.NestedClasses;
import org.jannocessor.model.util.New;
import org.jannocessor.model.variable.JavaField;
import org.jannocessor.processor.api.ProcessingContext;

import fr.herman.metatype.model.MetaProperty;

public class MetaTypeGenerator extends AbstractGenerator<AbstractJavaClass> {

    public MetaTypeGenerator(String destPackage) {
        super(destPackage, false);
    }

    @Override
    protected void generateCodeFrom(PowerList<AbstractJavaClass> models,
            ProcessingContext context) {
        context.getLogger().info("Start to generate MetaModel");
        for (AbstractJavaClass model : models) {
            String metaClassName = model.getName().toString() + "MetaType";
            JavaClass metaClass = New.classs(Classes.PUBLIC, metaClassName);

            for (JavaField property : model.getFields()) {
                String propertyName = property.getName().toString();
                String propertyMetaClassName = propertyName + "MetaProperty";
                JavaNestedClass propertyMetaClass = New.nestedClass(
                        NestedClasses.PUBLIC_FINAL_STATIC,
                        propertyMetaClassName);
                JavaType metaPropertyType = type(MetaProperty.class, model
                        .getType().getTypeClass(), property.getType()
                        .getTypeClass());
                propertyMetaClass.getInterfaces().add(metaPropertyType);
                metaClass.getNestedClasses().add(propertyMetaClass);

                addMetaNameProperty(propertyMetaClass, propertyName);

                addMetaTypeProperty(propertyMetaClass, property);

                JavaField field = New.field(Fields.PUBLIC_STATIC_FINAL,
                        metaPropertyType, propertyName);
                field.getValue()
                .setHardcoded("new %s()", propertyMetaClassName);
                // add the fields to the bean
                metaClass.getFields().add(field);
            }

            // generate the JavaBean class (e.g. Person)
            generate(metaClass);
        }

    }

    private void addMetaTypeProperty(JavaNestedClass propertyMetaClass,
            JavaField property) {
        JavaMethod typePropertyMethod = New.method(Methods.PUBLIC,
                type(Class.class, property.getType().getTypeClass()), "type");
        typePropertyMethod.getMetadata().add(New.metadata(Override.class));
        typePropertyMethod.getBody().setHardcoded("return %s.class;",
                property.getType().getSimpleName());
        propertyMetaClass.getMethods().add(typePropertyMethod);
    }

    private void addMetaNameProperty(JavaNestedClass propertyMetaClass,
            String propertyName) {
        JavaField namePropertyField = New.field(Fields.PUBLIC_STATIC_FINAL,
                String.class, "NAME");
        namePropertyField.getValue().setHardcoded("\"%s\"", propertyName);
        propertyMetaClass.getFields().add(namePropertyField);

        JavaMethod namePropertyMethod = New.method(Methods.PUBLIC,
                String.class, "name");
        namePropertyMethod.getMetadata().add(New.metadata(Override.class));
        namePropertyMethod.getBody().setHardcoded("return %s;",
                namePropertyField.getName());
        propertyMetaClass.getMethods().add(namePropertyMethod);
    }

    private static JavaType type(Class<?> clazz, Class<?>... params) {
        StringBuilder sb = new StringBuilder(clazz.getCanonicalName());
        if (params != null && params.length > 0) {
            sb.append('<');
            int i = 0;
            for (Class<?> param : params) {
                sb.append(param.getCanonicalName());
                if (++i < params.length) {
                    sb.append(',');
                }
            }
            sb.append('>');
        }
        return New.type(sb.toString());
    }
}
