package com.znh.siemens.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author JayNH
 * @Description TODO IP工具类
 * @Date 2023-05-27 14:21
 * @Version 1.0
 */
public class IpAddressUtil {

    public static boolean isIP(String addr) {
        if (addr == null || addr.length() < 7 || addr.length() > 15) {
            return false;
        }
        String rep =
                "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rep);
        Matcher mat = pat.matcher(addr);
        return mat.find();
    }

    /**
     * 通过IP获取地址(需要联网，调用淘宝的IP库)
     *
     * @param ip ip地址
     */
    public static String getIpInfo(String ip) {
        if ("127.0.0.1".equals(ip)) {
            ip = "127.0.0.1";
        }
        String info = "";
        try {
            URL url = new URL("http://ip.taobao.com/service/getIpInfo.php?ip=" + ip);
            HttpURLConnection httpOnly = (HttpURLConnection) url.openConnection();
            httpOnly.setRequestMethod("GET");
            httpOnly.setDoOutput(true);
            httpOnly.setDoInput(true);
            httpOnly.setUseCaches(false);
            InputStream in = httpOnly.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            StringBuilder temp = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                temp.append(line).append("\r\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            JSONObject obj = (JSONObject) JSON.parse(temp.toString());
            if (obj.getIntValue("code") == 0) {
                JSONObject data = obj.getJSONObject("data");
                info += data.getString("country") + " ";
                info += data.getString("region") + " ";
                info += data.getString("city") + " ";
                info += data.getString("isp");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return info;
    }
}
