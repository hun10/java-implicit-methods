/*******************************************************************************
 * Copyright (c) 2012 Artem Melentyev <amelentev@gmail.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the 
 * GNU Public License v2.0 + OpenJDK assembly exception.
 * 
 * Contributors:
 *     Artem Melentyev <amelentev@gmail.com> - initial API and implementation
 ******************************************************************************/
package javaoo.javac8;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.MemberEnter;
import com.sun.tools.javac.comp.OOAttr;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.util.Context;

public class OOProcessor {
    public static void patch(JavaCompiler compiler) {
        try {
            ClassLoader pcl = OOProcessor.class.getClassLoader();
            JavaCompiler delCompiler = (JavaCompiler) get(compiler, "delegateCompiler");
            if (delCompiler != null)
                compiler = delCompiler; // javac has delegateCompiler. netbeans hasn't
            Context context = (Context) get(compiler, "context");
            Attr attr = Attr.instance(context);
            if (attr instanceof OOAttr)
                return;
            ClassLoader destcl = attr.getClass().getClassLoader();

            // hack: load classes to the same classloader so they will be able to use and override default accessor members
            Class<?> attrClass = reloadClass("com.sun.tools.javac.comp.OOAttr", pcl, destcl);
            Class<?> transTypesClass = reloadClass("com.sun.tools.javac.comp.OOTransTypes", pcl, destcl);

            attr = (Attr) getInstance(attrClass, context);
            Object transTypes = getInstance(transTypesClass, context);

            set(compiler, JavaCompiler.class, "attr", attr);
            set(compiler, JavaCompiler.class, "transTypes", transTypes);
            set(MemberEnter.instance(context), MemberEnter.class, "attr", attr);
        } catch (Exception e) {
            sneakyThrow(e);
        }
    }
    @SuppressWarnings("unchecked")
    /** add class claz to outClassLoader */
    private static <T> Class<T> reloadClass(final String claz, ClassLoader incl, ClassLoader outcl) throws Exception {
        try { // already loaded?
            return (Class<T>) outcl.loadClass(claz);
        } catch (ClassNotFoundException ignored) {}
        String path = claz.replace('.', '/') + ".class";
        InputStream is = incl.getResourceAsStream(path);
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        Method m = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
        m.setAccessible(true);
        return (Class<T>) m.invoke(outcl, claz, bytes, 0, bytes.length);
    }

    // reflection stuff
    private static Object getInstance(Class<?> clas, Context context) throws ReflectiveOperationException {
        return clas.getDeclaredMethod("instance", Context.class).invoke(null, context);
    }
    private static Object get(Object obj, String field) throws ReflectiveOperationException {
        Field f = obj.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return f.get(obj);
    }
    private static void set(Object obj, Class clas, String field, Object val) throws ReflectiveOperationException {
        Field f = clas.getDeclaredField(field);
        f.setAccessible(true);
        f.set(obj, val);
    }
    private static void sneakyThrow(Throwable ex) {
        OOProcessor.<RuntimeException>sneakyThrowInner(ex);
    }
    private static <T extends Throwable> void sneakyThrowInner(Throwable ex) throws T {
        throw (T) ex;
    }
}
