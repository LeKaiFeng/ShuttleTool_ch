package galaxis.lee.junit;

import org.junit.Test;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * @Author: Lee
 * @Date: Created in 16:10 2020/8/25
 * @Description: TODO
 */
public class JunitTest2 {

    @Test
    public void testPath(){
        // 第一种：获取类加载的根路径   D:\git\daotie\daotie\target\classes
        // File f = new File(this.getClass().getResource("/").getPath());
        // System.out.println(f);
        //
        // // 获取当前类的所在工程路径; 如果不加“/”  获取当前类的加载目录  D:\git\daotie\daotie\target\classes\my
        // File f2 = new File(this.getClass().getResource("").getPath());
        // System.out.println(f2);

        // 第二种：获取项目路径    D:\git\daotie\daotie
        File directory = new File("");// 参数为空
        String courseFile = null;
        try {
            courseFile = directory.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(courseFile);


        // 第三种：  file:/D:/git/daotie/daotie/target/classes/
        URL xmlpath = this.getClass().getClassLoader().getResource("");
        System.out.println(xmlpath);


        // 第四种： D:\git\daotie\daotie
        System.out.println(System.getProperty("user.dir"));
        /*
         * 结果： C:\Documents and Settings\Administrator\workspace\projectName
         * 获取当前工程路径
         */

        // 第五种：  获取所有的类路径 包括jar包的路径
        System.out.println(System.getProperty("java.class.path"));
    }

    public static void main(String args[]) throws IOException {


        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.txt", "txt");
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(filter);
        fc.setMultiSelectionEnabled(false);
        int result = fc.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            if (!file.getPath().endsWith(".txt")) {
                file = new File(file.getPath() + ".txt");
            }
            System.out.println("file path=" + file.getPath());
            FileOutputStream fos = null;
            try {
                if (!file.exists()) {//文件不存在 则创建一个
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                fos.write("文件内容".getBytes());
                fos.flush();
            } catch (IOException e) {
                System.err.println("文件创建失败：");
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
