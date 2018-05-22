package Login;

import Constant.Constant;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;

public class ChangePassword extends Application {

    Stage stage = new Stage();

    @FXML
    private Button btnChangePsw;
    @FXML
    private Button btnBack;
    @FXML
    private PasswordField pfNewPassword;
    @FXML
    private PasswordField pfReNewPassword;

    private static String account;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Resource/fxml/changePassword_layout.fxml"));
        Scene scene = new Scene(root, 300, 300);
        primaryStage.setTitle("修改密码");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("/Resource/icon/mainicon.png"));
        primaryStage.show();
    }

    /**
     * 显示 ChangePassword 窗口
     *
     * @throws Exception
     */
    public void showWindow(String account) throws Exception {
        start(stage);
        this.account = account;
    }

    /**
     * 确认修改密码 按钮
     *
     * @param event
     */
    public void changePsw(ActionEvent event) {
        if (pfNewPassword.getText().isEmpty() || pfReNewPassword.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "填写的新密码不能为空！");
            alert.setHeaderText(null);
            alert.showAndWait();
        } else if (!(pfNewPassword.getText()).equals(pfReNewPassword.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "两次输入的密码不一致！");
            alert.setHeaderText(null);
            alert.showAndWait();
        } else {
            /*
             *  线程处理注册操作
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
                        //调用修改密码模块
                        change();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }).start();
        }
    }

    /**
     * 取消按钮
     *
     * @param event
     * @throws Exception
     */
    public void back(ActionEvent event) throws Exception {
        //销毁当前窗口
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }

    /**
     * 修改密码模块
     */
    private void change() {
        try {
            String password = pfNewPassword.getText();

            URL url = new URL(Constant.URL_ChangePassword + "account=" + account + "&" + "password=" + password);
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

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("修改密码");
                alert.setHeaderText(null);
                alert.setContentText("修改密码成功！");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {

                    //销毁当前窗口
                    Stage stage = (Stage) btnBack.getScene().getWindow();
                    stage.close();

                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "修改密码失败！");
                alert.setHeaderText(null);
                alert.showAndWait();
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
