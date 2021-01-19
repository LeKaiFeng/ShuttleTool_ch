package galaxis.lee.util;

import galaxis.lee.log.LogManager;
import org.junit.Test;

import java.util.regex.Pattern;

/**
 * @Author: Lee
 * @Date: Created in 9:18 2020/8/7
 * @Description: TODO 使用正则对 TextFiled 合法性检查
 */
public class CheckValidTool {

    public static boolean isValidIP(String ip) {
        String patternIP ="^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        if (Pattern.matches(patternIP, ip)) {
            return true;
        }
        LogManager.getLogger().debug("非法 IP ："+ip);
        return false;
    }

    public static boolean isValidPORT(String port) {
        String patternPORT = "^[1-9]$|(^[1-9][0-9]$)|(^[1-9][0-9][0-9]$)|(^[1-9][0-9][0-9][0-9]$)|(^[1-6][0-5][0-5][0-3][0-5]$)/";
        if(Pattern.matches(patternPORT, port)){
            return true;
        }
        LogManager.getLogger().debug("非法 port ："+ port);
        return false;
    }

    public static boolean isValidField(String field) {
        String patternField = "^[0-9]{1,3}$";
        if(Pattern.matches(patternField,field)){
            return true;
        }
        LogManager.getLogger().debug("非法 textField ：" +field);
        return false;
    }



}

