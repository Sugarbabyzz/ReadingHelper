package Login;

import Constant.Constant;
import Reader.MainPage;
import Util.AlertMaker;
import Util.UrlUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;

public class Register extends Application {

    Stage stage = new Stage();

    @FXML
    private TextField tfAccount;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private PasswordField pfRePassword;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnSignUp;
    @FXML
    private ProgressIndicator pi;
    @FXML
    private AnchorPane anchorPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Resource/fxml/register_layout.fxml"));
        primaryStage.setTitle("注册");
        primaryStage.setScene(new Scene(root, 350, 315));
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("/Resource/icon/mainicon.png"));
        primaryStage.show();

    }

    /**
     * 显示 注册 窗口
     *
     * @throws Exception
     */
    public void showWindow() throws Exception {
        start(stage);
    }

    /**
     * 完成注册 按钮
     *
     * @param actionEvent
     */
    @FXML
    public void signUp(ActionEvent actionEvent) {

        if (tfAccount.getText().isEmpty() || pfPassword.getText().isEmpty() || pfRePassword.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "账号或密码不能为空！");
            alert.setHeaderText(null);
            alert.showAndWait();
        } else if (!(pfPassword.getText()).equals(pfRePassword.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "两次输入的密码不一致！");
            alert.setHeaderText(null);
            alert.showAndWait();
        } else if (!tfAccount.getText().matches("[0-9A-Za-z]{1,15}") || pfPassword.getText().length() > 15) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "用户名仅能包含数字和字母，\n且用户名与密码长度均不能超过15位！");
            alert.setHeaderText(null);
            alert.showAndWait();
        } else {
            // 初始化进度条
            pi = new ProgressIndicator();
            //pi.setPrefSize(100, 100); //set size
            pi.setMinSize(60, 60);
            pi.setLayoutX(anchorPane.getWidth() / 2 - 30);    //set location
            pi.setLayoutY(anchorPane.getHeight() / 2 - 30);
            anchorPane.getChildren().add(pi);

            //login service 顺利执行完成即视为succeed
            mRegisterService.setOnSucceeded(event -> {
                switch (mRegisterService.getValue()) {
                    case Constant.FLAG_SUCCESS: // 注册成功
                        System.out.println("注册成功!");
                        pi.setDisable(true);
                        pi.setVisible(false);
                        //显示提示信息
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("用户注册");
                        alert.setHeaderText(null);
                        alert.setContentText("注册成功！\n请点击\"确认\"进入主页。");
                        //点击确认进入主页
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            //启动在线状态主页面
                            Platform.runLater(() -> {
                                try {
                                    new MainPage().showWindow(tfAccount.getText().trim());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                            //销毁当前窗口
                            Stage stage = (Stage) btnSignUp.getScene().getWindow();
                            stage.close();
                        }
                        break;
                    case Constant.FLAG_ACCOUNT_EXIST://账号已存在
                        System.out.println("注册失败,该账号已被注册!");
                        pi.setDisable(true);
                        pi.setVisible(false);
                        AlertMaker.showSimpleAlert("注册失败","该账号已被注册!");
                        break;
                    case Constant.FLAG_FAIL:
                        System.out.println("注册失败!");
                        pi.setDisable(true);
                        pi.setVisible(false);
                        AlertMaker.showErrorMessage("登录失败", "账号或密码错误");
                        break;
                    default:
                        System.out.println("UNKNOWN ERROR WHEN LOGGING IN!");
                        break;
                }
            });

            // 网络问题会导致该错误
            mRegisterService.setOnFailed(event -> {
                //dismiss current window
                pi.setDisable(true);
                pi.setVisible(false);
                AlertMaker.showErrorMessage("Error", "网络错误!请检查网络!");
            });

            // 真正做注册
            mRegisterService.restart();
//            Stage stage = (Stage) btnSignUp.getScene().getWindow();
//            stage.setOpacity(0.7);
        }
    }

    /**
     * 返回 按钮
     *
     * @param event
     * @throws Exception
     */
    public void back(ActionEvent event) throws Exception {
        //启动登录窗口
        new Login().showWindow();
        //销毁当前窗口
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }

    /**
     * 注册模块
     */
    private Service<String> mRegisterService = new Service<>() {
        @Override
        protected Task<String> createTask() {
            return new Task<>() {
                @Override
                protected String call() throws IOException {
                    String FLAG = null;

                    String account = tfAccount.getText();
                    String password = pfPassword.getText();
                    // assemble url string
                    String str = Constant.URL_Register + "account=" + account + "&" + "password=" + password;
                    //获取str对应Url的连接响应
                    String result = UrlUtil.openConnection(str);

                    // judge result
                    switch (result) {
                        case Constant.FLAG_SUCCESS:
                            FLAG = Constant.FLAG_SUCCESS;
                            break;
                        case Constant.FLAG_ACCOUNT_EXIST:
                            FLAG = Constant.FLAG_ACCOUNT_EXIST;
                            break;
                        case Constant.FLAG_FAIL:
                            FLAG = Constant.FLAG_FAIL;
                            break;
                    }
                    return FLAG;
                }
            };
        }
    };
}
