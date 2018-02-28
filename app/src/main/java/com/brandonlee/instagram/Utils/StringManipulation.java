package com.brandonlee.instagram.Utils;

/**
 * Created by Brandon on 2/28/2018.
 */

public class StringManipulation {

    public static String expandUsername(String username) {
        return username.replace(".", " ");
    }
    public static String condenseUsername(String username) {
        return username.replace(" ", ".");
    }

}

