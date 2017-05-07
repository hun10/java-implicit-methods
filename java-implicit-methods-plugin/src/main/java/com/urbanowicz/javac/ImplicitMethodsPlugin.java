package com.urbanowicz.javac;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.tree.JCTree;

public class ImplicitMethodsPlugin implements Plugin {
    @Override
    public String getName() {
        return "Implicit";
    }

    @Override
    public void init(JavacTask task, String... args) {
        ExplicitExpander explicitExpander = new ExplicitExpander(((BasicJavacTask) task).getContext());

        task.addTaskListener(new TaskListener() {
            @Override
            public void started(TaskEvent taskEvent) {
            }

            @Override
            public void finished(TaskEvent taskEvent) {
                if (taskEvent.getKind() == TaskEvent.Kind.ANALYZE) {
                    JCTree.JCCompilationUnit unit = (JCTree.JCCompilationUnit) taskEvent.getCompilationUnit();
                    unit.accept(explicitExpander);
                }
            }
        });
    }

    public static <T> void auto(Consumer<T> consumer) {
        throw new UnsupportedOperationException();
    }

    public static <T, U> void auto(BiConsumer<T, U> consumer) {
        throw new UnsupportedOperationException();
    }

    public static <T, R> R auto(Function<T, R> function) {
        throw new UnsupportedOperationException();
    }

    public static <T, U, R> R auto(BiFunction<T, U, R> function) {
        throw new UnsupportedOperationException();
    }
}