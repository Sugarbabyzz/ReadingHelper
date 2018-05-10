package Reader;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeCell;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class OfferTranslation extends Application {

    private String account; //账号
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

    public void showWindow(String account,TranslateResult controller) throws Exception {
        start(stage);
        this.account = account;
        this.controller = controller;
        System.out.println("offer:" + account);
    }

    public void submit(){
        controller.setSelfTrans(taSelfTrans.getText());

        //销毁当前窗口
        Stage stage = (Stage) btnSubmit.getScene().getWindow();
        stage.close();
    }

    public void back(){
        //销毁当前窗口
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }

}
