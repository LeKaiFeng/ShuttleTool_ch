package galaxis.lee.senceControl;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

/**
 * @Author: Lee
 * @Date: Created in 12:19 2020/8/5
 * @Description: TODO
 */
public class FXValue {

    private String dir;
    private String act;
    private int dis;
    private boolean check;

    public FXValue(ChoiceBox direction, ChoiceBox action, TextField distance, CheckBox check) {
        this.dir = (String) direction.getValue();
        this.act = (String) action.getValue();
        // this.dis = Integer.valueOf(distance.getText());
        this.dis = ("".equals(distance.getText())) ? 0 : Integer.valueOf(distance.getText());
        this.check = check.isSelected();

    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getAct() {
        return act;
    }

    public void setAct(String act) {
        this.act = act;
    }

    public int getDis() {
        return dis;
    }

    public void setDis(int dis) {
        this.dis = dis;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public String toString() {
        return "{" +
                "dir='" + dir +
                ", act='" + act +
                ", dis=" + dis +
                ", check=" + check +
                '}';
    }
}
