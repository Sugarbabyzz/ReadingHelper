package Constant;

public class Constant {

    // http请求返回值, 与servlet中对应
    public static final String FLAG_SUCCESS = "100";
    public static final String FLAG_FAIL = "200";
    public static final String FLAG_ACCOUNT_EXIST = "300";
    public static final String FLAG_NULL = "400";

    //连接至远程服务器
    public static String URL = "http://47.94.253.65:8080/ReaderServlet/";
    public static String URL_Login = URL + "LoginServlet?";
    public static String URL_Register = URL + "RegisterServlet?";
    public static String URL_SetSelfTranslation = URL + "SubmitSelfTranslationServlet?";
    public static String URL_SetLastChoice = URL + "SetLastChoiceServlet?";
    public static String URL_GetAll = URL + "GetAllServlet?";
    public static String URL_ChangePassword = URL + "ChangePswServlet?";
    public static String URL_AddNewWord = URL + "AddNewWordServlet?";
    public static String URL_GetNewWordList = URL + "GetNewWordListServlet?";
    public static String URL_RemoveWord = URL + "RemoveWordServlet?";
}
