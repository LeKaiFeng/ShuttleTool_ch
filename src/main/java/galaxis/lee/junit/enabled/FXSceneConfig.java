package galaxis.lee.junit.enabled;


import galaxis.lee.log.LogManager;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @Author: Lee
 * @Date: Created in 12:52 2020/8/5
 * @Description: TODO Config    弃用——只等保存加载指定配置文件
 */
public class FXSceneConfig {
    //默认目录
    String defaultDirectory = "e:/V4Double/V4Double_FX/";
    //默认文件名
    String defaultFilename = "cxf.bmp";
    /**
     * TODO save Config
     * read Node —— write config
     */
     Properties  pros = new Properties();
    protected  String choiceValue = "";    //复选框值
    protected  String field = "";          //文本框值
    protected  boolean check = false;      //勾选框值

    public  void saveNodeValue(HBox hbox_top, GridPane grid, HBox hbox_end) {
        for (Node n : hbox_top.getChildren()) {
            getNodeValue(n);
        }
        for (Node node : grid.getChildren()) {
            if (node.getClass().toString().equals("class javafx.scene.layout.HBox")) {
                HBox hBox = (HBox) node;
                for (Node nd : hBox.getChildren()) {
                    getNodeValue(nd);
                }
            }
        }
        for (Node n : hbox_end.getChildren()) {
            getNodeValue(n);
        }



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

    public  void getNodeValue(Node nd) {
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


    /**
     * TODO load config
     * read config —— write node
     * mapper name -> Node
     */
    private Map<String, Node> map = new HashMap<>();

    public void nodeFillMap(Node nd){
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

    public void getSceneNode(HBox hbox_top,GridPane grid,HBox hbox_end) {
        for(Node node : hbox_top.getChildren()){
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
        for(Node node : hbox_end.getChildren()){
            nodeFillMap(node);
        }
    }


    public void fillSceneNode(HBox hbox_top,GridPane grid,HBox hbox_end) {

        getSceneNode(hbox_top,grid,hbox_end);
        File file = new File("./config.con");
        if (!file.exists()) {
            LogManager.getLogger().debug("Config does not exist！");
            return;
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            Properties pros = new Properties();
            pros.load(in);

            Iterator<Object> iterator = pros.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = pros.getProperty(key);
                // LogManager.getLogger().debug(key + "->> " + value);
                System.out.println(key + "->> " + value);
                Node nd = map.get(key);
                if (nd.getClass().toString().equals("class javafx.scene.control.ChoiceBox")) {
                    ChoiceBox choiceBox = (ChoiceBox) nd;
                    choiceBox.setValue(value);
                } else if (nd.getClass().toString().equals("class javafx.scene.control.TextField")) {
                    TextField textField = (TextField) nd;
                    textField.setText(value);
                } else if (nd.getClass().toString().equals("class com.jfoenix.controls.JFXCheckBox")) {
                    CheckBox checkBox = (CheckBox) nd;
                    checkBox.setSelected(Boolean.valueOf(value));
                }
            }
            LogManager.getLogger().debug("Config load success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
