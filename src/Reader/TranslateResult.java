package Reader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class TranslateResult extends Application {


    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Layout/translate_result.fxml"));
        primaryStage.setTitle("ReadingHelper");
        primaryStage.setScene(new Scene(root, 500, 300));
        primaryStage.show();

    }

    public void showWindow(double X, double Y)throws Exception{
        Stage stage = new Stage();
        stage.setX(X);
        stage.setY(Y);
        start(stage);
    }
}
