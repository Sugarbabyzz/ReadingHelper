package Reader;

import Util.DragUtil;
import Util.TransApi;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectTranslateResult extends Application {

    Stage stage = new Stage();

    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20180429000151734";
    private static final String SECURITY_KEY = "pAi_1F_ReUEJSeQvA_QP";

    @FXML
    private JFXTextArea taResult;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("Resource/fxml/direct_translate_result.fxml")
        );
        loader.setController(this);
        Parent root = loader.load();

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("翻译结果");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 300, 80));
        primaryStage.getIcons().add(new Image("/Resource/icon/mainicon.png"));
        primaryStage.show();

    }

    /**
     * 显示 直接翻译结果 页面
     *
     * @throws Exception
     */
    public void showWindow(String sourceSentence, double X, double Y) throws Exception {

        //获取屏幕大小并让窗口总出现在屏幕范围内
        Toolkit tool = Toolkit.getDefaultToolkit();
        Dimension screen = tool.getScreenSize();
        if (X > (screen.width-300)){
            X = X - 300;
        }
        if (Y > (screen.height - 80)){
            Y = Y - 80;
        }

        stage.setX(X);
        stage.setY(Y);
        start(stage);

        TransApi api = new TransApi(APP_ID, SECURITY_KEY);

        String result = " ";
        Pattern p = Pattern.compile("(.*)(\"dst\":\")(.*)(\"})(.*)");
        Matcher m = p.matcher(api.getTransResult(sourceSentence, "en", "zh"));
        if (m.find()) {
            result = m.group(3);
        }
        String directResult = decodeUnicode(result);

//        System.out.println(result);
//        System.out.println(api.getTransResult(sourceSentence, "en", "zh"));
//        System.out.println(directResult);

        taResult.setText(directResult);
        taResult.setEditable(false);

        taResult.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            moveout();
        });


    }


    /**
     * Unicode转中文 模块
     * @param asciicode
     * @return
     */
    private static String decodeUnicode(String asciicode) {

        String[] asciis = asciicode.split("\\\\u");
        String nativeValue = asciis[0];
        try {
            for (int i = 1; i < asciis.length; i++) {
                String code = asciis[i];
                nativeValue += (char) Integer.parseInt(code.substring(0, 4), 16);
                if (code.length() > 4) {
                    nativeValue += code.substring(4, code.length());
                }
            }
        } catch (NumberFormatException e) {
            return asciicode;
        }
        return nativeValue;
    }

    /**
     * 鼠标移出界面
     */
    private void moveout(){
        //销毁当前窗口
        Stage stage = (Stage) taResult.getScene().getWindow();
        stage.close();
    }
}
