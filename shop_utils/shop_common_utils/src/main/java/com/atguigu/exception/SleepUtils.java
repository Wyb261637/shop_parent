package com.atguigu.exception;

/**
 * @author wangy
 */
public class SleepUtils {
    public static void second(int second) {
        try {
            Thread.sleep(1000L *second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void millis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
