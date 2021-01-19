package galaxis.lee.senceControl;

import com.jfoenix.controls.JFXButton;
import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.FXMLView;
import galaxis.lee.control.FlashTask;
import galaxis.lee.log.LogManager;
import galaxis.lee.log.LogReader;
import galaxis.lee.log.LogShow;
import galaxis.lee.util.TextAreaHandler;
import galaxis.lee.view.DialogBuilder;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import sun.rmi.runtime.Log;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Lee
 * @Date: Created in 14:56 2020/8/27
 * @Description: TODO
 */
@FXMLView(value = "/fxml/log.fxml")
@FXMLController
public class ShowLogController extends BaseFXController {
    @FXML
    protected TextArea tea;
    @FXML
    protected TextField lineNum;
    @FXML
    protected JFXButton flush, clean;
    protected ThreadPoolExecutor pool;
    protected TextAreaHandler ta;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tea.setEditable(false);
        lineNum.setTooltip(new Tooltip("默认显示倒数1000行日志"));
        ta = new TextAreaHandler();
        //线程发送任务
        pool = new ThreadPoolExecutor(2, 2, 60,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5),
                Executors.defaultThreadFactory());
        //弹出窗口初始化
        // Platform.runLater(new Runnable() {
        //     @Override
        //     public void run() {
        //         pool.execute(new LogReader(tea));
        //     }
        // });
        //更新： 可显示倒数1000行日志（默认）
        pool.execute(new LogReader(tea, lineNum));

        // String logMesage = ta.showFlashLog();
        // tea.appendText(logMesage);

        flush.setOnMouseClicked(event -> {
            tea.setText("");
            pool.execute(new LogReader(tea, lineNum));
            // tea.appendText(ta.showFlashLog());
        });

        clean.setOnMouseClicked(event -> {
            // pool.execute(new LogReader(tea));
            boolean boo = DialogBuilder.alertChoose(clean, "删除后不可恢复，确认删除吗!");
            if (boo) {
                tea.setText("");
                boolean b = ta.cleanLog();
                if (b) {
                    tea.setText("");

                    LogManager.getLogger().debug("Log cleared successfully！");
                } else {
                    LogManager.getLogger().debug("Log emptying failed！");
                }
                pool.execute(new LogReader(tea, lineNum));
            }
        });
    }
}
