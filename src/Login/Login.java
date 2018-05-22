package Login;

import Constant.Constant;
import Reader.MainPage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class Login extends Application {

    Stage stage = new Stage();

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


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Layout/login_layout.fxml"));
        Scene scene = new Scene(root, 350, 400);
        primaryStage.setTitle("登录");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("/Res/mainicon.png"));
        primaryStage.show();

    }

    /**
     * 显示 Login 窗口
     *
     * @throws Exception
     */
    public void showWindow() throws Exception {
        start(stage);
    }

    /**
     * 登录按钮
     *
     * @param actionEvent
     */
    @FXML
    public void signIn(ActionEvent actionEvent) {

        if (tfAccount.getText().isEmpty() || pfPassword.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "账号或密码不能为空！");
            alert.setHeaderText(null);
            alert.showAndWait();
        }  else if (!tfAccount.getText().matches("[0-9A-Za-z]*")){
            Alert alert = new Alert(Alert.AlertType.ERROR, "账号或密码错误！");
            alert.setHeaderText(null);
            alert.showAndWait();
        } else {
            /*
             *  线程处理登录操作
             *  坑！！！
             *  用runable run方法会报Not on FX application thread; currentThread = thread1的异常。
             *  导致这个异常的原因就是因为修改界面的工作必须要由JavaFX的Application Thread来完成，由其它线程来完成不是线程安全的。
             *
             *  解决：Platform.runLater
             *  JavaFX提供的一个工具方法，可以把修改界面的工作放到一个队列中，等到Application Thread空闲的时候，它就会自动执行队列中修改界面的工作了
             */

            new Thread(() -> {
                Platform.runLater(() -> {
                    try {
                        //调用登录模块
                        login();
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "登录失败！");
                        alert.setHeaderText(null);
                        alert.showAndWait();
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
    public void signUp(ActionEvent actionEvent) {

        //启动注册窗口
        Platform.runLater(() -> {
            try {
                new Register().showWindow();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //销毁当前窗口
        Stage stage = (Stage) btnSignUp.getScene().getWindow();
        stage.close();
    }

    /**
     * 游客登录按钮
     *
     * @param actionEvent
     */
    @FXML
    public void offlineUse(ActionEvent actionEvent){

        //启动离线状态主页面
        Platform.runLater(() -> {
            try {
                new MainPage().showWindow();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        //销毁当前窗口
        Stage stage = (Stage) btnOfflineUse.getScene().getWindow();
        stage.close();
    }



    /**
     * 登录模块
     */
    private void login() {

        try {
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
                Platform.runLater(() -> {
                    try {
                        new MainPage().showWindow(account);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                //销毁当前窗口
                Stage stage = (Stage) btnSignIn.getScene().getWindow();
                stage.close();

                System.out.println("登录成功！");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "账号或密码错误！");
                alert.setHeaderText(null);
                alert.showAndWait();

                System.out.println("账号密码错误！");
            }
        } catch (Exception e) {
            //网络不通的情况
            Alert alert = new Alert(Alert.AlertType.ERROR, "网络连接异常！");
            alert.setHeaderText(null);
            alert.showAndWait();

            e.printStackTrace();
        }
    }


}


