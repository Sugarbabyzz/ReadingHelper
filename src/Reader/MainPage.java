package Reader;

import Dictionary.Dictionary;
import javafx.application.Application;
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

public class MainPage extends Application {

    Stage stage = new Stage();

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
        primaryStage.setTitle("Reader");
        primaryStage.setScene(new Scene(root, 1000, 500));
        primaryStage.show();
    }

    public void showWindow() throws Exception {
        start(stage);
    }

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
                            new TranslateResult().showWindow(srcWord, EnglishPhoneticSymbol, EnglishAccentUrl, AmericanPhoneticSymbol, AmericanAccentUrl,
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

        String replaceResult;
        this.replaceWord = replaceWord;
        replaceResult = textArea.getText().replace(srcWord, replaceWord);
        srcWord = replaceWord;
        textArea.setText(replaceResult);
    }
}
