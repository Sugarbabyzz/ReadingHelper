package Reader;

import Util.TransApi;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectTranslateResult extends Application {

    Stage stage = new Stage();

    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20180429000151734";
    private static final String SECURITY_KEY = "pAi_1F_ReUEJSeQvA_QP";

    @FXML
    private Text txtResult;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("Layout/direct_translate_result.fxml")
        );
        loader.setController(this);
        Parent root = loader.load();

        primaryStage.setTitle("翻译结果");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 300, 100));
        primaryStage.show();
    }

    /**
     * 显示 直接翻译结果 页面
     *
     * @throws Exception
     */
    public void showWindow(String sourceSentence) throws Exception {
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

        txtResult.setText(directResult);
        txtResult.setWrappingWidth(300);
    }

    //Unicode转中文
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
}
