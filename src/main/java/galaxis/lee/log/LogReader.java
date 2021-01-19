package galaxis.lee.log;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @Author: Lee
 * @Date: Created in 10:10 2020/9/2
 * @Description: TODO
 */
public class LogReader implements Runnable {
    private File logFile = null;
    private long lastTimeFileSize = 0; // 上次文件大小
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    protected TextArea textArea;

    protected TextField lineNum;
    public LogReader(TextArea tea, TextField lineNum) {
        this.textArea = tea;
        this.lineNum = lineNum;

    }

    /**
     * 实时输出日志信息
     */
    public void run() {

            // Platform.runLater(()->textArea.appendText(showFlashLog()));
        textArea.appendText(showFlashLog(new File("./FlashControl.log"),Integer.parseInt(lineNum.getText())));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

    }

    private BufferedReader in;
    private FileOutputStream out;

    /**
     *
     * @param f
     * @param lineNum 读取倒数多少行
     * @return
     */
    public static String showFlashLog(File f, int lineNum) {
        String logger = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            ArrayList<String> log = new ArrayList<>();
            while (line != null) {
                log.add(line);
                line = br.readLine();
            }
            if (lineNum > log.size() || lineNum < 0) {
                lineNum = log.size();
            }
            for (int i = log.size() - lineNum; i <= log.size() - 1; i++) {
                logger += log.get(i)+"\r\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logger;
    }

    //普通读取日志，弊端会导致日志卡死
  /*  public String showFlashLog() {
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
    }*/

}
