package com.akasiyanik.trip;

import java.util.HashMap;
import java.util.Map;

/**
 * @author akasiyanik
 *         2/28/17
 */


public class Main {

    @GuardedBy
    private  int r;

    public static void main(String[] args) {
        System.out.println(new Main().r);
    }
}
