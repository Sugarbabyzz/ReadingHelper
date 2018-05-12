package Reader;

import Dictionary.Dictionary;
import Login.ChangePassword;
import Login.Login;
import Util.WordStyleSet;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
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

    private TextField tfWordFont;
    private TextField tfWordSize;
    private MenuButton mbFontSelect;
    private MenuItem[] menuItemArray;
    private HBox hBox;
    int i;

    @FXML
    TextArea textArea;
    @FXML
    VBox vBox;
    @FXML
    ToggleButton toggle_button;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Layout/main_page.fxml"));
        primaryStage.setTitle("英语阅读器");
        primaryStage.setScene(new Scene(root, 1000, 500));
        primaryStage.getIcons().add(new Image("/Res/icon.jpg"));
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
            textArea.setEditable(false);
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

                        //启动翻译结果页面
                        Platform.runLater(() -> {
                            try {
                                new TranslateResult().showWindow(account, isOnline, srcWord, EnglishPhoneticSymbol, EnglishAccentUrl, AmericanPhoneticSymbol, AmericanAccentUrl,
                                        result, event.getScreenX(), event.getScreenY(), MainPage.this);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }

                }


            });


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
     *
     * @param event
     */
    public void signOut(ActionEvent event) throws Exception {

        //启动登录主页面
        Platform.runLater(() -> {
            try {
                new Login().showWindow();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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

        /*
         * 全局替换
         */

        String replaceResult = textArea.getText();
        Pattern pattern = Pattern.compile("\\b" + srcWord + "\\b");
        Matcher matcher = pattern.matcher(replaceResult);
        replaceResult = matcher.replaceAll(replaceWord);
        srcWord = replaceWord;
        textArea.setText(replaceResult);

        /*
         * 局部替换
         */

    }

    /**
     * 恢复原词
     *
     * @param sourceWord
     */
    public void recoverWord(String sourceWord) {


        String replaceResult = textArea.getText();
        Pattern pattern = Pattern.compile("\\b" + srcWord + "\\b");
        Matcher matcher = pattern.matcher(replaceResult);
        replaceResult = matcher.replaceAll(sourceWord);
        srcWord = sourceWord;
        textArea.setText(replaceResult);

    }

    /**
     * 修改密码
     *
     * @param event
     */
    public void changePsw(ActionEvent event) {

        //启动修改密码主页面
        Platform.runLater(() -> {
            try {
                new ChangePassword().showWindow(account);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 调用百度翻译
     *
     * @param event
     */
    public void translate(ActionEvent event) {

        String srcSentence = textArea.getSelectedText().trim();

        //启动直接翻译结果主页面
        Platform.runLater(() -> {
            try {
                new DirectTranslateResult().showWindow(srcSentence);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * 显示字体设置栏
     */
    public void showWordStyle(){
        //此布局的根节点
        hBox = new HBox();
        hBox.prefHeight(25);
        hBox.prefWidth(600);

        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontName = e.getAvailableFontFamilyNames();
        menuItemArray = new MenuItem[fontName.length];

        for (i = 0; i < fontName.length; i++) {
            menuItemArray[i] = new MenuItem(fontName[i]);
        }

        for (MenuItem mi:menuItemArray){
            mi.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String str = mi.getText();
                    WordStyleSet.StyleSet(textArea,str,Double.parseDouble(tfWordSize.getText()));
                    mbFontSelect.setText(str);
                    System.out.println("字体:" + textArea.getFont().getName());
                }
            });
        }

        mbFontSelect = new MenuButton();
        mbFontSelect.getItems().addAll(menuItemArray);
        mbFontSelect.setText(textArea.getFont().getName());
        mbFontSelect.prefWidth(100);
        mbFontSelect.prefHeight(25);

        //设置字体大小的按钮
        Button setWordSize = new Button();
        setWordSize.setText("设置字体大小");
        //字体大小输入
        tfWordSize = new TextField();
        tfWordSize.prefWidth(15);
        tfWordSize.prefHeight(25);
        tfWordSize.setPadding(new Insets(4,-110,4,7));
        tfWordSize.setText(textArea.getFont().getSize()+"");
        //字体大小监听
        setWordSize.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                WordStyleSet.StyleSet(textArea,mbFontSelect.getText(),Double.parseDouble(tfWordSize.getText()));
            }
        });

        // 将控件加入字体样式根节点、设置间距
        hBox.getChildren().addAll(setWordSize,tfWordSize,mbFontSelect);
        hBox.setMargin(setWordSize,new Insets(0,0,0,50));
        hBox.setMargin(mbFontSelect,new Insets(0,0,0,50));

        // 将该根节点加入布局
        vBox.getChildren().add(1,hBox);
    }

    /**
     * 对toggleButton是否按下作判断
     */
    public void ifSetFont() {
        if (toggle_button.isSelected() == true){
            showWordStyle();
            toggle_button.setText("隐藏字体设置");
        }else {
            vBox.getChildren().remove(hBox);
            toggle_button.setText("字体设置");
        }
    }
}
