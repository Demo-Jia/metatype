package fr.herman.metatype.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import fr.herman.metatype.annotation.MetaIgnore;
import fr.herman.metatype.annotation.MetaGetter;
import fr.herman.metatype.annotation.MetaSetter;

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

    public MethodWrapper(Context context, ExecutableElement method)
    {
        this.method = method;
        this.context = context;
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
        if (method.getParameters().size() == 0 && method.getReturnType().getKind() != TypeKind.VOID && method.getModifiers().contains(Modifier.PUBLIC) && method.getAnnotation(MetaIgnore.class) == null)
        {
            MetaGetter metaGetter = method.getAnnotation(MetaGetter.class);
            if (metaGetter != null)
            {
                res = true;
                fieldName = metaGetter.value();
            }
            else
            {
                Matcher getterMatcher = GETTER_PATTERN.matcher(method.getSimpleName());
                res = getterMatcher.matches();
                if (res)
                {
                    fieldName = WordUtils.unCapitalize(getterMatcher.group(2));
                }
            }
        }
        return res;
    }

    public boolean isSetter()
    {
        boolean res = false;
        if (method.getParameters().size() == 1 && method.getModifiers().contains(Modifier.PUBLIC) && method.getAnnotation(MetaIgnore.class) == null)
        {
            MetaSetter metaSetter = method.getAnnotation(MetaSetter.class);
            if (metaSetter != null)
            {
                res = true;
                fieldName = metaSetter.value();
            }
            else
            {
                Matcher setterMatcher = SETTER_PATTERN.matcher(method.getSimpleName());
                res = setterMatcher.matches();
                if (res && method.getReturnType().getKind() == TypeKind.VOID)
                {
                    fieldName = WordUtils.unCapitalize(setterMatcher.group(1));
                }
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
}
