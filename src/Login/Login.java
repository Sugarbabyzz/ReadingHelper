package Login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Login extends Application {

    Stage stage = new Stage();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../Layout/login_layout.fxml"));

        Scene scene = new Scene(root, 400, 275);
        primaryStage.setTitle("用户登录");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public void showWindow() throws Exception {
        start(stage);
    }
}


