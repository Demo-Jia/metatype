package fr.herman.metatype.processor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import lombok.Data;

public class BeanWrapper
{

    private static final Set<String> exclusions = new TreeSet<String>(Arrays.asList("getClass"));

    final Context                    context;
    final TypeElement                typeElement;
    final Map<String, FieldItem>     fieldsGraph;

    public BeanWrapper(Context context, TypeElement typeElement)
    {
        this.context = context;
        this.typeElement = typeElement;

        fieldsGraph = buildFieldGraph();
    }

    private Map<String, FieldItem> buildFieldGraph()
    {
        final HashMap<String, FieldItem> result = new HashMap<String, FieldItem>();
        final List<ExecutableElement> methods = ElementFilter.methodsIn(typeElement.getEnclosedElements());

        // looping around all methods
        for (ExecutableElement method : methods)
        {
            if (exclusions.contains(method.getSimpleName().toString()))
            {
                continue;
            }
            MethodWrapper methodWrapper = new MethodWrapper(context, method);

            if (methodWrapper.isGetter())
            {
                putGetterField(methodWrapper, result);
            }
            else if (methodWrapper.isSetter())
            {
                putSetterField(methodWrapper, result);
            }

        }

        return result;
    }

    public TypeMirror getType()
    {
        return typeElement.asType();
    }

    public PackageElement getPackage()
    {
        Element type = typeElement;
        while (type.getKind() != ElementKind.PACKAGE)
        {
            type = type.getEnclosingElement();
        }
        return (PackageElement) type;
    }

    private void putSetterField(MethodWrapper methodWrapper, HashMap<String, FieldItem> result)
    {

        String field = methodWrapper.getFieldName();
        FieldItem item = result.get(field);
        if (item != null)
        {
            item = new FieldItem(field, methodWrapper, item.getter);
        }
        else
        {
            item = new FieldItem(field, methodWrapper, null);
        }
        result.put(field, item);
    }

    private void putGetterField(MethodWrapper methodWrapper, HashMap<String, FieldItem> result)
    {

        String field = methodWrapper.getFieldName();
        FieldItem item = result.get(field);
        if (item != null)
        {
            item = new FieldItem(item.field, item.setter, methodWrapper);
        }
        else
        {
            item = new FieldItem(field, null, methodWrapper);
        }

        result.put(field, item);
    }

    public Iterable<? extends String> getFields()
    {
        return fieldsGraph.keySet();
    }

    public boolean hasFieldAndSetter(String field)
    {
        boolean res = false;
        FieldItem item = fieldsGraph.get(field);

        if (item != null && item.setter != null)
        {
            res = true;
        }

        return res;
    }

    public MethodWrapper getSetterFor(String field)
    {
        FieldItem item = fieldsGraph.get(field);

        if (item != null)
        {
            return item.setter;
        }
        return null;
    }

    public String getOutSetterPathFor(String field)
    {
        return String.format("out.%s", getSetterFor(field));
    }

    public MethodWrapper getGetterFor(String field)
    {
        FieldItem item = fieldsGraph.get(field);

        if (item != null)
        {
            return item.getter;
        }
        return null;
    }

    public TypeMirror getTypeFor(String field)
    {
        TypeMirror result = null;
        FieldItem item = fieldsGraph.get(field);

        if (item != null)
        {
            if (item.getter != null)
            {
                result = item.getter.returnType();
            }
            else
            {
                result = item.setter.firstParameterType();
            }
        }

        return result;
    }

    public Element getFieldElement(String field)
    {
        return fieldsGraph.get(field).getter.element();
    }

    public Set<String> getSetterFields()
    {
        Set<String> res = new TreeSet<String>();
        for (String s : fieldsGraph.keySet())
        {
            if (fieldsGraph.get(s).setter != null)
            {
                res.add(s);
            }
        }
        return res;
    }

    public Element getSetterElement(String field)
    {

        return fieldsGraph.get(field).setter.element();
    }

    public TypeMirror getFieldType(String field)
    {
        return fieldsGraph.get(field).getType();
    }

    @Data
    class FieldItem
    {
        private final String        field;
        private final MethodWrapper setter;
        private final MethodWrapper getter;

        FieldItem(String field, MethodWrapper setter, MethodWrapper getter)
        {
            this.field = field;
            this.setter = setter;
            this.getter = getter;
        }

        public TypeMirror getType()
        {
            return getter != null ? getter.returnType() : setter.firstParameterType();
        }
    }

}