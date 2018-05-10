package Reader;

import Constant.Constant;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeCell;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class OfferTranslation extends Application {

    private static String account; //账号
    private static String word;
    Stage stage = new Stage();

    TranslateResult controller;

    @FXML
    TextArea taSelfTrans;

    @FXML
    Button btnSubmit;

    @FXML
    Button btnBack;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("Layout/user_offer_translate.fxml")
        );
        loader.setController(this);
        Parent root = loader.load();

        primaryStage.setTitle("提供译文");
        primaryStage.setScene(new Scene(root, 500, 300));
        primaryStage.show();
    }

    public void showWindow(String account, String srcWord, TranslateResult controller) throws Exception {
        start(stage);
        this.account = account;
        this.word = srcWord;
        this.controller = controller;
        System.out.println("offer:" + account);
    }

    /**
     * 提交译文按钮
     */
    public void submit() {
        controller.setSelfTrans(taSelfTrans.getText()); //设置翻译页面的提供译文栏

        new Thread(() -> {
            Platform.runLater(() -> {
                try {
                    //调用提交译文模块
                    /**
                     * 中文url存在编码问题！！待解决！！
                     */
                    //submitSelfTranslation(taSelfTrans.getText());
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "提交译文失败！");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                }
            });
        }).start();

    }

    /**
     * 退回按钮
     */
    public void back() {

        //销毁当前窗口
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }

    /**
     * 提交译文模块
     *
     * @param selfTranslation
     */
    public void submitSelfTranslation(String selfTranslation) {
        try {
            // 获取账号、单词和提交的译文
            String account = this.account;
            String word = this.word;

            URL url = new URL(Constant.URL_SetSelfTranslation + "account=" + account + "&" + "word=" + word + "&" + "translation=" + selfTranslation);
            // 接收servlet返回值，是字节
            InputStream is = url.openStream();

            // 由于is是字节，所以我们要把它转换为String类型，否则遇到中文会出现乱码
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            if (sb.toString().equals(Constant.FLAG_SUCCESS)) {

                //销毁当前窗口
                Stage stage = (Stage) btnSubmit.getScene().getWindow();
                stage.close();

                System.out.println("提交译文成功！");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "提交译文失败！");
                alert.setHeaderText(null);
                alert.showAndWait();

                System.out.println("提交译文失败！");
            }
        } catch (Exception e) {
            //网络不通的情况
            Alert alert = new Alert(Alert.AlertType.ERROR, "网络连接异常，提交译文失败！");
            alert.setHeaderText(null);
            alert.showAndWait();

            e.printStackTrace();
        }
    }

}
