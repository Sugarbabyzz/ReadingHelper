package Login;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class AboutThis extends Application {

    Stage stage  = new Stage();

    @FXML
    private Label labAbout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("Layout/about_layout.fxml")
        );
        loader.setController(this);
        Parent root = loader.load();

        primaryStage.setTitle("关于");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 300, 150));
        primaryStage.getIcons().add(new Image("/Res/mainicon.png"));
        primaryStage.show();

    }

    /**
     * 显示 帮助-关于 窗口
     *
     * @throws Exception
     */
    public void showWindow() throws Exception {
        start(stage);
        labAbout.setText("支持在位（in-place）翻译的英文文档阅读器 \n"
                        + "组员：\t唐载钏 41524162\n"
                        + "\t\t顾松 41524160\n"
                        + "\t\t赵冰玉 41524156\n"
                        + "\t\t刘卫卫 41524140\n");
    }
}
