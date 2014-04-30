package fr.herman.metatype.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class MethodWrapper
{

    private static final String     GETTER_FORMAT           = "(get|is)(.*)";
    private static final Pattern    GETTER_PATTERN          = Pattern.compile(GETTER_FORMAT);
    private static final String     SETTER_FORMAT           = "set(.*)";
    private static final Pattern    SETTER_PATTERN          = Pattern.compile(SETTER_FORMAT);
    private final ExecutableElement method;
    boolean                         ignoreMissingProperties = false;
    private String                  fieldName;

    protected final Context         context;

    public MethodWrapper(Context context,ExecutableElement method)
    {
        this.method = method;
        this.context=context;
    }

    public TypeMirror firstParameterType()
    {

        if (method.getParameters().size() > 0)
        {
            return method.getParameters().get(0).asType();
        }
        else
        {
            return null;
        }
    }

    public TypeMirror returnType()
    {
        return method.getReturnType();
    }

    public String getSimpleName()
    {
        return method.getSimpleName().toString();
    }

    public ExecutableElement element()
    {
        return method;
    }

    public boolean hasOneParameter()
    {
        return method.getParameters().size() == 1;
    }

    public int parameterCount()
    {
        return method.getParameters().size();
    }

    public boolean hasReturnType()
    {

        return method.getReturnType() != null && method.getReturnType().getKind() != TypeKind.VOID;
    }

    public boolean isGetter()
    {
        boolean res = false;
        if (method.getParameters().size() == 0 && method.getReturnType().getKind() != TypeKind.VOID && method.getModifiers().contains(Modifier.PUBLIC))
        {
            Matcher getterMatcher = GETTER_PATTERN.matcher(method.getSimpleName());
            res = getterMatcher.matches();
            if (res)
            {
                fieldName = getterMatcher.group(2);
            }
        }
        return res;
    }

    public boolean isSetter()
    {
        boolean res = false;
        if (method.getParameters().size() == 1 && method.getReturnType().getKind() == TypeKind.VOID && method.getModifiers().contains(Modifier.PUBLIC))
        {
            Matcher setterMatcher = SETTER_PATTERN.matcher(method.getSimpleName());
            res = setterMatcher.matches();
            if (res)
            {
                fieldName = setterMatcher.group(1);
            }
        }
        return res;
    }

    public String getFieldName()
    {
        if (isGetter() || isSetter())
        {
            return fieldName;
        }
        return null;
    }

    public boolean hasAnnotation(String annotation)
    {
        boolean res = false;
        for (AnnotationMirror annotationMirror : method.getAnnotationMirrors())
        {
            if (annotationMirror.getAnnotationType().toString().equals(annotation))
            {
                res = true;
                break;
            }
        }
        return res;

    }

    public boolean hasAnnotation(Class<?> annotation)
    {
        return hasAnnotation(annotation.getCanonicalName());
    }
}
