package com.hbm.registries;

import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RegistryHelper {
    public static String generateOrderlyName(String name){
        return Arrays.stream(name.split("_|\\.")).map(s -> s.substring(0,1).toUpperCase() + s.substring(1)).reduce("",(r, id) -> r + (r.isEmpty() ? "": " ") + id);
    }
    public static String generateReversedName(String name){
        List<String> strings = new java.util.ArrayList<>(Arrays.stream(name.split("_|\\.")).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1)).toList());
        Collections.reverse(strings);
        return strings.subList(1, strings.size()).stream().reduce(strings.get(0), (s, s1) -> s + " " + s1);
    }

    public static String generateOrderlyExceptFirstName(String name){
        List<String> strings = Arrays.stream(name.split("_|\\.")).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1)).toList();
        return strings.subList(1, strings.size()).stream().reduce("",(r, id) -> r + (r.isEmpty() ? "": " ") + id) + " " + strings.get(0);
    }
}
