package galaxis.lee.log;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.awt.*;
import java.io.*;

/**
 * @Author: Lee
 * @Date: Created in 16:47 2020/9/1
 * @Description: TODO
 */
public class LogShow implements Runnable{

    protected TextArea textArea;

    private BufferedReader in;
    private FileOutputStream out;

    public LogShow(TextArea textArea){
        this.textArea = textArea;
    }

    @Override
    public void run() {
        showFlashLog();
    }

    public String showFlashLog() {
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("./FlashControl.log"))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // System.out.println("开始 调用 日志刷新");
        String message = "";
        String str = null;
        try {
            if (in != null) {
                while ((str = in.readLine()) != null
                        && str != "\r\n") {
                    message += str + "\r\n";
                }
                // Platform.runLater();
                textArea.appendText(message);
                return message;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public boolean cleanLog() {
        try {
            // in.close();
            // in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("./FlashControl.log"))));
            out = new FileOutputStream("./FlashControl.log", false);
            out.write(new String("").getBytes());
            out.close();
            return true;
        } catch (IOException e1) {
            LogManager.getLogger().debug(e1.getMessage());
            return false;
        }finally {
            try {
                if (out != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
