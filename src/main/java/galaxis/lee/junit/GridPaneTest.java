package galaxis.lee.junit;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @Author: Lee
 * @Date: Created in 15:46 2020/8/4
 * @Description: TODO
 */
public class GridPaneTest extends Application{

    private Map map = null;

    //这个是准备的数据  用来模拟登录使用
    @Override
    public void init() throws Exception {

        map = new HashMap<String,String>();
        map.put("username", "password");
    }

    @Override
    public void start(Stage stage) throws Exception {

        Text name = new Text("用户名：");
        Text password = new Text("密码：");

        TextField e_name = new TextField();
        e_name.setPrefWidth(150);

        PasswordField e_password = new PasswordField();

        Button clear = new Button("清除");
        Button login = new Button("登录");

        //网格布局
        GridPane gridPane = new GridPane();
        //设置网格位置
        gridPane.add(name, 0, 0);
        gridPane.add(e_name, 1,0);
        gridPane.add(password, 0, 1);
        gridPane.add(e_password, 1, 1);
        gridPane.add(clear, 0,2);
        gridPane.add(login, 1,2);

        //设置单独组件的上下左右的间距
        GridPane.setMargin(login, new Insets(0,0,0,115));

        gridPane.setAlignment(Pos.CENTER);
        //设置垂直间距
        gridPane.setVgap(10);
        //设置水平间距
        gridPane.setHgap(5);

        Scene scene = new Scene(gridPane);

        stage.setScene(scene);
        stage.setTitle("布局类学习");
        stage.setHeight(300);
        stage.setWidth(500);
        stage.show();

        //绑定监听事件 clear清除
        clear.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                //单击清除用户名和密码
                e_name.setText("");
                e_password.setText("");

            }
        });

        //对登录按钮绑定监听事件
        login.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                String username = null;
                String password = null;
                //登录成功后
                Set set = map.entrySet();

                for (Object object : set) {

                    String[] strings = object.toString().split("=");

                    username = strings[0];
                    password = strings[1];

                }

                if(e_name.getText().equals(username) && e_password.getText().equals(password)){

                    stage.close();

                    Stage stage2 = new Stage();

                    stage2.setHeight(300);
                    stage2.setWidth(500);
                    stage2.show();
                }else{

                    System.out.println("密码输入错误");
                }

            }
        });

    }

    public static void main(String[] args) {

        launch(args);
    }

}
