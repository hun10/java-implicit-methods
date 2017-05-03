package com.urbanowicz.javac;

import java.math.BigInteger;
import java.util.function.Function;
import java.util.function.Consumer;

public class Testbed {
    public static void main(String... args) {
        BigInteger x = 55;
        String arg1 = "success";
        auto(Testbed::testMethod);
        auto(Testbed::testMethod2);
    }

    private static void testMethod(String arg) {
        System.out.println(arg);
    }

    private static void testMethod2(BigInteger arg) {
        System.out.println(arg);
    }

    private static <T> void auto(Consumer<T> consumer) {
        throw new UnsupportedOperationException();
    }
}