package Reader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainPage extends Application {

    Stage stage = new Stage();

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
}
