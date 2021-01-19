package galaxis.lee.senceControl;

/**
 * @Author: Lee
 * @Date: Created in 16:02 2020/8/27
 * @Description: TODO
 */
public class Version {

    //新增菜单栏
    //任务线程优化
    // public static final String VERSION1 = "V1.0\n" +
    //         "\tInterface optimization, new menu bar\n"+
    //         "\ttasks are sent by threads\n";
    public static final String VERSION1 = "V1.0\n" +
            "\t1.界面优化,新增菜单栏\n"+
            "\t2.系统优化,任务为线程发送\n";
    public static final String VERSION2 = "V1.1\n" +
            "\t1.切换英文版，展会专用\n"+
            "\t2.日志刷新优化\n" +
            "\t3.任务循环间隔修改3S，充电3S\n";
    public static final String VERSION3 = "V10.13\n" +
            "\t1.优化日志显示卡死，显示界面默认展示1000条,可查看 FlashControl.log\n"+
            "\t2.任务组添加至40组，方便多段路径演示\n" +
            "\t3.任务循环间隔修改10S，充电10S，通用\n";
    //加载配置与监听器冲突，每个配置文件后台加载两次
    public static final String VERSION4 = "V12.01\n" +
            "\t1.加载配置与监听器冲突，导致保存配置异常；每个配置文件后台加载两次\n" +
            "\t2.保存配置，只保存勾选的配置\n";


    public static String showVersions(){
        //把所有更新进行拼接
        return VERSION1+VERSION2+VERSION3+VERSION4;
    }
    public static String currentVersion(){
        //把所有更新进行拼接
        return VERSION4;
    }
}
