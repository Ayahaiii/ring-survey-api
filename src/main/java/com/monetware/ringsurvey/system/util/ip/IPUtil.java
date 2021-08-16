package com.monetware.ringsurvey.system.util.ip;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monetware.ringsurvey.system.config.BaiduConfig;
import com.monetware.ringsurvey.system.util.spring.SpringBeanUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class IPUtil {

    /**
     * 获取指定IP对应的经纬度（为空返回当前机器经纬度）
     *
     * @param ip
     * @return
     */
    public static String[] getIPXY(String ip) {
        BaiduConfig baiduConfig = SpringBeanUtil.getBean(BaiduConfig.class);
        String ak = baiduConfig.getAk();
        if (null == ip) {
            ip = "";
        }

        try {
            URL url = new URL("http://api.map.baidu.com/location/ip?ak=" + ak
                    + "&ip=" + ip + "&coor=bd09ll");
            InputStream inputStream = url.openStream();
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputReader);
            StringBuffer sb = new StringBuffer();
            String str;
            do {
                str = reader.readLine();
                sb.append(str);
            } while (null != str);

            str = sb.toString();
            if (null == str || str.isEmpty()) {
                return null;
            }

            // 获取坐标位子
            int index = str.indexOf("point");
            int end = str.indexOf("}}", index);

            if (index == -1 || end == -1) {
                return null;
            }

            str = str.substring(index - 1, end + 1);
            if (null == str || str.isEmpty()) {
                return null;
            }

            String[] ss = str.split(":");
            if (ss.length != 4) {
                return null;
            }

            String x = ss[2].split(",")[0];
            String y = ss[3];
            x = x.substring(x.indexOf("\"") + 1, x.indexOf("\"", 1));
            y = y.substring(y.indexOf("\"") + 1, y.indexOf("\"", 1));

            return new String[]{x, y};
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ip获取城市地域信息
     *
     * @param ip
     * @return
     */
    public static IpInfo ip2Region(String ip) {
        String url = "http://whois.pconline.com.cn/ipJson.jsp";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> map = new HashMap<>();
        map.put("ip", ip);
        map.put("json", true);
        HttpHeaders headers = new HttpHeaders();
        //  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        //  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        ObjectMapper mapper = new ObjectMapper();
        String value = null;
        try {
            value = mapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(value, headers);
        ResponseEntity response = restTemplate.postForEntity(url, requestEntity, String.class);
        String res = response.getBody().toString();
        Document doc = Jsoup.parse(res);
        String resText = doc.body().text().trim();
        String realText = resText.substring(resText.lastIndexOf("{"), resText.indexOf("}") + 1);
//        System.out.println(realText);
        return JSONObject.parseObject(realText, IpInfo.class);
    }
}
