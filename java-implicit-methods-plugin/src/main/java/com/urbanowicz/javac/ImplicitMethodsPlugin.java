package com.urbanowicz.javac;

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
}