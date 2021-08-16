package com.monetware.ringsurvey.system.util.pay;


import com.monetware.ringsurvey.system.util.codec.MD5;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Cookie on 2018/11/20.
 */
public class CPayUtil {

    private static long seq = 99999999L;

    /**
     *
     * @Author: Cookie
     * @Date: 2018/11/20 13:39
     * @Description: 生成流水号
     *
     */
    public static synchronized String getTradeNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");//15位
        String dateTime = sdf.format(new Date());
        String seqStr = getSeq();//8
        return dateTime + "000" + getRand(6) + seqStr;
    }


    /**
     *
     * @Author: Cookie
     * @Date: 2018/11/20 13:39
     * @Description: 格式化8位数据序列号
     *
     */
    private static String getSeq() {
        int len = 8;
        String seqStr = String.format("%07d", seq++);//7
        if (seqStr.length() > len) {
            seqStr = seqStr.substring(seqStr.length() - len);
        }
        return seqStr;
    }

    /**
     *
     * @Author: Cookie
     * @Date: 2018/11/20 13:41
     * @Description: 生成指定长度的随机码
     *
     */
    private static String getRand(int round) {
        String str = MD5.encode(System.currentTimeMillis() + "" + (int) (Math.random() * 10000));
        return str.substring(0, round).toUpperCase();
    }

//    public static void main(String[] args) {
//        System.out.println(getTradeNum().length());
//        for (int i = 1;i<10;i++){
//            System.out.println(getTradeNum());
//        }
//    }
}
