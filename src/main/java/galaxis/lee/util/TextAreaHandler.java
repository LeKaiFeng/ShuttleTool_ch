package galaxis.lee.util;

import galaxis.lee.log.LogManager;

import java.io.*;

/**
 * @Author: Lee
 * @Date: Created in 11:16 2020/8/7
 * @Description: TODO 处理文本域 与日志文件
 */
public class TextAreaHandler {
    private BufferedReader in;
    private FileOutputStream out;

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
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //定时刷新日志
    // Timer timer = new Timer();
    // Platform.runLater(() -> timer.scheduleAtFixedRate(new TimerTask() {
    //     public void run() {
    //         Platform.runLater(() -> tea.appendText(textAreaHandler.showFlashLog()));
    //         Platform.runLater(() -> isConnected());
    //     }
    // }, 0, 3000 * 1));// 设定固定延时执行
    // Platform.runLater(() -> pool.execute(new RWLog()));

    // Platform.runLater(new Runnable() {
    //     @Override
    //     public void run() {
    //         pool.execute(new RWLog());
    //     }
    // });
     /* class RWLog implements Runnable {
        @Override
        public void run() {

            while (true) {
                try {
                    tea.appendText(textAreaHandler.showFlashLog());
                    isConnected();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LogManager.getLogger().debug(e.getMessage());
                }
            }
        }
    }*/
}
