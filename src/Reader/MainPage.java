package Reader;

import Dictionary.Dictionary;
import Login.AboutThis;
import Login.ChangePassword;
import Login.Login;
import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import javax.swing.*;
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

    private double eventX;
    private double eventY;

    @FXML
    private TextArea textArea;
    @FXML
    private JFXCheckBox ckTransSentence;
    @FXML
    private JFXCheckBox ckTransWord;
    @FXML
    private ProgressIndicator pi;
    @FXML
    private BorderPane borderPane;

    private File CurFileName = null;//保存当前打开文件的文件目录

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


    }

    /**
     * 显示 主 页面 （离线版）
     *
     * @throws Exception
     */
    public void showWindow() throws Exception {
        this.isOnline = false;
        start(stage);
    }

    /**
     * 显示 主 页面 （在线版）
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
        setCurFileName(filePath);
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

            textArea.setOnMouseClicked(event -> {

                /*
                 * 选中划词，启用双击划词翻译
                 */
                if (ckTransWord.isSelected() && !ckTransSentence.isSelected() && event.getClickCount() == 2) {

                    // 初始化进度条
                    pi = new ProgressIndicator();
                    //pi.setPrefSize(100, 100); //set size
                    pi.setMinSize(60, 60);
                    pi.setLayoutX(borderPane.getWidth() / 2 - 30);    //set location
                    pi.setLayoutY(borderPane.getHeight() / 2 - 30);
                    borderPane.getChildren().add(pi);

                    srcWord = textArea.getSelectedText().trim();
                    eventX = event.getScreenX();
                    eventY = event.getScreenY();

                    //启动翻译结果页面
                    Platform.runLater(() -> {
                        try {
                            new TranslateResult().showWindow(account, isOnline, srcWord,
                                    event.getScreenX() + 10, event.getScreenY() + 10, MainPage.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
                /*
                 * 选中划句，启用划句翻译
                 * 调用百度翻译API
                 */
                if (ckTransSentence.isSelected() && !textArea.getSelectedText().isEmpty()) {
                    String srcSentence = textArea.getSelectedText().trim();
                    //启动直接翻译结果主页面
                    Platform.runLater(() -> {
                        try {
                            new DirectTranslateResult().showWindow(srcSentence, event.getScreenX(), event.getScreenY());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }


            });

        }
    }

    /**
     * 菜单栏 文件-保存
     *
     * @param event
     */
    public void saveFile(ActionEvent event) {

        File file = getCurFileName();

        if ((file == null)) {
            return;
        }

        //如果文件不存在，则创建
        if (!file.exists()) {
            //file.getParentFile();
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (!file.isFile()) {
            return;
        }

        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            //获取textArea文本，并用\n分隔符分割文本
            String[] s = this.textArea.getText().split("\n");
            for (int i = 0; i < s.length; i++) {
                bw.write(s[i] + "\r\n");
                bw.flush();
                //bw.newLine();
            }


        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        } finally {
            try {
                bw.close();
                fw.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

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

    public File getCurFileName() {
        return CurFileName;
    }

    public void setCurFileName(File curFileName) {
        CurFileName = curFileName;
    }

}
