/**
 * Copyright (C),2019-09-04,Galaxis
 * Date:2019/9/4 0004 9:18
 * Author:LELE
 * Description:
 */

package galaxis.lee.db;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.FXMLView;

import galaxis.lee.senceControl.BaseFXController;
import galaxis.lee.view.DialogBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

@FXMLView(value = "/fxml/ConfigDB.fxml")
@FXMLController
public class ConfigDBController extends BaseFXController {

    @FXML
    protected ChoiceBox<String> dbTypeChoice;
    @FXML
    protected TextField hostField;
    @FXML
    protected TextField portField;
    @FXML
    protected TextField userNameField;
    @FXML
    protected TextField passwordField;
    @FXML
    protected TextField schemaField;
    @FXML
    protected ChoiceBox<String> encodingChoice;


    private DatabaseConfig getUIConfig() {
        DatabaseConfig db = new DatabaseConfig();
        db.setDbType(dbTypeChoice.getValue());
        db.setHost(hostField.getText());
        db.setPort(portField.getText());
        db.setUsername(userNameField.getText());
        db.setPassword(passwordField.getText());
        db.setSchema(schemaField.getText());
        db.setEncoding(encodingChoice.getValue());
        return db;
    }

    @FXML
    void saveDBConfig() {
        if (StringUtils.isAnyEmpty(dbTypeChoice.getValue(), hostField.getText(), portField.getText(),
                userNameField.getText(), passwordField.getText(), schemaField.getText(), encodingChoice.getValue() )) {
            DialogBuilder.alert(dbTypeChoice,"所有字段必填");
            return;
        }
        DatabaseConfig databaseConfig= getUIConfig();
        DBConfigUtil.configFile(databaseConfig);
        DialogBuilder.alert(dbTypeChoice,"配置成功");
        getDialogStage().close();
    }

    @FXML
    void connectTest(){
        if (StringUtils.isAnyEmpty(dbTypeChoice.getValue(), hostField.getText(), portField.getText(),
                userNameField.getText(), passwordField.getText(), schemaField.getText(), encodingChoice.getValue() )) {
            DialogBuilder.alert(dbTypeChoice,"所有字段必填");
            return;
        }
        DatabaseConfig databaseConfig= getUIConfig();
        DBConfigUtil.configFile(databaseConfig);
        Connection con  = ConnectionUtil.conDB();
        if (con != null) {
            DialogBuilder.alert(dbTypeChoice, "数据库连接成功");
        } else {
            DialogBuilder.alert(dbTypeChoice, "数据库连接失败");
        }
        // showlable(con);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void cancel() {
        getDialogStage().close();
    }


}
