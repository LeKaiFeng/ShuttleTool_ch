package galaxis.lee.util;

import com.jfoenix.controls.JFXCheckBox;
import galaxis.lee.log.LogManager;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: Lee
 * @Date: Created in 15:58 2020/8/26
 * @Description: TODO 与配置相关
 *                  优化
 *                  1.保存配置： 可以保存多个配置，
 *                  2.加载配置： 可灵活选用任意配置，方便测试
 */
public class FileUtil {
    Properties pros = new Properties();
    protected String choiceValue = "";    //复选框值
    protected String field = "";          //文本框值
    protected boolean check = false;      //勾选框值

    String defaultDirectory;    //默认选择目录
    String defaultFilename = "config.con";                             //默认文件名

    /**
     * TODO save: 获取界面每一个节点数据，一一对应，保存到配置文件
     */
    public void saveNodeValue(HBox hbox_top, GridPane grid, HBox hbox_end, Stage stage) {
        for (Node n : hbox_top.getChildren()) {
            getNodeValue(n);
        }
        for (Node node : grid.getChildren()) {
            if (node.getClass().toString().equals("class javafx.scene.layout.HBox")) {
                HBox hBox = (HBox) node;
                //HBox 下的 JFXCheckBox 为选中状态才进行保存
                //步骤：遍历出JFXCheckBox，检查是否选中，选中则保存这个HBox下的所有配置


                //检查是否选中
                JFXCheckBox mycheck = (JFXCheckBox) hBox.getChildren().get(4);

                if (mycheck.isSelected()) {
                    for (int i = 0; i < hBox.getChildren().size(); i++) {
                        getNodeValue(hBox.getChildren().get(i));
                    }
                }
                // for (Node nd : hBox.getChildren()) {
                //     if (nd.getClass().toString().equals("class com.jfoenix.controls.JFXCheckBox")) {
                //         JFXCheckBox choose = (JFXCheckBox) nd;
                //         if (choose.isSelected()) {
                //             for (Node selectNode : hBox.getChildren()) {
                //                 getNodeValue(selectNode);
                //             }
                //         }
                //     }
                // }
            }
        }
        for (Node n : hbox_end.getChildren()) {
            getNodeValue(n);
        }
        showSaveConfigDialog(stage);

        //只能单独保存一个配置
     /*   try {
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(new File("./config.con"))));
            this.pros.store(writer, "");
            LogManager.getLogger().debug("config save success");
        } catch (IOException var8) {
            var8.printStackTrace();
        }*/
    }

    public void getNodeValue(Node nd) {
        if (nd.getClass().toString().equals("class javafx.scene.control.ChoiceBox")) {
            ChoiceBox choiceBox = (ChoiceBox) nd;
            String id = choiceBox.getId();
            choiceValue = (String) choiceBox.getValue();
            pros.put(id, choiceValue);
        } else if (nd.getClass().toString().equals("class javafx.scene.control.TextField")) {
            TextField textField = (TextField) nd;
            String id = textField.getId();
            field = textField.getText();
            pros.put(id, field);
        } else if (nd.getClass().toString().equals("class com.jfoenix.controls.JFXCheckBox")) {
            CheckBox checkBox = (CheckBox) nd;
            String id = checkBox.getId();
            check = checkBox.isSelected();
            pros.put(id, String.valueOf(check));
        }
    }

    //弹窗式,自命名保存配置
    protected void showSaveConfigDialog(Stage primaryStage) {
        FileChooser chooser = new FileChooser(); // 创建一个文件对话框
        chooser.setTitle("save config"); // 设置文件对话框的标题
        defaultDirectory = System.getProperty("user.dir");
        //设置默认目录
        chooser.setInitialDirectory(new File(defaultDirectory));
        //设置默认文件名
        chooser.setInitialFileName(defaultFilename);
        // 创建一个文件类型过滤器
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("File(*.con)", "*.con");
        // 给文件对话框添加文件类型过滤器
        chooser.getExtensionFilters().add(filter);
        File file = chooser.showSaveDialog(primaryStage); // 显示文件保存对话框
        if (file == null) { // 文件对象为空，表示没有选择任何文件
            System.out.println("please choose a config!");
        } else { // 文件对象非空，表示选择了某个文件
            if (!file.getPath().endsWith(".con")) {
                file = new File(file.getPath() + ".con");
            }
            BufferedWriter writer = null;
            try {
                if (!file.exists()) {//文件不存在 则创建一个
                    file.createNewFile();
                }
                //TODO----------------------
                LogManager.getLogger().debug("save config path：" + file.getAbsolutePath());
                writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(file)));
                pros.store(writer, "");
                LogManager.getLogger().debug("config save success");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }


