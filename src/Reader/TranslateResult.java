package Reader;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class TranslateResult extends Application {

    @FXML
    private Text tSrcWord;
    @FXML
    private TextArea taTransResult;

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

    public void showWindow(String srcWord, String result, double X, double Y) throws Exception{

        Stage stage = new Stage();
        stage.setX(X);
        stage.setY(Y);
        start(stage);
        tSrcWord.setText(srcWord);
        taTransResult.setText(result);
        taTransResult.setEditable(false);
        
    }



}
