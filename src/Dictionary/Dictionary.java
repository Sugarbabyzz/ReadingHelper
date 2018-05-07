package Dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dictionary {

/*    public static void Reader(String[] args){

        System.out.println("请输入要查询的单词：");
        String word = new Scanner(System.in).next();
        //调用httpRequest方法，获取html字符串
        String html = httpRequest("http://www.iciba.com/" + word);
        //利用正则表达式，抓取单词翻译信息
        String result = GetResult(html);
        //打印
        System.out.println(result);

    }*/

    /**
     * 发起http get请求获取网页源代码
     *
     * @param requestUrl String    请求地址
     * @return String    该地址返回的html字符串
     */
    public static String httpRequest(String requestUrl) {
        StringBuffer buffer = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        InputStream inputStream = null;
        HttpURLConnection httpUrlConn = null;

        try {
            // 建立get请求
            URL url = new URL(requestUrl);
            httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");

            // 获取输入流
            inputStream = httpUrlConn.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);

            // 从输入流读取结果
            buffer = new StringBuffer();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpUrlConn != null) {
                httpUrlConn.disconnect();
            }
        }
        return buffer.toString();
    }

    /**
     * 过滤掉html字符串中无用的信息
     *
     * @param html String    html字符串
     * @return String    有用的数据
     */
    public static String GetResult(String html) {

        StringBuffer buffer = new StringBuffer();
        String str1 = "";
        String str2 = "";
        String str3 = "";
        String str4 = "";
        buffer.append("翻译结果: \t\n");

        // 取出有用的范围
        Pattern p = Pattern.compile("(.*)(<div class=\"in-base\">)(.*?)(进入生词本)(.*)");
        Matcher m = p.matcher(html);
        if (m.matches()) {
            str1 = m.group(3);
            //System.out.println(str1);

            /**
             * 匹配发音，注：发音被含在<div class="base-speak"><span> 和</i></span></div> 中
             */
            p = Pattern.compile("(.*)(<div class=\"base-speak\">)(.*?)(<div class=\"base-word\" >)(.*)");
            m = p.matcher(str1);
            if (m.matches()) {
                str2 = m.group(3);

                //英式
                p = Pattern.compile("(.*)((<span>英)(.*?)(<span>美))(.*)");
                m = p.matcher(str2);
                if (m.matches()) {
                    str3 = m.group(2);

                    //英式音标
                    p = Pattern.compile("(.*)(<span>英)(.*?)(</span>)(.*)");
                    m = p.matcher(str3);
                    if (m.matches()) {
                        str4 = m.group(3);
                        buffer.append("英");
                        buffer.append(str4);
                    }

                    //英式发音
                    p = Pattern.compile("(.*)((http://)(.*?))(')(.*)");
                    m = p.matcher(str3);
                    if (m.matches()) {
                        str4 = m.group(2);
                        buffer.append(" " + str4);
                    }
                }

                //美式
                p = Pattern.compile("(.*)((<span>美)(.*?)(</div>))(.*)");
                m = p.matcher(str2);
                if (m.matches()) {
                    str3 = m.group(2);

                    //美式音标
                    p = Pattern.compile("(.*)(<span>美)(.*?)(</span>)(.*)");
                    m = p.matcher(str3);
                    if (m.matches()) {
                        str4 = m.group(3);
                        buffer.append("  美");
                        buffer.append(str4);
                    }

                    //美式发音
                    p = Pattern.compile("(.*)((http://)(.*?))(')(.*)");
                    m = p.matcher(str3);
                    if (m.matches()) {
                        str4 = m.group(2);
                        buffer.append(" " + str4);
                        buffer.append(" \n");
                    }
                }
            }

            /**
             * 匹配单词基本释义，注：基本释义被包含在<ul class="base-list switch_part" class=""> 和 </ul> 中
             */
            p = Pattern.compile("(.*)(<ul class=\"base-list switch_part\" class=\"\">)(.*?)(</ul>)(.*)");
            m = p.matcher(str1);
            if (m.matches()) {
                str2 = m.group(3);

                //vt
                p = Pattern.compile("(.*)(<span class=\"prop\">vt.</span>)(.*?)(</p>)(.*)");
                m = p.matcher(str2);
                if (m.matches()) {
                    str3 = m.group(3);
                    buffer.append("vt. ");

                    //细节
                    p = Pattern.compile("(.*?)(<span>)(.*?)(</span>)(.*?)");
                    m = p.matcher(str3);
                    while (m.find()) {
                        buffer.append(m.group(3));
                    }

                    buffer.append(" \n");
                }

                //vi
                p = Pattern.compile("(.*)(<span class=\"prop\">vi.</span>)(.*?)(</p>)(.*)");
                m = p.matcher(str2);
                if (m.matches()) {
                    str3 = m.group(3);
                    buffer.append("vi. ");

                    //细节
                    p = Pattern.compile("(.*?)(<span>)(.*?)(</span>)(.*?)");
                    m = p.matcher(str3);
                    while (m.find()) {
                        buffer.append(m.group(3));
                    }

                    buffer.append(" \n");
                }

                //n
                p = Pattern.compile("(.*)(<span class=\"prop\">n.</span>)(.*?)(</p>)(.*)");
                m = p.matcher(str2);
                if (m.matches()) {
                    str3 = m.group(3);
                    buffer.append("n. ");

                    //细节
                    p = Pattern.compile("(.*?)(<span>)(.*?)(</span>)(.*?)");
                    m = p.matcher(str3);
                    while (m.find()) {
                        buffer.append(m.group(3));
                    }

                    buffer.append(" \n");
                }

                //adj
                p = Pattern.compile("(.*)(<span class=\"prop\">adj.</span>)(.*?)(</p>)(.*)");
                m = p.matcher(str2);
                if (m.matches()) {
                    str3 = m.group(3);
                    buffer.append("adj. ");

                    //细节
                    p = Pattern.compile("(.*?)(<span>)(.*?)(</span>)(.*?)");
                    m = p.matcher(str3);
                    while (m.find()) {
                        buffer.append(m.group(3));
                    }

                    buffer.append(" \n");
                }

                //adv
                p = Pattern.compile("(.*)(<span class=\"prop\">adv.</span>)(.*?)(</p>)(.*)");
                m = p.matcher(str2);
                if (m.matches()) {
                    str3 = m.group(3);
                    buffer.append("adv. ");

                    //细节
                    p = Pattern.compile("(.*?)(<span>)(.*?)(</span>)(.*?)");
                    m = p.matcher(str3);
                    while (m.find()) {
                        buffer.append(m.group(3));
                    }

                    buffer.append(" \n");
                }

                //art
                p = Pattern.compile("(.*)(<span class=\"prop\">art.</span>)(.*?)(</p>)(.*)");
                m = p.matcher(str2);
                if (m.matches()) {
                    str3 = m.group(3);
                    buffer.append("art. ");

                    //细节
                    p = Pattern.compile("(.*?)(<span>)(.*?)(</span>)(.*?)");
                    m = p.matcher(str3);
                    while (m.find()) {
                        buffer.append(m.group(3));
                    }

                    buffer.append(" \n");
                }

                //prep
                p = Pattern.compile("(.*)(<span class=\"prop\">prep.</span>)(.*?)(</p>)(.*)");
                m = p.matcher(str2);
                if (m.matches()) {
                    str3 = m.group(3);
                    buffer.append("prep. ");

                    //细节
                    p = Pattern.compile("(.*?)(<span>)(.*?)(</span>)(.*?)");
                    m = p.matcher(str3);
                    while (m.find()) {
                        buffer.append(m.group(3));
                    }

                    buffer.append(" \n");
                }

                //释义
                p = Pattern.compile("(.*)(<span class=\"prop\">释义</span>)(.*?)(</p>)(.*)");
                m = p.matcher(str2);
                if (m.matches()) {
                    str3 = m.group(3);
                    buffer.append("释义. ");

                    //细节
                    p = Pattern.compile("(.*?)(<span>)(.*?)(</span>)(.*?)");
                    m = p.matcher(str3);
                    while (m.find()) {
                        buffer.append(m.group(3));
                    }

                    buffer.append(" \n");
                }
            }
        }

        return buffer.toString();
    }
}

