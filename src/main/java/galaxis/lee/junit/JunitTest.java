package galaxis.lee.junit;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import galaxis.lee.db.DBConnectUtil;
import galaxis.lee.util.BitByteChange;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * @Author: Lee
 * @Date: Created in 9:27 2019/9/12
 * HEX，Hexadecimal ，十六进制。
 * DEC，Decimal ，十进制。
 * OCT，Octal ，八进制。
 * BIN，Binary ，二进制。
 *
 * todo 一、wcs下发动作指令的结构
 *
 * 指令编号     	2
 * 穿梭车编号	2
 * 命令       	2	1-直行；2-横行；3-取货；4-放货
 * 预留1      	2
 * 预留2      	4
 * 直行距离     	8
 * 横行距离     	8
 * 预留3      	4
 * 取放货类型(Type)	2	高位字节表示货在货位上的状态，其中bit7为0表示左边bit7为1表示右边，低字节表示货物在车上的位置
 * 填充至      40个字节
 *
 * 货位状态信息：0-无货、1-有货
 * TypeH解释
 * Bit7 	Bit6    	Bit5        	Bit4        	Bit3    	Bit2    	Bit1    	Bit0
 * Dir      是否为大箱子	是否在层间线上	是否是倒货指令	            左侧货位    	中间货位    	右侧货位
 *
 * Bit7：取放货方向
 * Bit6：当出现货架上货位有两个1存在的时候，0-两个小箱子、1-一个大箱子
 * Bit5：层间线上取货距离和货位上不一样，该bit表示是否是层间线上的货物
 * Bit4：倒货指令，当这一位置1时，bit2~bit0表示的时原车上货位状态
 * Bit3：预留
 * Bit2~Bit0：以车的方向为基准，左中右位置的货位状态
 *
 * Todo 二、状态上报
 * 使用ST和SD的应答报文来上报车的货位状态
 * 报文结构
 *
 * 指令编号     	2
 * 穿梭车编号	2
 * 命令       	2	ST或SD
 * 穿梭车状态	2	高位字节表示车内货物状态（参照TypeL），低位字节表示自动还是手动
 * 信息       	2
 */
@RunWith(SpringRunner.class)
public class JunitTest {
    protected int send_length = 40;

    @Test
    public void test_BIN() {
        // int a = 0x84;
        // int b = 0x04;
        // System.out.println(a+","+b);
        // int c = 0x91;
        // int d = 0x04;
        // System.out.println(c+","+d);
        byte low = 7;
        byte height = 84;
        if ((low & 0x01) != 0) {
            System.out.println("右侧有货");
        } else {
            System.out.println("右侧无货");
        }
        if ((low & 0x02) != 0) {
            System.out.println("中间有货");
        } else {
            System.out.println("中间无货");
        }
        if ((low & 0x04) != 0) {
            System.out.println("左侧有货");
        } else {
            System.out.println("左侧无货");
        }
        if ((low & 0x08) != 0) {
            System.out.println("预留状态为1");
        } else {
            System.out.println("预留状态为0");
        }
        if ((height & 0x16) != 0) {
            System.out.println("倒货");
        } else {
            System.out.println("不倒货");
        }
        if ((height & 0x32) != 0) {
            System.out.println("在层间线上");
        } else {
            System.out.println("不在层间线上");
        }
        if ((height & 0x64) != 0) {
            System.out.println("大箱子");
        } else {
            System.out.println("不是大箱子");
        }
        if ((height & 0x128) != 0) {
            System.out.println("向左操作");
        } else {
            System.out.println("向右操作");
        }
    }

    @Test
    public void BIN_calculate() {
        //&
        int a = 3;
        int b = 4;
        /**
         * 双目运算符
         * int 4字节
         * 二进制3== 0b 00000000 00000000 00000000 00000011
         * 二进制4== 0b 00000000 00000000 00000000 00000100
         * 3 & 4 == 0b 00000000 00000000 00000000 00000000
         * 3 | 4 == 0b 00000000 00000000 00000000 00000111
         * 3 ^ 4 == 0b 00000000 00000000 00000000 00000111
         * ~3    == 0b 11111111 11111111 11111111 11111100  变成补码
         */
        System.out.println(3 & 4);
        System.out.println(3 | 4);
        System.out.println(3 ^ 4);//异或
        //单目运算
        System.out.println(~3);//取反
        System.out.println(~4);

        System.out.println(a ^ b ^ b); //加密应用(一个数异或同一个数两次，是本身)

        //位运算，变量值交换，不用中间变量
        a = a ^ b;
        b = a ^ b; //b= a^b^b
        a = a ^ b; //a= a^a^b
        System.out.println("a:" + a + ",b:" + b);

        /**
         * 位移运算符
         * << 左移        ：操作数×2的n次幂,  n移动的位数
         * >> 右移        ：操作数÷2的n次幂,  n移动的位数
         * >>> 无符号右移
         */
        int m = 7;
        //

        /**
         * 7原码： 00000000 00000000 00000000 00000111
         * 7<<2 ： 7*2^2 = 28
         * 7<<2 ： 00000000 00000000 00000000 00011100 = 16+8+4
         */
        System.out.println("左移 m:" + (7 << 2));     //字面结果：28
        /**
         * 7原码： 00000000 00000000 00000000 00000111
         * 7>>2 ： 7 / 2^2 = 1
         * 7>>2 ： 00000000 00000000 00000000 00000001
         */
        System.out.println("右移 m:" + (7 >> 2));     //字面结果：1

        /**
         * -7原码： 10000000 00000000 00000000 00000111
         * -7反码： 11111111 11111111 11111111 11111000
         * -7补码： 10000000 00000000 00000000 00000111
         */
    }


    @Test
    public void testDB() {
        // LogManager.getLogger().debug("FlashTool log test !");
        Connection conn = DBConnectUtil.conDB();
        String sql = "select * from wip_users";
        try {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                System.out.println(rs.getMetaData().getColumnName(i + 1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private BufferedReader in;

    @Test
    public void cleanLog() {
        try {
            if (in != null) {
                in.close();
            }

            in = new BufferedReader(
                    new InputStreamReader(new FileInputStream(new File("./FlashTool.log"))));
            FileOutputStream out = new FileOutputStream("./FlashTool.log", false);
            out.write(new String("").getBytes());
            out.close();
            // detailLog.setText("");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Test
    public void t1() {
        // boolean isClamp = false;
        // System.out.println(isClamp ? FlashInterface.CLAMP_PUT_DOWN_LEFT_2 : FlashInterface.PUT_DOWN_LEFT_2);
        // boolean isClamp1 = true;
        // System.out.println(isClamp1 ? FlashInterface.CLAMP_PUT_DOWN_LEFT_2 : FlashInterface.PUT_DOWN_LEFT_2);
        byte a = (byte) 0x84;
        byte b = 0x04;
        System.out.println(BitByteChange.byteToBit(a));
        System.out.println(BitByteChange.byteToBit(b));
        // System.out.println(a + "," + b);
        byte a1 = (byte) 0x91;
        byte b1 = 0x04;
        System.out.println(BitByteChange.byteToBit(a1));
        System.out.println(BitByteChange.byteToBit(b1));

        byte f = (byte) 0xc4;
        System.out.println(BitByteChange.byteToBit(f));


    }
}
