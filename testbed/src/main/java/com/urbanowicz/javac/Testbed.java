package com.urbanowicz.javac;

import java.util.UUID;

import static com.urbanowicz.javac.ImplicitMethodsPlugin.auto;

public class Testbed {
    public static void main(String... args) {
        String arg1 = "success";
        int s = 45;
        UUID u = UUID.randomUUID();
        auto(Testbed::tri);
    }

    static void tri(String x, long y, UUID uuid) {}

    static void arc(String[] y) {
        for (String s : y) {
            auto(Testbed::testMethod);
        }
    }

    private static void testMethod(String arg) {
        System.out.println(arg);
    }
}