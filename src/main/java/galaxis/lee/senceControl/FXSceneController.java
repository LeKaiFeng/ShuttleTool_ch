package galaxis.lee.senceControl;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import de.felixroske.jfxsupport.FXMLView;
import galaxis.lee.control.DoubleClampShuttleConnector;
import galaxis.lee.control.ShuttleSingleClampConnector;
import galaxis.lee.control.V4ShuttleConnector2;
import galaxis.lee.control.device.shuttleAction;
import galaxis.lee.control.device.FlashInterface;
import galaxis.lee.db.ConfigDBController;
import galaxis.lee.log.LogManager;
import galaxis.lee.control.FlashTask;
import galaxis.lee.util.*;
import galaxis.lee.view.DialogBuilder;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Lee
 * @Date: Created in 11:51 2020/8/6
 */

@FXMLView(value = "/fxml/flash.fxml")
public class FXSceneController extends BaseFXController {
    @FXML
    private HBox hbox_top, hbox_end;        //获取头部配置、尾部配置
    @FXML
    private GridPane grid;                  //获取中间部分配置
    @FXML
    private MenuBar mb;                     //新增菜单栏
    @FXML
    private ChoiceBox shuttleType;          //小车类型
    @FXML
    private TextField shuttleId, ip, port;
    @FXML
    private JFXButton btn_connect;
    @FXML
    private Circle circle;
    @FXML
    private ChoiceBox dir1, dir2, dir3, dir4, dir5, dir6, dir7, dir8, dir9, dir10,
            dir11, dir12, dir13, dir14, dir15, dir16, dir17, dir18, dir19, dir20,
            dir21, dir22, dir23, dir24, dir25, dir26, dir27, dir28, dir29, dir30,
            dir31, dir32, dir33, dir34, dir35, dir36, dir37, dir38, dir39, dir40;
    @FXML
    private ChoiceBox action1, action2, action3, action4, action5, action6, action7, action8, action9, action10,
            action11, action12, action13, action14, action15, action16, action17, action18, action19, action20,
            action21, action22, action23, action24, action25, action26, action27, action28, action29, action30,
            action31, action32, action33, action34, action35, action36, action37, action38, action39, action40;
    @FXML
    private Label label1, label2, label3, label4, label5, label6, label7, label8, label9, label10,
            label11, label12, label13, label14, label15, label16, label17, label18, label19, label20,
            label21, label22, label23, label24, label25, label26, label27, label28, label29, label30,
            label31, label32, label33, label34, label35, label36, label37, label38, label39, label40;
    @FXML
    private TextField dis1, dis2, dis3, dis4, dis5, dis6, dis7, dis8, dis9, dis10,
            dis11, dis12, dis13, dis14, dis15, dis16, dis17, dis18, dis19, dis20,
            dis21, dis22, dis23, dis24, dis25, dis26, dis27, dis28, dis29, dis30,
            dis31, dis32, dis33, dis34, dis35, dis36, dis37, dis38, dis39, dis40;
    @FXML
    private JFXCheckBox check1, check2, check3, check4, check5, check6, check7, check8, check9, check10,
            check11, check12, check13, check14, check15, check16, check17, check18, check19, check20,
            check21, check22, check23, check24, check25, check26, check27, check28, check29, check30,
            check31, check32, check33, check34, check35, check36, check37, check38, check39, check40;
    @FXML
    private TextField times;    //循环次数
    @FXML
    private JFXButton start;    //执行

    protected FlashInterface fc;
    protected TextAreaHandler textAreaHandler;
    protected ThreadPoolExecutor pool;
    protected Stage primaryStage;       //弹窗专用


    public boolean isConnected = false; //是否连接上
    /**
     * 仅有老版程序autoTest 双深取放货接口： 11 12 13 14 (尚不清楚适用哪种车型，嘉兴演示库有用到)
     * shuttle4.5、GIS、单深夹抱车、双深夹抱车等 双深取放货接口： 7 8 9 10
     */
    public boolean isNewLoadUpDownInterface = true;    //是否为新的双深取放货接口
    public boolean isSingleClamp = false; //是否为单深夹抱车,界面下拉框移除双深货位的取放货,取放货添加宽度

    public FXSceneController() {
    }

