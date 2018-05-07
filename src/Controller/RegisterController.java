package Controller;

import Constant.Constant;
import Login.Login;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class RegisterController {

    @FXML
    private TextField tfAccount;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private PasswordField pfRePassword;
    @FXML
    private Button btnBack;


    public void signUp(ActionEvent actionEvent) {

        if (tfAccount.getText().isEmpty() || pfPassword.getText().isEmpty() || pfRePassword.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "账号或密码不能为空！").show();
        } else if (!(pfPassword.getText()).equals(pfRePassword.getText())) {
            new Alert(Alert.AlertType.ERROR, "两次输入的密码不一致！").show();
        } else {
            try {
                register();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public void back(ActionEvent event) throws Exception {
        //启动注册舞台
        Login nextWindow = new Login();
        nextWindow.showWindow();
        //销毁当前舞台
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }

    private void register() throws IOException {


        String account = tfAccount.getText();
        String password = pfPassword.getText();

        URL url = new URL(Constant.URL_Register + "account=" + account + "&" + "password=" + password);
        // 接收servlet返回值，是字节
        InputStream is = url.openStream();

        // 由于is是字节，所以我们要把它转换为String类型，否则遇到中文会出现乱码
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        //判断结果
        if (sb.toString().equals(Constant.FLAG_SUCCESS)) {
            new Alert(Alert.AlertType.INFORMATION, "注册成功！点击确认进入主页面。").show();
//            if (op == JOptionPane.YES_OPTION) {
//                jf.dispose();
//                new MainPage();
//            }
        } else if (sb.toString().equals(Constant.FLAG_YES)) {
            new Alert(Alert.AlertType.ERROR, "该账号已存在！").show();
        } else {
            new Alert(Alert.AlertType.ERROR, "注册失败！").show();
        }
    }
}
