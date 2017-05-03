package com.urbanowicz.javac;

import java.math.BigInteger;

public class Testbed {
    public static void main(String... args) {
        BigInteger x = 55;
        String arg1 = "success";
        testMethod(arg1);
    }

    private static void testMethod(String arg) {
        System.out.println(arg);
    }
}