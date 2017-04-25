package com.urbanowicz.javac;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

public class ImplicitMethodsPlugin implements Plugin {
    @Override
    public String getName() {
        return "Implicit";
    }

    @Override
    public void init(JavacTask task, String... args) {
        task.addTaskListener(new MyTask(task));
    }
}

class MyTask implements TaskListener {
    private final Replacer replacer;

    MyTask(JavacTask task) {
        this.replacer = new Replacer(task);
    }

    @Override
    public void started(TaskEvent taskEvent) {

    }

    @Override
    public void finished(TaskEvent taskEvent) {
        if (taskEvent.getKind() == TaskEvent.Kind.PARSE) {
            CompilationUnitTree compilationUnit = taskEvent.getCompilationUnit();
            ((JCTree.JCCompilationUnit) compilationUnit).accept(replacer);
        }
    }
}

class Replacer extends TreeTranslator {

    private final TreeMaker make;
    private final Names names;

    Replacer(JavacTask task) {
        Context context = ((BasicJavacTask)task).getContext();
        make = TreeMaker.instance(context);
        names = Names.instance(context);
    }

    @Override
    public void visitAssign(JCTree.JCAssign tree) {
        if (tree.getVariable().getKind() == Tree.Kind.ARRAY_ACCESS) {
            JCTree.JCArrayAccess arrayAccess = (JCTree.JCArrayAccess) tree.getVariable();
            if (arrayAccess.getIndex().getKind() == Tree.Kind.STRING_LITERAL) {
                JCTree.JCExpression methodSelect = make.Select(arrayAccess.getExpression(), names.fromString("put"));
                result = make.Apply(List.nil(), methodSelect, List.of(arrayAccess.getIndex(), tree.getExpression()));
                return;
            }
        }
        super.visitAssign(tree);
    }

    @Override
    public void visitIndexed(JCTree.JCArrayAccess tree) {
        if (tree.getIndex().getKind() == Tree.Kind.STRING_LITERAL) {
            JCTree.JCExpression methodSelect = make.Select(tree.getExpression(), names.fromString("get"));
            result = make.Apply(List.nil(), methodSelect, List.of(tree.getIndex()));
            return;
        }
        super.visitIndexed(tree);
    }
}