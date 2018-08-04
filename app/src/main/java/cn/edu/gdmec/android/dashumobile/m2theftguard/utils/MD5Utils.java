package cn.edu.gdmec.android.dashumobile.m2theftguard.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 达叔小生 on 2018/8/4.
 * email:2397923107@qq.com
 * version: 1.0
 */

public class MD5Utils {
    public static String encode(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(text.getBytes());
            StringBuilder sb = new StringBuilder();

            for (byte b : result) {
                int number = b & 0xff;
                String hex = Integer.toHexString(number);

                if (hex.length() == 1) {
                    sb.append("0" + hex);
                } else {
                    sb.append(hex);
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
