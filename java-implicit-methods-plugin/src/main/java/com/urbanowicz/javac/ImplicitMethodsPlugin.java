package com.urbanowicz.javac;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.main.JavaCompiler;
import javaoo.javac8.OOProcessor;

public class ImplicitMethodsPlugin implements Plugin {
    @Override
    public String getName() {
        return "Implicit";
    }

    @Override
    public void init(JavacTask task, String... args) {
        task.addTaskListener(new TaskListener() {
            @Override
            public void started(TaskEvent taskEvent) {
                if (taskEvent.getKind() == TaskEvent.Kind.ANALYZE) {
                    JavaCompiler compiler = JavaCompiler.instance(((BasicJavacTask) task).getContext());
                    OOProcessor.patch(compiler);
                }
            }

            @Override
            public void finished(TaskEvent taskEvent) {
            }
        });
    }

    public static <T> void auto(Consumer<T> consumer) {
        throw new UnsupportedOperationException();
    }

    public static <T, R> R auto(Function<T, R> function) {
        throw new UnsupportedOperationException();
    }

    public static <T, U, R> R auto(BiFunction<T, U, R> function) {
        throw new UnsupportedOperationException();
    }
}