package com.urbanowicz.javac;

import java.util.function.Function;
import java.util.function.Consumer;

public class Testbed {
    public static void main(String... args) {
        String arg1 = "success";
        auto(Testbed::testMethod);
    }

    private static void testMethod(String arg) {
        System.out.println(arg);
    }

    private static <T> void auto(Consumer<T> consumer) {
        throw new UnsupportedOperationException();
    }
}