package com.urbanowicz.javac;

public class JavacRunner {
    public static void main(String... args) throws Exception {
        com.sun.tools.javac.Main.main(new String[] {
                "-printsource",
                "-Xplugin:Implicit",
                "testbed/src/main/java/com/urbanowicz/javac/Testbed.java"
        });
    }
}
