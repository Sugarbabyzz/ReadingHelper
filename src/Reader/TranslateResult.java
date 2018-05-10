package Reader;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class TranslateResult extends Application {


    @FXML
    private Text tSrcWord;
    @FXML
    private TextArea taTransResult;
    @FXML
    private Button btnQuit;
    @FXML
    private Text ukPhoneticSymbol; //英式音标
    @FXML
    private Text usPhoneticSymbol; //美式音标
    @FXML
    private ImageView ukVoice; //英式发音图标
    @FXML
    private ImageView usVoice; //美式发音图标
    @FXML
    private TextArea taSelfTrans; //自己对该词的翻译
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnAddWord;
    MainPage controller;

    private static String account; //账号
    private String replaceWord; //替换的释义
    private String ukUrl; //英式发音链接
    private String usUrl; //美式发音链接

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {


        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("Layout/translate_result.fxml")
        );
        loader.setController(this);
        Parent root = loader.load();

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("ReadingHelper");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

    }

    public void showWindow(String account, boolean isOnline, String srcWord, String ukPhonetic, String ukUrl, String usPhonetic, String usUrl,
                           String result, double X, double Y, MainPage controller) throws Exception {
        this.account = account;

        Stage stage = new Stage();
        stage.setX(X);
        stage.setY(Y);
        start(stage);

        this.controller = controller;

        tSrcWord.setText(srcWord);
        ukPhoneticSymbol.setText(ukPhonetic);
        usPhoneticSymbol.setText(usPhonetic);
        this.ukUrl = ukUrl;
        this.usUrl = usUrl;
        taTransResult.setText(result);
        taTransResult.setEditable(false);
        taTransResult.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if (event.getClickCount() == 2) {

                    replaceWord = taTransResult.getSelectedText().trim();
                    //System.out.println(replaceWord);

                    controller.replaceWord(replaceWord);
                }
            }
        });
        btnEdit.setDisable(!isOnline);
        btnAddWord.setDisable(!isOnline);
    }

    /**
     * 加入生词本按钮
     *
     * @param event
     */
    public void addWord(ActionEvent event) {

    }

    /**
     * 编辑按钮
     *
     * @param event
     */
    public void edit(ActionEvent event) throws Exception {
        new OfferTranslation().showWindow(account, TranslateResult.this);
    }

    /**
     * 恢复原文按钮
     *
     * @param event
     */
    public void recover(ActionEvent event) {

        controller.recoverWord(tSrcWord.getText());
    }


    /**
     * 退出按钮
     *
     * @param event
     */
    @FXML
    public void quit(ActionEvent event) {

        //销毁当前窗口
        Stage stage = (Stage) btnQuit.getScene().getWindow();
        stage.close();
    }

    /**
     * 英式发音
     *
     * @throws IOException
     * @throws JavaLayerException
     */
    public void pronounceUk() throws IOException, JavaLayerException {
        URL url = new URL(ukUrl);
        BufferedInputStream buffer = new BufferedInputStream(url.openStream());
        Player player = new Player(buffer);
        player.play();
    }

    /**
     * 美式发音
     *
     * @throws IOException
     * @throws JavaLayerException
     */
    public void pronounceUs() throws IOException, JavaLayerException {
        URL url = new URL(usUrl);
        BufferedInputStream buffer = new BufferedInputStream(url.openStream());
        Player player = new Player(buffer);
        player.play();
    }

    /**
     * 设置用户自己的译文
     *
     * @param selfTrans
     */
    public void setSelfTrans(String selfTrans) {
        taSelfTrans.setText(selfTrans);
    }
}
