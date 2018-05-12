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
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Layout/direct_translate_result.fxml"));
        Scene scene = new Scene(root, 300, 100);
        primaryStage.setTitle("直接翻译");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
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
        Matcher m = p.matcher(api.getTransResult(sourceSentence,"en", "zh" ));
        if (m.find()){
            result = m.group(3);
        }
        String directResult = decodeUnicode(result);

        System.out.println(result);
        System.out.println(api.getTransResult(sourceSentence, "en", "zh"));
        System.out.println(directResult);

        txtResult.setText(directResult);
        //taResult.setEditable(false);
    }

    //Unicode转中文
    public static String decodeUnicode(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }
}
