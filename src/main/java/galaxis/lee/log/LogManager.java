package galaxis.lee.log;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @Author: Lee
 * @Date: Created in 10:57 2019/9/12
 */

public class LogManager {
    private static Logger logger = null;
    public static Logger getLogger(){
        if(logger == null){
            PropertyConfigurator.configure("log4j.properties");
            logger = Logger.getLogger("FlashControl");
        }
        return logger;
    }

    public static void start(){
        if(logger == null){
            PropertyConfigurator.configure("log4j.properties");
            logger = Logger.getLogger("FlashControl");
        }
    }
}
