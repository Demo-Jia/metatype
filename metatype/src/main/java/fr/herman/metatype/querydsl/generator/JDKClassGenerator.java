package fr.herman.metatype.querydsl.generator;

import java.io.StringWriter;
import java.io.Writer;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import com.mysema.codegen.CodegenException;
import com.mysema.codegen.MemFileManager;
import com.mysema.codegen.MemSourceFileObject;
import com.mysema.codegen.SimpleCompiler;

public class JDKClassGenerator extends ClassGenerator
{
    private final MemFileManager fileManager;

    private final String         classpath;

    private final List<String>   compilationOptions;

    private final JavaCompiler   compiler;

    public JDKClassGenerator(URLClassLoader parent)
    {
        this(parent, ToolProvider.getSystemJavaCompiler());
    }

    public JDKClassGenerator(URLClassLoader parent, JavaCompiler compiler)
    {
        fileManager = new MemFileManager(parent, compiler.getStandardFileManager(null, null, null));
        this.compiler = compiler;
        classpath = SimpleCompiler.getClassPath(parent);
        loader = fileManager.getClassLoader(StandardLocation.CLASS_OUTPUT);
        compilationOptions = Arrays.asList("-classpath", classpath, "-g:none");
    }

    @Override
    protected void compile(String source, String id)
    {
        // compile
        SimpleJavaFileObject javaFileObject = new MemSourceFileObject(id, source);
        Writer out = new StringWriter();

        CompilationTask task = compiler.getTask(out, fileManager, null, compilationOptions, null, Collections.singletonList(javaFileObject));
        if (!task.call().booleanValue())
        {
            throw new CodegenException("Compilation of " + source + " failed.\n" + out.toString());
        }
    }

}
