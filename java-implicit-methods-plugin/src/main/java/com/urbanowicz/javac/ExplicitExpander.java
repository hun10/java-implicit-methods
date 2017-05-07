package com.urbanowicz.javac;

import static java.util.stream.Collectors.toList;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

class ExplicitExpander extends TreeTranslator {
    private final Names names;
    private final Types types;
    private final TreeMaker make;

    private List<List<Symbol>> frames;

    ExplicitExpander(Context context) {
        names = Names.instance(context);
        types = Types.instance(context);
        make = TreeMaker.instance(context);
        frames = List.of(List.nil());
    }

    @Override
    public void visitVarDef(JCTree.JCVariableDecl jcVariableDecl) {
        super.visitVarDef(jcVariableDecl);

        Symbol.VarSymbol sym = jcVariableDecl.sym;
        addToCurrentFrame(sym);
    }

    @Override
    public void visitBlock(JCTree.JCBlock jcBlock) {
        inFrame(() -> super.visitBlock(jcBlock));
    }

    @Override
    public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
        inFrame(() -> super.visitMethodDef(jcMethodDecl));
    }

    @Override
    public void visitForeachLoop(JCTree.JCEnhancedForLoop jcEnhancedForLoop) {
        inFrame(() -> super.visitForeachLoop(jcEnhancedForLoop));
    }

    @Override
    public void visitApply(JCTree.JCMethodInvocation jcMethodInvocation) {
        super.visitApply(jcMethodInvocation);

        if (jcMethodInvocation.meth instanceof JCTree.JCIdent) {
            JCTree.JCIdent ident = (JCTree.JCIdent) jcMethodInvocation.meth;

            if (names.fromString("auto").equals(ident.name)
                    && names.fromString(ImplicitMethodsPlugin.class.getName())
                    .equals(ident.sym.owner.getQualifiedName())) {

                JCTree.JCMemberReference ref = (JCTree.JCMemberReference) jcMethodInvocation.args.get(0);
                Type.MethodType methodType = (Type.MethodType) ref.sym.type;

                List<Symbol> scope = List.from(frames.stream().flatMap(List::stream).collect(toList()));

                List<JCTree.JCExpression> args = List.nil();
                for (Type required : methodType.argtypes) {
                    for (Symbol s : scope) {
                        if (types.isAssignable(s.type, required)) {
                            args = args.append(make.Ident(s));
                        }
                    }
                }

                JCTree.JCMethodInvocation mi = make.Apply(
                        null,
                        make.Select(ref.expr, ref.sym), args
                );
                mi.type = jcMethodInvocation.type;
                mi.pos = ref.pos;
                result = mi;
            }
        }
    }

    private void inFrame(Runnable v) {
        frames = frames.prepend(List.nil());
        v.run();
        frames = frames.tail;
    }

    private void addToCurrentFrame(Symbol sym) {
        List<Symbol> head = frames.head;
        List<Symbol> newHead = head.append(sym);
        frames = frames.tail.prepend(newHead);
    }
}
