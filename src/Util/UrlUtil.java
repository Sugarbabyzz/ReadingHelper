package Util;

import Constant.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtil {
    public static String openConnection(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        // 接收servlet返回值，是字节
        InputStream is = url.openStream();

        // 由于is是字节，所以我们要把它转换为String类型，否则遇到中文会出现乱码
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
