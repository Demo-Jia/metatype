package fr.herman.metatype.processor;

import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import fr.herman.metatype.processor.meta.ClassMeta;

public class Context
{
    private final ProcessingEnvironment processingEnv;


    // Handle method stack to build all mapping method not already built
    private String                      newParams;

    private Map<TypeMirror, ClassMeta>  metas;

    public Context(ProcessingEnvironment processingEnvironment)
    {
        processingEnv = processingEnvironment;
    }

    public void error(Element element, String message, Object... args)
    {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String.format(message, args), element);
    }

    public void warn(String s, ExecutableElement element)
    {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, s, element);
    }

    public ProcessingEnvironment processingEnv()
    {
        return processingEnv;
    }

    public void info(Element typeElement, String templateMessage, Object... args)
    {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.format(templateMessage, args), typeElement);
    }

    public void warn(Element element, String templateMessage, Object... args)
    {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, String.format(templateMessage, args), element);
    }

    public void setNewParams(String newParams)
    {
        this.newParams = newParams;
    }

    public String newParams()
    {
        return newParams;
    }

    public ProcessingEnvironment getProcessingEnvironment()
    {
        return processingEnv;
    }

    public void setMetas(Map<TypeMirror, ClassMeta> metas)
    {
        this.metas = metas;
    }

    public boolean hasMeta(TypeMirror type)
    {
        return metas.containsKey(type);
    }

    public ClassMeta getMeta(TypeMirror type)
    {
        return metas.get(type);
    }
}