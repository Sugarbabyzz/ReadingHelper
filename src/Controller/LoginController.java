package Controller;

import Constant.Constant;
import Login.Register;
import javafx.application.Platform;
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

public class LoginController {

    @FXML
    private TextField tfAccount;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private Button btnSignIn;
    @FXML
    private Button btnSignUp;
    @FXML
    private Button btnOfflineUse;

    /**
     * 登录按钮
     *
     * @param actionEvent
     */
    @FXML
    public void signIn(ActionEvent actionEvent) throws Exception {

        if (tfAccount.getText().isEmpty() || pfPassword.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "账号或密码不能为空!").show();
        } else {
            //线程处理登录操作
            /*
             *  线程处理登录操作
             *  坑！！！
             *  用runable run方法会报Not on FX application thread; currentThread = thread1的异常。
             *  导致这个异常的原因就是因为修改界面的工作必须要由JavaFX的Application Thread来完成，由其它线程来完成不是线程安全的。
             *
             *  解决：Platform.runLater
             *  JavaFX提供的一个工具方法，可以把修改界面的工作放到一个队列中，等到Application Thread空闲的时候，它就会自动执行队列中修改界面的工作了
             */
            new Thread(()->{
                Platform.runLater(() -> {
                    try {
                        login();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                });
            }).start();
        }
    }


    /**
     * 注册按钮
     *
     * @param actionEvent
     */
    @FXML
    public void signUp(ActionEvent actionEvent) throws Exception {

        //启动注册窗口
        Register nextWindow = new Register();
        nextWindow.showWindow();
        //销毁当前窗口
        Stage stage = (Stage) btnSignIn.getScene().getWindow();
        stage.close();

    }

    /**
     * 离线使用按钮
     *
     * @param actionEvent
     */
    @FXML
    public void offlineUse(ActionEvent actionEvent) {

        //启动离线状态主页面
        //~~~~~

        //销毁当前窗口
        Stage stage = (Stage) btnOfflineUse.getScene().getWindow();
        stage.close();
    }


    private void login() throws IOException {

        // 获取账号和密码
        String account = tfAccount.getText();
        String password = pfPassword.getText();

        URL url = new URL(Constant.URL_Login + "account=" + account + "&" + "password=" + password);
        // 接收servlet返回值，是字节
        InputStream is = url.openStream();

        // 由于is是字节，所以我们要把它转换为String类型，否则遇到中文会出现乱码
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        if (sb.toString().equals(Constant.FLAG_SUCCESS)) {

            //启动在线状态主页面
            //~~~~~

            //销毁当前窗口
            Stage stage = (Stage) btnSignIn.getScene().getWindow();
            stage.close();

            System.out.println("登录成功！");
        } else {
            new Alert(Alert.AlertType.ERROR, "账号或密码错误！").show();
        }
    }
}
