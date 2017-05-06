package com.urbanowicz.javac;

import static com.urbanowicz.javac.ImplicitMethodsPlugin.auto;

public class Testbed {
    public static void main(String... args) {
        String arg1 = "success";
        auto(Testbed::testMethod);
    }

    private static void testMethod(String arg) {
        System.out.println(arg);
    }
}