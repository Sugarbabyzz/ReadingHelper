package Reader;

import Dictionary.Dictionary;
import Login.ChangePassword;
import Login.AboutThis;
import Login.Login;
import Util.WordStyleSet;
import com.jfoenix.controls.*;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.awt.*;
import java.io.*;
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
    int i;

    @FXML
    private TextField tfWordFont;
    @FXML
    private TextField tfWordSize;
    @FXML
    private MenuButton mbFontSelect;
    @FXML
    private MenuItem[] menuItemArray;
    @FXML
    private HBox hBox;
    @FXML
    private TextArea textArea;
    @FXML
    private VBox vBox;
    @FXML
    private ToggleButton toggle_button;
    @FXML
    private JFXCheckBox ckTransSentence;
    @FXML
    private JFXCheckBox ckTransWord;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Resource/fxml/main_page.fxml"));
        Scene scene = new Scene(root, 1000, 500);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("Resource/css/jfoenix-main-demo.css").toExternalForm());
        scene.getStylesheets().add(getClass().getClassLoader().getResource("Resource/css/jfoenix-components.css").toExternalForm());
        primaryStage.setTitle("英语阅读器");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("/Resource/icon/mainicon.png"));
        primaryStage.show();

//        textArea.setStyle(".text-area");
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
     * @param account 传入账号信息
     * @throws Exception
     */
    public void showWindow(String account) throws Exception {
        this.isOnline = true;
        this.account = account;
        start(stage);
    }


    /**
     * 菜单栏 文件-打开
     *
     * @throws IOException
     */
    public void openFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        File filePath = fileChooser.showOpenDialog(new Stage());
        if (filePath != null) {
            if (filePath.toString().endsWith(".txt")) {
                /*
                 * 打开txt
                 */
                BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
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
            } else if (filePath.toString().endsWith(".doc")) {
                /*
                 * 打开doc格式
                 */

                InputStream is = new FileInputStream(filePath.toString());
                WordExtractor extractor = new WordExtractor(is);

                //System.out.println(extractor.getText());
                textArea.setText(extractor.getText());
            } else if (filePath.toString().endsWith(".docx")) {
                /*
                 * 打开docx格式
                 */
                XWPFDocument doc = new XWPFDocument(
                        new FileInputStream(filePath.toString()));
                //using XWPFWordExtractor Class
                XWPFWordExtractor we = new XWPFWordExtractor(doc);

                //System.out.println(we.getText());
                textArea.setText(we.getText());
            } else if (filePath.toString().endsWith(".pdf")) {
                /*
                 * 打开pdf格式
                 */
                PDDocument document = null;
                document = PDDocument.load(filePath);
                // 获取页码
                int pages = document.getNumberOfPages();
                // 读文本内容
                PDFTextStripper stripper = new PDFTextStripper();
                // 设置按顺序输出
                stripper.setSortByPosition(true);
                stripper.setStartPage(1);
                stripper.setEndPage(pages);
                String content = stripper.getText(document);

                //System.out.println(content);
                textArea.setText(content);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "仅支持 word、pdf、txt格式文档！");
                alert.setHeaderText(null);
                alert.showAndWait();
            }

            textArea.setWrapText(true);
            textArea.setEditable(false);
            textArea.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {
                    if (ckTransWord.isSelected() && event.getClickCount() == 2) {

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

        }
    }

    /**
     * 菜单栏 文件-退出
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
     * 菜单栏 个人-修改密码
     *
     * @param event
     */
    public void changePsw(ActionEvent event) {

        if (!isOnline) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "游客登录无法使用此功能");
            alert.setHeaderText(null);
            alert.showAndWait();
        } else {
            //启动修改密码主页面
            Platform.runLater(() -> {
                try {
                    new ChangePassword().showWindow(account);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 菜单栏 个人-生词本
     *
     * @param event
     */
    public void newWord(ActionEvent event) throws IOException {

        if (!isOnline) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "游客登录无法使用此功能");
            alert.setHeaderText(null);
            alert.showAndWait();
        } else {
            //显示生词本界面
            new NewWordList().showWindow(account);
        }
    }

    /**
     * 菜单栏 帮助-关于
     *
     * @param event
     */

    public void about(ActionEvent event) {

        //启动关于软件窗口
        Platform.runLater(() -> {
            try {
                new AboutThis().showWindow();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 字体设置 按钮
     * 对toggleButton是否按下作判断
     */
    public void ifSetFont() {
        if (toggle_button.isSelected()) {
            showWordStyle();
            toggle_button.setText("隐藏字体设置");
        } else {
            vBox.getChildren().remove(hBox);
            toggle_button.setText("字体设置");
        }
    }

    /**
     * 显示字体设置栏
     */
    public void showWordStyle() {
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

        for (MenuItem mi : menuItemArray) {
            mi.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String str = mi.getText();
                    WordStyleSet.StyleSet(textArea, str, Double.parseDouble(tfWordSize.getText()));
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
        tfWordSize.setPadding(new Insets(4, -110, 4, 7));
        tfWordSize.setText(textArea.getFont().getSize() + "");
        //字体大小监听
        setWordSize.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                WordStyleSet.StyleSet(textArea, mbFontSelect.getText(), Double.parseDouble(tfWordSize.getText()));
            }
        });

        // 将控件加入字体样式根节点、设置间距
        hBox.getChildren().addAll(setWordSize, tfWordSize, mbFontSelect);
        hBox.setMargin(setWordSize, new Insets(0, 0, 0, 50));
        hBox.setMargin(mbFontSelect, new Insets(0, 0, 0, 50));

        // 将该根节点加入布局
        vBox.getChildren().add(1, hBox);
    }


    /**
     * 划词翻译
     * 获取单词释义
     *
     * @param srcWord 待翻译的单词
     * @return 返回翻译结果
     */
    public static String search(String srcWord) {
        //调用httpRequest方法，获取html字符串
        String html = Dictionary.httpRequest("http://www.iciba.com/" + srcWord);
        //利用正则表达式，抓取单词翻译信息
        String result = Dictionary.GetResult(html);

        return result;
    }



    /**
     * 划词翻译
     * 用译文替换单词
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
         * 待完成
         */

    }

    /**
     * 划词翻译
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
     * 划句翻译
     * 调用百度翻译API
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
}
