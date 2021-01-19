package galaxis.lee.util;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.awt.event.ItemEvent;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @Author: Lee
 * @Date: Created in 11:36 2020/8/31
 * @Description: TODO 中英文切换  全手动切换
 */
public class LanguageChange {

    /**
     * 变动
     * 1.菜单栏
     * 2.top
     * 3.center
     * 3.button
     */

    public void toEnglish(MenuBar mb,HBox hbox_top,GridPane grid,HBox hbox_end){
        ResourceBundle bundle = ResourceBundle.getBundle("language", new Locale("zh", "CN"));
        String cancel = bundle.getString("cancelKey");
        ObservableList<Menu> menus = mb.getMenus();
        for(Menu menu : menus){
           // menu.getText().equals()
        }
        System.out.println(cancel);
    }


}
