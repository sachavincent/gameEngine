package util;

import java.util.Arrays;

public class Utils {

    private static Utils instance;

    public static Utils getInstance() {
        return instance == null ? (instance = new Utils()) : instance;
    }

    private Utils() {
    }

    public void printArray(Object[] array) {
        if (array.length == 0)
            System.out.println("Empty array");
        else
            Arrays.asList(array).forEach(System.out::println);
    }
}
