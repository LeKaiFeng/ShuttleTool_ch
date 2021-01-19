package galaxis.lee.control;

import com.jfoenix.controls.JFXButton;
import galaxis.lee.control.device.shuttleAction;
import galaxis.lee.control.device.FlashInterface;
import galaxis.lee.log.LogManager;
import galaxis.lee.view.DialogBuilder;

import java.util.ArrayList;

/**
 * @Author: Lee
 * @Date: Created in 16:43 2020/6/6
 * @Description: TODO
 */
public class FlashTask implements Runnable {

    private ArrayList<shuttleAction> actions;
    private FlashInterface fc;
    protected JFXButton btn_start;
    private int num = 1;

    public FlashTask(ArrayList<shuttleAction> actions, FlashInterface fc, JFXButton btn_start) {
        this.actions = actions;
        this.fc = fc;
        this.btn_start = btn_start;
    }

    @Override
    public void run() {
        // System.out.println("Current Task Thread is" + Thread.currentThread().getName());
        try {
            // Thread.sleep(2000);
            // LogManager.getLogger().debug("actions: "+actions.size());
            if (actions.size() != 0) {
                for (shuttleAction san : actions) {
                    // System.out.println("type: " + san.getType() + " , action: " + san.getAction() + ", length: " + san.getLength());
                    LogManager.getLogger().debug("type: " + san.getType() + " , action: " + san.getAction() + ", length: " + san.getLength());
                }
                int t;
                for (shuttleAction sA : actions) {
                    LogManager.getLogger().debug("num:" + (num++) + ", sA.type:" + sA.getType() + ", action" + ((sA.type == 1) ? "move" :
                            "load"));
                    if (sA.type == 1) {
                        LogManager.getLogger().debug("Send move, " + sA.action + ", " + sA.length);
                        if (!fc.moveTo(sA.action, sA.length, 0, 0, 0)) {
                            LogManager.getLogger().debug("Shuttle is nor ready, break!");
                            btn_start.setDisable(false);    //小车未准备就绪，解禁
                            return;
                        }
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e1) {
                            LogManager.getLogger().error(e1.toString());
                        }
                        t = fc.isTaskFinish();
                        LogManager.getLogger().debug("Task result is " + t);
                        if (t != 1) {
                            btn_start.setDisable(false);    //未收到完成，解禁
                            return;
                        }
                    } else if (sA.type == 0) {  // sleep task
                        try {
                            Thread.sleep(sA.length);
                        } catch (InterruptedException e1) {
                            LogManager.getLogger().error(e1.toString());
                        }
                    } else {
                        LogManager.getLogger().debug("Send action, " + sA.action);
                        if (!fc.loadUpDown(sA.action, sA.length, 0)) {
                            LogManager.getLogger().debug("Shuttle is nor ready, break!");
                            btn_start.setDisable(false);
                            return;
                        }
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e1) {
                            LogManager.getLogger().error(e1.toString());
                        }
                        t = fc.isTaskFinish();
                        LogManager.getLogger().debug("Task result is " + t);
                        if (t != 1) {
                            btn_start.setDisable(false);
                            return;
                        }
                    }
                }
                btn_start.setDisable(false);// 所有任务完成,执行解禁
            } else {
                DialogBuilder.alert(btn_start, "尚未勾选任务");
                btn_start.setDisable(false);// 一个任务没有解禁
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
