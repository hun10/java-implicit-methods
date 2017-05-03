/*******************************************************************************
 * Copyright (c) 2012 Artem Melentyev <amelentev@gmail.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the
 * GNU Public License v2.0 + OpenJDK assembly exception.
 *
 * Contributors:
 *     Artem Melentyev <amelentev@gmail.com> - initial API and implementation
 *     some code from from OpenJDK langtools (GPL2 + assembly exception)
 ******************************************************************************/
package com.sun.tools.javac.comp;

import java.util.Map;
import java.util.WeakHashMap;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;


public class OOAttr extends Attr {
    protected OOAttr(Context context) {
        super(context);
    }
    public static OOAttr instance(Context context) {
        Attr attr = context.get(attrKey);
        if (attr instanceof OOAttr) return (OOAttr) attr;
        context.put(attrKey, (Attr)null);
        return new OOAttr(context);
    }

    /** WeakHashMap to allow GC collect entries. Because we don't need them when they are gone */
    Map<JCTree, JCTree.JCExpression> translateMap = new WeakHashMap<>();

    @Override
    public void visitApply(JCTree.JCMethodInvocation jcMethodInvocation) {
        super.visitApply(jcMethodInvocation);
        if (jcMethodInvocation.meth instanceof JCTree.JCIdent) {
            JCTree.JCIdent ident = (JCTree.JCIdent) jcMethodInvocation.meth;
            if (names.fromString("auto").equals(ident.name) && jcMethodInvocation.args.size() == 1) {
                Type.MethodType methodType = (Type.MethodType) ident.type;
                Type.ClassType cls = (Type.ClassType) methodType.argtypes.get(0);
                Type required = cls.typarams_field.get(0);

                JCTree.JCMemberReference mref = (JCTree.JCMemberReference) jcMethodInvocation.args.get(0);

                for (Symbol s : env.info.scope.getElements()) {
                    if (types.isAssignable(s.type, required)) {
                        JCTree.JCMethodInvocation mi = make.Apply(null, make.Select(mref.expr, mref.sym), List.of(make.Ident(s)));
                        attribExpr(mi, env);
                        translateMap.put(jcMethodInvocation, mi);
                    }
                }
            }
        }
    }
}
