package galaxis.lee.db;


import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: Lee
 * @Date: Created in 9:20 2019/9/12
 */
public class DBConnectUtil {
    private static String url ;
    private static String username;
    private static String password;
    private static String driver;
    Connection conn = null;
    public static Connection conDB()
    {
        Map<String, String> conf = getConf();
        if(conf ==null){
            return null;
        }
        url = conf.get("url");
        username = conf.get("username");
        password = conf.get("password");
        driver = conf.get("driver");
        try {
            // Class.forName(driver);
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, username, password);
            return con;
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println("ConnectionUtil : "+ex.getMessage());
            return null;
        }
    }
    /**
     * 将页面配置数据  写入配置文件
     */
    public static void configFile(DBConfig config) {
        clearConfig("DBConfig.properties");
        Properties pop = new Properties();
        pop.setProperty("url", "jdbc:mysql://" + config.getHost() + ":" + config.getPort() + "/" + config.getSchema() + "?serverTimezone=CTT&useUnicode=true&characterEncoding=" + config.getEncoding() + "&allowMultiQueries=true");
        pop.setProperty("username", config.getUsername());
        pop.setProperty("password", config.getPassword());
        pop.setProperty("driver", "com.mysql.jdbc.Driver");
        try {
            pop.store(new FileOutputStream("DBConfig.properties"), "数据库基本配置");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void clearConfig(String fileName) {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 读取配置文件
     *
     * @return
     */
    public static Map<String, String> getConf() {
        Map<String, String> map = new HashMap<String, String>();
        File file = new File("DBConfig.properties");
        boolean canUse = false;
        try {
            canUse = file.exists() || file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!canUse) {
            System.out.println("操作配置文件失败， 无法继续");
            return null;
        }


        try (InputStream is = new FileInputStream(file)) {
            Properties pop = new Properties();
            pop.load(is);
            map.put("url", pop.getProperty("url"));
            map.put("username", pop.getProperty("username"));
            map.put("password", pop.getProperty("password"));
            map.put("driver", pop.getProperty("driver"));
            return map;

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        /*for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }*/
        return null;
    }
}