    /**
     * TODO load：获取界面每一个节点，将读取的配置文件数据，一一对应，写入每个节点
     */

    private Map<String, Node> map = new HashMap<>();

    public void nodeFillMap(Node nd) {
        if (nd.getClass().toString().equals("class javafx.scene.control.ChoiceBox")) {
            ChoiceBox choiceBox = (ChoiceBox) nd;
            map.put(choiceBox.getId(), choiceBox);
        } else if (nd.getClass().toString().equals("class javafx.scene.control.TextField")) {
            TextField textField = (TextField) nd;
            map.put(textField.getId(), textField);
        } else if (nd.getClass().toString().equals("class com.jfoenix.controls.JFXCheckBox")) {
            CheckBox checkBox = (CheckBox) nd;
            map.put(checkBox.getId(), checkBox);
        }
    }

    public void getSceneNode(HBox hbox_top, GridPane grid, HBox hbox_end) {
        for (Node node : hbox_top.getChildren()) {
            nodeFillMap(node);
        }
        for (Node node : grid.getChildren()) {
            if (node.getClass().toString().equals("class javafx.scene.layout.HBox")) {
                HBox hBox = (HBox) node;
                for (Node nd : hBox.getChildren()) {
                    nodeFillMap(nd);
                }
            }
        }
        for (Node node : hbox_end.getChildren()) {
            nodeFillMap(node);
        }

    }


    public void fillSceneNode(HBox hbox_top, GridPane grid, HBox hbox_end, Stage stage) {

        getSceneNode(hbox_top, grid, hbox_end);
        //可选定的加载任意配置文件
        // File file = new File("./config.con");
        File file = loadOneConfig(stage);
        if (file == null || !file.exists()) {
            LogManager.getLogger().debug("Config does not exist！");
            return;
        }
        BufferedReader in = null;
        try {
            //todo 12.1 修改
            // 问题：加载配置文件的load/unload 会触发监听器的默认选项，即下拉框改变，后面的下拉框就默认选中第一条，两者冲突
            // 解决办法：后台读两遍配置文件，加载两次
            in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            Properties pros = new Properties();
            pros.load(in);
            for (int i = 0; i < 2; i++) {

                Iterator<Object> iterator = pros.keySet().iterator();
                while (iterator.hasNext()) {

                    String key = (String) iterator.next();
                    String value = pros.getProperty(key);
                    if (i == 1) {
                        LogManager.getLogger().debug(key + "->> " + value);
                    }
                    // System.out.println(key + "->> " + value);
                    Node nd = map.get(key);
                    if (nd.getClass().toString().equals("class javafx.scene.control.ChoiceBox")) {
                        ChoiceBox choiceBox = (ChoiceBox) nd;
                        String id = choiceBox.getId();
                        if (id.equals(key)) {
                            choiceBox.setValue(value);
                        }
                    } else if (nd.getClass().toString().equals("class javafx.scene.control.TextField")) {
                        TextField textField = (TextField) nd;
                        textField.setText(value);
                    } else if (nd.getClass().toString().equals("class com.jfoenix.controls.JFXCheckBox")) {
                        CheckBox checkBox = (CheckBox) nd;
                        checkBox.setSelected(Boolean.valueOf(value));
                    }
                }

            }
            LogManager.getLogger().debug("Config load success");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //弹窗式，加载任意配置
    public File loadOneConfig(Stage primaryStage) {
        FileChooser chooser = new FileChooser(); // 创建一个文件对话框
        chooser.setTitle("load config"); // 设置文件对话框的标题
        defaultDirectory = System.getProperty("user.dir");
        chooser.setInitialDirectory(new File(defaultDirectory)); // 设置文件对话框的初始目录
        // 给文件对话框添加多个文件类型的过滤器
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All file", "*.con*"),
                new FileChooser.ExtensionFilter("All pictures", "*.jpg", "*.gif", "*.bmp", "*.png"));
        // 显示文件打开对话框，且该对话框支持同时选择多个文件
        File file = chooser.showOpenDialog(primaryStage); // 显示文件打开对话框
        if (file == null) { // 文件对象为空，表示没有选择任何文件
            LogManager.getLogger().debug("please choose a config");
        } else { // 文件对象非空，表示选择了某个文件
            LogManager.getLogger().debug("load config path：" + file.getAbsolutePath());
        }
        return file;
    }


}