    public FXSceneController(Stage stage) {
        this.primaryStage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        shuttleType.getItems().addAll(TYPE2);
        shuttleType.getSelectionModel().select(TYPE2);
        //UI
        choiceBoxChangeListener(dir1, action1, label1, dis1);
        choiceBoxChangeListener(dir2, action2, label2, dis2);
        choiceBoxChangeListener(dir3, action3, label3, dis3);
        choiceBoxChangeListener(dir4, action4, label4, dis4);
        choiceBoxChangeListener(dir5, action5, label5, dis5);
        choiceBoxChangeListener(dir6, action6, label6, dis6);
        choiceBoxChangeListener(dir7, action7, label7, dis7);
        choiceBoxChangeListener(dir8, action8, label8, dis8);
        choiceBoxChangeListener(dir9, action9, label9, dis9);
        choiceBoxChangeListener(dir10, action10, label10, dis10);
        choiceBoxChangeListener(dir11, action11, label11, dis11);
        choiceBoxChangeListener(dir12, action12, label12, dis12);
        choiceBoxChangeListener(dir13, action13, label13, dis13);
        choiceBoxChangeListener(dir14, action14, label14, dis14);
        choiceBoxChangeListener(dir15, action15, label15, dis15);
        choiceBoxChangeListener(dir16, action16, label16, dis16);
        choiceBoxChangeListener(dir17, action17, label17, dis17);
        choiceBoxChangeListener(dir18, action18, label18, dis18);
        choiceBoxChangeListener(dir19, action19, label19, dis19);
        choiceBoxChangeListener(dir20, action20, label20, dis20);

        //俞 + 40
        choiceBoxChangeListener(dir21, action21, label21, dis21);
        choiceBoxChangeListener(dir22, action22, label22, dis22);
        choiceBoxChangeListener(dir23, action23, label23, dis23);
        choiceBoxChangeListener(dir24, action24, label24, dis24);
        choiceBoxChangeListener(dir25, action25, label25, dis25);
        choiceBoxChangeListener(dir26, action26, label26, dis26);
        choiceBoxChangeListener(dir27, action27, label27, dis27);
        choiceBoxChangeListener(dir28, action28, label28, dis28);
        choiceBoxChangeListener(dir29, action29, label29, dis29);
        choiceBoxChangeListener(dir30, action30, label30, dis30);

        choiceBoxChangeListener(dir31, action31, label31, dis31);
        choiceBoxChangeListener(dir32, action32, label32, dis32);
        choiceBoxChangeListener(dir33, action33, label33, dis33);
        choiceBoxChangeListener(dir34, action34, label34, dis34);
        choiceBoxChangeListener(dir35, action35, label35, dis35);
        choiceBoxChangeListener(dir36, action36, label36, dis36);
        choiceBoxChangeListener(dir37, action37, label37, dis37);
        choiceBoxChangeListener(dir38, action38, label38, dis38);
        choiceBoxChangeListener(dir39, action39, label39, dis39);
        choiceBoxChangeListener(dir40, action40, label40, dis40);

        //线程发送任务
        pool = new ThreadPoolExecutor(2, 2, 60,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5),
                Executors.defaultThreadFactory());

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pool.execute(new RWLog());
            }
        });

      /*  // 监听小车类型，动态改变 界面
        shuttleType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (fc != null) {    //已建立连接，界面不再变动
                    return;
                }
                String type = shuttleType.getItems().get(newValue.intValue()).toString();
                isSingleClamp = ((TYPE3.equals(type)) ? true : false);
            }
        });*/
        textAreaHandler = new TextAreaHandler();

        FileUtil fileUtil = new FileUtil();
        ObservableList<Menu> menus = mb.getMenus();         //菜单栏的各种操作
        for (Menu mu : menus) {
            ObservableList<MenuItem> items = mu.getItems();
            for (MenuItem mi : items) {
                mi.setOnAction(e -> {
                    if (mi.getText().equals(LOG_ITEM1)) {
                        ShowLogController showLogController = (ShowLogController) loadFXMLPage(LOG_ITEM1, FXMLPages.SHOW_LOG, false);
                    } else if (mi.getText().equals(CONFIG_ITEM2)) {
                        //TODO 在加载配置文件期间，让监听器不起作用
                        fileUtil.fillSceneNode(hbox_top, grid, hbox_end, primaryStage);
                    } else if (mi.getText().equals(CONFIG_ITEM1)) {
                        fileUtil.saveNodeValue(hbox_top, grid, hbox_end, primaryStage);
                    } else if (mi.getText().equals(VERSION_ITEM1)) {
                        DialogBuilder.alert(mb, Version.currentVersion());
                    } else if (mi.getText().equals(VERSION_ITEM2)) {
                        DialogBuilder.alert(mb, Version.showVersions());
                    }
                });


            }
        }

    }


    class RWLog implements Runnable {
        @Override
        public void run() {

            boolean status = false;

            while (true) {
                try {
                    V4ShuttleConnector2 single = (V4ShuttleConnector2) fc;
                    if (fc != null) {
                        // System.out.println("connectState1:"+ single.isConnectState());
                        if (single.isConnectState()) {
                            if (isConnected == false) {
                                //需判断是否连接成功，再禁用，或不禁用
                                shuttleId.setDisable(true);
                                ip.setDisable(true);
                                port.setDisable(true);
                                btn_connect.setDisable(true);

                                if (status == false) {
                                    circle.setFill(Color.GREEN);
                                    LogManager.getLogger().debug("Connecting success");
                                    status = true;
                                }

                                isConnected = true;
                                // break;
                            }
                        } else {
                            if (status == true) {
                                status = false;
                                circle.setFill(Color.RED);
                                LogManager.getLogger().debug("Connection failed");
                                // shuttleId.setDisable(false);
                                // ip.setDisable(false);
                                // port.setDisable(false);
                                // btn_connect.setDisable(false);
                            }
                        }
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LogManager.getLogger().debug(e.getMessage());
                }
            }
        }
    }

    @FXML
    public void connect() {
        try {
            String flash_IP = ip.getText();
            String flash_port = port.getText();
            String flash_id = shuttleId.getText();
            if (CheckValidTool.isValidField(flash_id) && CheckValidTool.isValidIP(flash_IP) && CheckValidTool.isValidPORT(flash_port)) {
                String type = (String) shuttleType.getValue();
                if (TYPE2.equals(type)) {
                    fc = new V4ShuttleConnector2(flash_IP, Integer.valueOf(flash_port), Integer.valueOf(flash_id));
                    isNewLoadUpDownInterface = true;
                } else {
                    LogManager.getLogger().debug("异常类型：" + type);
                    return;
                }
            } else {
                DialogBuilder.alert(btn_connect, "请检查填写的数据");
            }
        } catch (Exception e) {
            LogManager.getLogger().debug(e.getMessage());
            DialogBuilder.alert(btn_connect, "请检查日志");
        }
    }

    @FXML
    public void flashAction() {  //执行
        if (fc == null) {
            DialogBuilder.alert(start, "尚未建立连接");
            return;
        }
        try {
            // times.setDisable(true);
            start.setDisable(true);
            Thread.sleep(100);
            ArrayList<shuttleAction> actions = new ArrayList<>();

            int m = Integer.valueOf(times.getText());
            for (int i = 1; i <= m; i++) {
                int t = 0;
                int a = 0;
                int length = 0;

                //优化代码 遍历所有节点，获取id,依次添加到FXDate,也麻烦，算了

                if (check1.isSelected()) {
                    actions.add(getFXData(t, a, length, dir1, action1, dis1));
                }
                if (check2.isSelected()) {
                    actions.add(getFXData(t, a, length, dir2, action2, dis2));
                }
                if (check3.isSelected()) {
                    actions.add(getFXData(t, a, length, dir3, action3, dis3));
                }
                if (check4.isSelected()) {
                    actions.add(getFXData(t, a, length, dir4, action4, dis4));
                }
                if (check5.isSelected()) {
                    actions.add(getFXData(t, a, length, dir5, action5, dis5));
                }
                if (check6.isSelected()) {
                    actions.add(getFXData(t, a, length, dir6, action6, dis6));
                }
                if (check7.isSelected()) {
                    actions.add(getFXData(t, a, length, dir7, action7, dis7));
                }
                if (check8.isSelected()) {
                    actions.add(getFXData(t, a, length, dir8, action8, dis8));
                }
                if (check9.isSelected()) {
                    actions.add(getFXData(t, a, length, dir9, action9, dis9));
                }
                if (check10.isSelected()) {
                    actions.add(getFXData(t, a, length, dir10, action10, dis10));
                }
                if (check11.isSelected()) {
                    actions.add(getFXData(t, a, length, dir11, action11, dis11));
                }
                if (check12.isSelected()) {
                    actions.add(getFXData(t, a, length, dir12, action12, dis12));
                }
                if (check13.isSelected()) {
                    actions.add(getFXData(t, a, length, dir13, action13, dis13));
                }
                if (check14.isSelected()) {
                    actions.add(getFXData(t, a, length, dir14, action14, dis14));
                }
                if (check15.isSelected()) {
                    actions.add(getFXData(t, a, length, dir15, action15, dis15));
                }
                if (check16.isSelected()) {
                    actions.add(getFXData(t, a, length, dir16, action16, dis16));
                }
                if (check17.isSelected()) {
                    actions.add(getFXData(t, a, length, dir17, action17, dis17));
                }
                if (check18.isSelected()) {
                    actions.add(getFXData(t, a, length, dir18, action18, dis18));
                }
                if (check19.isSelected()) {
                    actions.add(getFXData(t, a, length, dir19, action19, dis19));
                }
                if (check20.isSelected()) {
                    actions.add(getFXData(t, a, length, dir20, action20, dis20));
                }
                if (check21.isSelected()) {
                    actions.add(getFXData(t, a, length, dir21, action21, dis21));
                }
                if (check22.isSelected()) {
                    actions.add(getFXData(t, a, length, dir22, action22, dis22));
                }
                if (check23.isSelected()) {
                    actions.add(getFXData(t, a, length, dir23, action23, dis23));
                }
                if (check24.isSelected()) {
                    actions.add(getFXData(t, a, length, dir24, action24, dis24));
                }
                if (check25.isSelected()) {
                    actions.add(getFXData(t, a, length, dir25, action25, dis25));
                }
                if (check26.isSelected()) {
                    actions.add(getFXData(t, a, length, dir26, action26, dis26));
                }
                if (check27.isSelected()) {
                    actions.add(getFXData(t, a, length, dir27, action27, dis27));
                }
                if (check28.isSelected()) {
                    actions.add(getFXData(t, a, length, dir28, action28, dis28));
                }
                if (check29.isSelected()) {
                    actions.add(getFXData(t, a, length, dir29, action29, dis29));
                }
                if (check30.isSelected()) {
                    actions.add(getFXData(t, a, length, dir30, action30, dis30));
                }
                if (check31.isSelected()) {
                    actions.add(getFXData(t, a, length, dir31, action31, dis31));
                }
                if (check32.isSelected()) {
                    actions.add(getFXData(t, a, length, dir32, action32, dis32));
                }
                if (check33.isSelected()) {
                    actions.add(getFXData(t, a, length, dir33, action33, dis33));
                }
                if (check34.isSelected()) {
                    actions.add(getFXData(t, a, length, dir34, action34, dis34));
                }
                if (check35.isSelected()) {
                    actions.add(getFXData(t, a, length, dir35, action35, dis35));
                }
                if (check36.isSelected()) {
                    actions.add(getFXData(t, a, length, dir36, action36, dis36));
                }
                if (check37.isSelected()) {
                    actions.add(getFXData(t, a, length, dir37, action37, dis37));
                }
                if (check38.isSelected()) {
                    actions.add(getFXData(t, a, length, dir38, action38, dis38));
                }
                if (check39.isSelected()) {
                    actions.add(getFXData(t, a, length, dir39, action39, dis39));
                }
                if (check40.isSelected()) {
                    actions.add(getFXData(t, a, length, dir40, action40, dis40));
                }

                //① 至少选中一个，再停下来充电
                if (actions.size() != 0) {
                    // ②最最后一个任务执行完成，理应不再发送原地充电任务。 目的(现场反馈)：最后一个动作完成，界面执行按钮可以立即解禁
                    if (i < m) {
                        actions.add(new shuttleAction(0, 0, 1 * 10000));     //一轮循环，在充电桩停留时间
                    }
                }
            }


            if(actions.size()==0){
                DialogBuilder.alert(start,"无任务可执行");
                start.setDisable(false);
                return;
            }

            // todo 线程发送任务  防止界面卡死 (周末任务)
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    pool.execute(new FlashTask(actions, fc, start));
                    // pool.shutdownNow();
                }
            });
        } catch (Exception e) {
            times.setDisable(false);
            start.setDisable(false);
            DialogBuilder.alert(start, "Please check the data");
            LogManager.getLogger().debug(e.getMessage());
        }
    }

    /**
     * 获取界面数据
     */
    protected shuttleAction getFXData(int t, int a, int l, ChoiceBox dir, ChoiceBox act, TextField dis) {
        t = 0;
        a = 0;
        l = 0;
        String direction = (String) dir.getValue();
        String action = (String) act.getValue();
        String distance = dis.getText();
        if (direction.equals("移动")) {
            t = 1;
        } else {
            t = 2;
        }
        //"向前", "向后", "向左", "向右"
        if (t == 1) {
            if (action.endsWith("向前")) {
                a = FlashInterface.DIRECTION_FORWARD;
            } else if (action.endsWith("向后")) {
                a = FlashInterface.DIRECTION_BACK;
            } else if (action.endsWith("向左")) {
                a = FlashInterface.DIRECTION_LEFT;
            } else {
                a = FlashInterface.DIRECTION_RIGHT;
            }
        } else {
            if (action.endsWith("左取")) {
                a = FlashInterface.PICK_UP_LEFT;
            } else if (action.endsWith("左放")) {
                a = FlashInterface.PUT_DOWN_LEFT;
            } else if (action.endsWith("右取")) {
                a = FlashInterface.PICK_UP_RIGHT;
            } else if (action.endsWith("右放")) {
                a = FlashInterface.PUT_DOWN_RIGHT;
            } else if (action.endsWith("左取2")) {
                // a = FlashInterface.PICK_UP_LEFT_2;
                a = FlashInterface.CLAMP_PICK_UP_LEFT_2; //接口差异导致
            } else if (action.endsWith("左放2")) {
                // a = FlashInterface.PUT_DOWN_LEFT_2;
                a = FlashInterface.CLAMP_PUT_DOWN_LEFT_2; //接口差异导致
            } else if (action.endsWith("右取2")) {
                // a = FlashInterface.PICK_UP_RIGHT_2;
                a = FlashInterface.CLAMP_PICK_UP_RIGHT_2; //接口差异导致
            } else if (action.endsWith("右放2")) {
                // a = FlashInterface.PUT_DOWN_RIGHT_2;
                a = FlashInterface.CLAMP_PUT_DOWN_RIGHT_2; //接口差异导致
            } else {
                LogManager.getLogger().debug("动作指令异常： " + action);
            }
        }
        if (t == 1) {
            l = Integer.valueOf(distance);
        }
        return new shuttleAction(t, a, l);
    }

    // 监听choiceBox1 改变choiceBox2
    private void choiceBoxChangeListener(ChoiceBox dir, ChoiceBox action, Label label, TextField dis) {
        dir.getItems().addAll("移动", "取货", "放货");
        dir.getSelectionModel().select("移动");
        action.getItems().addAll("向前", "向后", "向左", "向右");
        action.getSelectionModel().select("向前");

        //设置选择监听
        dir.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                String dir_val = dir.getItems().get(newValue.intValue()).toString();
                if (oldValue != newValue) {
                    action.getItems().clear();
                    if (dir_val.equals("移动")) {
                        action.getItems().addAll("向前", "向后", "向左", "向右");
                        action.getSelectionModel().select("向前");

                        // label.setText("距离(mm)");
                        label.setTextFill(Color.BLACK);
                        dis.setDisable(false);


                    } else if (dir_val.equals("取货")) {

                        action.getItems().addAll("左取", "右取", "左取2", "右取2");
                        action.getSelectionModel().select("左取");
                        // label.setText("距离(mm)");
                        label.setTextFill(Color.GREEN);
                        dis.setDisable(true);
                        // LogManager.getLogger().debug("取货 Trigger default condition");

                    } else if (dir_val.equals("放货")) {

                        action.getItems().addAll("左放", "右放", "左放2", "右放2");
                        action.getSelectionModel().select("左放");
                        // label.setText("距离(mm)");
                        label.setTextFill(Color.GREEN);
                        dis.setDisable(true);
                        // LogManager.getLogger().debug("放货 Trigger default condition ");
                    }
                }
            }
        });
    }

    //中文版阉割部分功能
    public static final String TYPE1 = "V4";
    public static final String TYPE2 = "V4.5";
    public static final String TYPE3 = "V4单深夹抱";
    public static final String TYPE4 = "V5 NEW";
    private static final String LOG_ITEM1 = "查看日志";
    private static final String CONFIG_ITEM1 = "保存配置";
    private static final String CONFIG_ITEM2 = "加载配置";
    private static final String DB_ITEM1 = "MySQL";
    private static final String VERSION_ITEM1 = "当前版本";
    private static final String VERSION_ITEM2 = "历史版本";
    private static final String LANGUAGE_ITEM1 = "中文";
    private static final String LANGUAGE_ITEM2 = "英文";
}
