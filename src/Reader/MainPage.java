package Reader;

import Dictionary.Dictionary;
import Login.Login;
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainPage extends Application {

    Stage stage = new Stage();

    private static boolean isOnline; //判断是否登录账号
    private static String account; //传过来的账号

    private boolean flag = true;
    private String srcWord;
    private String result;
    private String EnglishAccentUrl;
    private String AmericanAccentUrl;
    private String EnglishPhoneticSymbol;
    private String AmericanPhoneticSymbol;
    private String replaceWord;
    @FXML
    TextArea textArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Layout/main_page.fxml"));
        primaryStage.setTitle("英语阅读器");
        primaryStage.setScene(new Scene(root, 1000, 500));
        primaryStage.show();
    }

    /**
     * 显示 Main 页面 （离线版）
     *
     * @throws Exception
     */
    public void showWindow() throws Exception {
        this.isOnline = false;
        start(stage);
    }

    /**
     * 显示 Main 页面 （在线版）
     *
     * @param account
     * @throws Exception
     */
    public void showWindow(String account) throws Exception {
        this.isOnline = true;
        this.account = account;
        start(stage);
    }


    /**
     * 打开文件
     *
     * @throws IOException
     */
    public void openFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        File filePath = fileChooser.showOpenDialog(new Stage());
        if (filePath != null) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            textArea.setWrapText(true);
            textArea.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {
                    if (event.getClickCount() == 2) {

                        srcWord = textArea.getSelectedText().trim();
                        System.out.println(srcWord);
                        //翻译
                        result = search(srcWord);
                        System.out.println(result);
                        //获取英式美式的两个音标、两个url
                        EnglishAccentUrl = Dictionary.EnglishAccentUrl;
                        AmericanAccentUrl = Dictionary.AmericanAccentUrl;
                        EnglishPhoneticSymbol = Dictionary.EnglishPhoneticSymbol;
                        AmericanPhoneticSymbol = Dictionary.AmericanPhoneticSymbol;
                        try {
                            new TranslateResult().showWindow(account, isOnline, srcWord, EnglishPhoneticSymbol, EnglishAccentUrl, AmericanPhoneticSymbol, AmericanAccentUrl,
                                    result, event.getScreenX(), event.getScreenY(), MainPage.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

            textArea.setEditable(false);

            //一行一行读取至BufferReader并输出到textArea
            String str_line;
            while ((str_line = bufferedReader.readLine()) != null) {
                if (flag) {
                    textArea.setText(str_line);
                    flag = false;
                } else {
                    textArea.setText(textArea.getText() + "\n" + str_line);
                }
            }
        }
    }

    /**
     * 退出登录
     * @param event
     */
    public void signOut(ActionEvent event) throws Exception {

        //启动登录窗口
        new Login().showWindow();
        //销毁当前窗口
        Stage stage = (Stage) textArea.getScene().getWindow();
        stage.close();

    }


    /**
     * 访问网络获取翻译结果
     *
     * @param srcWord 待翻译的单词
     * @return 返回翻译结果
     */
    private String search(String srcWord) {
        //调用httpRequest方法，获取html字符串
        String html = Dictionary.httpRequest("http://www.iciba.com/" + srcWord);
        //利用正则表达式，抓取单词翻译信息
        String result = Dictionary.GetResult(html);

        return result;
    }

    /**
     * 将选中的单词替换为用户选中的释义
     *
     * @param replaceWord
     */
    public void replaceWord(String replaceWord) {

        /**
         * 存在bug，会匹配所有包含该字母的单词 (已修复）
         *
         * 解决：
         * 需用正则表达式进行查找
         */

        String replaceResult = textArea.getText();
        Pattern pattern = Pattern.compile("\\s" + srcWord + "\\s");
        Matcher matcher = pattern.matcher(replaceResult);
        replaceResult = matcher.replaceAll(" " + replaceWord + " ");
        srcWord = replaceWord;
        textArea.setText(replaceResult);

    }

    /**
     * 恢复原词
     *
     * @param sourceWord
     */
    public void recoverWord(String sourceWord) {


        String replaceResult = textArea.getText();
        Pattern pattern = Pattern.compile("\\s" + srcWord + "\\s");
        Matcher matcher = pattern.matcher(replaceResult);
        replaceResult = matcher.replaceAll(" " + sourceWord + " ");
        srcWord = sourceWord;
        textArea.setText(replaceResult);

    }


}
