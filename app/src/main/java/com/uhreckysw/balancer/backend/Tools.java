package com.uhreckysw.balancer.backend;

public class Tools {

    public static int int_length(int number) {
        int length = 0;
        long temp = 1;
        while (temp <= number) {
            length++;
            temp *= 10;
        }
        return length;
    }
}
