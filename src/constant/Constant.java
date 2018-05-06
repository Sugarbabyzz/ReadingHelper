package constant;

public class Constant {

    // http请求返回值, 与servlet中对应
    public static final String FLAG_FAILURE = "300";
    public static final String FLAG_SUCCESS = "200";
    public static final String FLAG_YES = "100";
    public static final String FLAG_NO = "400";

    //连接至远程服务器
    public static String URL = "http://47.94.253.65:8080/ReaderServlet/";
    public static String URL_Login = URL + "LoginServlet?";
    public static String URL_Register = URL + "RegisterServlet?";
}
