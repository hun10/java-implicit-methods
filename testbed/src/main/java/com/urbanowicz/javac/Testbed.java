package com.urbanowicz.javac;

import java.util.HashMap;
import java.util.Map;

public class Testbed {
    public static void main(String... args) {
        Map<String, String> map = new HashMap<>();
        map.put("2+2", "4");
        System.out.println(map["2+2"]);
    }
}