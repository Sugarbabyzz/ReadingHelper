package Reader;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class TranslateResult extends Application {

    @FXML
    private Text source_word;

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
        primaryStage.setScene(new Scene(root, 500, 300));
        primaryStage.show();


    }

    public void showWindow(String srcWord, String result, double X, double Y)throws Exception{

        Stage stage = new Stage();
        stage.setX(X);
        stage.setY(Y);
        start(stage);
        source_word.setText(srcWord);
        
    }



}
