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

import java.io.IOException;

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
    @FXML
    private ProgressIndicator pi;
    @FXML
    private AnchorPane anchorPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Resource/fxml/login_layout.fxml"));
        Scene scene = new Scene(root, 350, 400);
        primaryStage.setTitle("登录");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("/Resource/icon/mainicon.png"));
        primaryStage.show();

    }

    /**
     * 显示 登录 窗口
     *
     * @throws Exception
     */
    public void showWindow() throws Exception {
        start(stage);
    }

    /**
     * 登录 按钮
     *
     * @param actionEvent
     */
    @FXML
    public void signIn(ActionEvent actionEvent) {


        if (tfAccount.getText().isEmpty() || pfPassword.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "账号或密码不能为空！");
            alert.setHeaderText(null);
            alert.showAndWait();
        } else if (!tfAccount.getText().matches("[0-9A-Za-z]*")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "账号或密码错误！");
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
            mLoginService.setOnSucceeded(event -> {
                switch (mLoginService.getValue()) {
                    case Constant.FLAG_SUCCESS:
                        System.out.println("登录成功!");
                        pi.setDisable(true);
                        pi.setVisible(false);
                        // 启动mainPage
                        Platform.runLater(() -> {
                            try {
                                new MainPage().showWindow(tfAccount.getText().trim());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        Stage stage = (Stage) btnSignUp.getScene().getWindow();
                        stage.close();
                        break;
                    case Constant.FLAG_FAIL:
                        System.out.println("登录失败!账号或密码错误!");
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
            mLoginService.setOnFailed(event -> {
                //dismiss current window
                pi.setDisable(true);
                pi.setVisible(false);
                AlertMaker.showErrorMessage("Error", "网络错误!请检查网络!");
            });

            // 真正做登录
            mLoginService.restart();
//            Stage stage = (Stage) btnSignUp.getScene().getWindow();
//            stage.setOpacity(0.7);
        }
    }

    /**
     * 注册 按钮
     *
     * @param actionEvent
     */
    @FXML
    public void signUp(ActionEvent actionEvent) {
        /*
         *  线程处理登录操作
         *  坑！！！
         *  用runable run方法会报Not on FX application thread; currentThread = thread1的异常。
         *  导致这个异常的原因就是因为修改界面的工作必须要由JavaFX的Application Thread来完成，由其它线程来完成不是线程安全的。
         *
         *  解决：Platform.runLater
         *  JavaFX提供的一个工具方法，可以把修改界面的工作放到一个队列中，等到Application Thread空闲的时候，它就会自动执行队列中修改界面的工作了
         */

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
     * 游客登录 按钮
     *
     * @param actionEvent
     */
    @FXML
    public void offlineUse(ActionEvent actionEvent) {

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
     * 登录 模块
     */
    private Service<String> mLoginService = new Service<>() {
        @Override
        protected Task<String> createTask() {
            return new Task<>() {
                @Override
                protected String call() throws IOException {
                    String FLAG = null;

                    String account = tfAccount.getText();
                    String password = pfPassword.getText();
                    // assemble url string
                    String str = Constant.URL_Login + "account=" + account +
                            "&password=" + java.net.URLEncoder.encode(password, "utf-8");
                    //获取str对应Url的连接响应
                    String result = UrlUtil.openConnection(str);
                    System.out.println("result:" + result);
                    // judge result
                    if (result.equals(Constant.FLAG_SUCCESS)) {
                        FLAG = Constant.FLAG_SUCCESS;
                    } else if (result.equals(Constant.FLAG_FAIL)) {
                        FLAG = Constant.FLAG_FAIL;
                    }
                    System.out.println("Flag:" + FLAG);
                    return FLAG;
                }
            };
        }
    };
}


