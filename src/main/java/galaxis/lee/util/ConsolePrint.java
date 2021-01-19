package galaxis.lee.util;

import galaxis.lee.log.LogManager;

/**
 * @Author: Lee
 * @Date: Created in 10:20 2020/8/10
 * @Description: TODO
 */
public class ConsolePrint {

    public static void print1(byte[] b) {
        if (b == null) {
            return;
        }
        String message = "";
        for (byte bt : b) {
            message = message + " " + bt;
        }
        System.out.println(message);
        if (message.length() < 40) {
            LogManager.getLogger().debug(message);
        }
    }

}
