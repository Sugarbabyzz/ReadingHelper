package Controller;

import Dictionary.Dictionary;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainPageController {

    boolean flag = true;
    String srcWord;
    String result;
@FXML
    TextArea textArea;
    public void openFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        File filePath = fileChooser.showOpenDialog(new Stage());
        if (filePath != null){
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            textArea.setWrapText(true);
            textArea.setOnMouseClicked(event -> {
                // 双击获取选中的单词
                if (event.getClickCount() == 2){

                    srcWord = textArea.getSelectedText().trim();
                    //翻译
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            result = search(srcWord);
                        }
                    }).start();
                    System.out.print(result);
                    try {
                        Parent anotherRoot = FXMLLoader.load(getClass().getClassLoader().getResource("Layout/translate_result.fxml"));
                        Stage anotherStage = new Stage();
                        anotherStage.initStyle(StageStyle.TRANSPARENT);
                        anotherStage.setX(event.getScreenX());
                        anotherStage.setY(event.getScreenY());
                        anotherStage.setTitle("Another");
                        anotherStage.setScene(new Scene(anotherRoot,100,100));
                        anotherStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
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

    private void newTranslateResult() throws IOException {

    }

    private String search(String srcWord) {
        //调用httpRequest方法，获取html字符串
        String html = Dictionary.httpRequest("http://www.iciba.com/" + srcWord);
        //利用正则表达式，抓取单词翻译信息
        return Dictionary.GetResult(html);
    }
}
