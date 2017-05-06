package com.urbanowicz.javac;

import static com.urbanowicz.javac.ImplicitMethodsPlugin.auto;

public class Testbed {
    public static void main(String... args) {
        String arg1 = "success";
        Void auto = auto(Testbed::testMethod2);
    }

    private static Void testMethod(String arg) {
        System.out.println(arg);
        return null;
    }

    private static Void testMethod2(String arg, String[] var) {
        System.out.println(arg);
        return null;
    }
}