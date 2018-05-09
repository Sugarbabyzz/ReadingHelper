package Reader;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

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

    public void showWindow(String srcWord, String ukPhonetic , String ukUrl, String usPhonetic, String usUrl,
                           String result, double X, double Y) throws Exception{

        Stage stage = new Stage();
        stage.setX(X);
        stage.setY(Y);
        start(stage);
        tSrcWord.setText(srcWord);
        ukPhoneticSymbol.setText(ukPhonetic);
        usPhoneticSymbol.setText(usPhonetic);
        taTransResult.setText(result);
        taTransResult.setEditable(false);
        
    }

    /**
     * 加入生词本按钮
     * @param event
     */
    public void addWord(ActionEvent event){

    }

    /**
     * 编辑按钮
     * @param event
     */
    public void edit(ActionEvent event){

    }

    /**
     * 替换按钮
     * @param event
     */
    public void replace(ActionEvent event){

    }


    /**
     * 退出按钮
     * @param event
     */
    @FXML
    public void quit(ActionEvent event){

        //销毁当前窗口
        Stage stage = (Stage) btnQuit.getScene().getWindow();
        stage.close();
    }

}
