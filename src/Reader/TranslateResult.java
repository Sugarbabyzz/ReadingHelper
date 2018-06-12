package Reader;

import Constant.Constant;
import Dictionary.Dictionary;
import Util.AlertMaker;
import Util.UrlUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import de.jensd.fx.fontawesome.Icon;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslateResult extends Application {


    @FXML
    private Text tSrcWord;
    @FXML
    private JFXTextArea taTransResult; //翻译结果
    @FXML
    private JFXTextArea taOtherTransResult; // 别人提供的译文
    @FXML
    private JFXTextArea taSelfTrans; //自己提供的译文
    @FXML
    private JFXTextArea taLastChoice; //自己LastChoice
    @FXML
    private Text ukPhoneticSymbol; //英式音标
    @FXML
    private Text usPhoneticSymbol; //美式音标
    @FXML
    private ImageView ukVoice; //英式发音图标
    @FXML
    private ImageView usVoice; //美式发音图标
    @FXML
    private JFXButton btnEdit;
    @FXML
    private JFXButton btnAddWord;
    @FXML
    private Icon iconAddWord;
    @FXML
    private Icon iconUnAddWord;
    @FXML
    private ProgressIndicator pi;
    @FXML
    private AnchorPane anchorPaneOnline;
    @FXML
    private AnchorPane anchorPaneOffline;

    private MainPage controller;

    private static Stage stage = new Stage();

    private static String account; //账号
    private static Boolean isOnline;
    private static String word; //当前选中的单词
    private String replaceWord; //替换的释义

    private String transResult;
    private StringBuffer stringBuffer;
    private String lastChoice = " ";
    private String selfTranslation = " ";
    private String otherTranslation = " ";
    private String queryNewWord = "";


    private String ukUrl; //英式发音链接
    private String usUrl; //美式发音链接

    private static HashMap<String, String> transToWord = new HashMap<String, String>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        if (isOnline) {

            if (isChinese(word)) {
                /*
                 * 登录状态，并且含有中文 - 加载 translate_result_chinese.fxml 布局
                 */
                FXMLLoader loader = new FXMLLoader(
                        getClass().getClassLoader().getResource("Resource/fxml/translate_result_chinese.fxml")
                );
                loader.setController(this);
                Parent root = loader.load();
                Scene scene = new Scene(root, 70, 25);
                primaryStage.setTitle("翻译结果");
                primaryStage.setScene(scene);
                primaryStage.setResizable(false);
                primaryStage.getIcons().add(new Image("/Resource/icon/mainicon.png"));
                primaryStage.show();
                System.out.println("onlinE");

            } else {
                /*
                 * 登录状态，并且不含有中文 - 加载 translate_result_online.fxml 布局
                 */
                FXMLLoader loader = new FXMLLoader(
                        getClass().getClassLoader().getResource("Resource/fxml/translate_result_online.fxml")
                );
                loader.setController(this);
                Parent root = loader.load();
                Scene scene = new Scene(root, 300, 480);
                primaryStage.setTitle("翻译结果");
                primaryStage.setScene(scene);
                primaryStage.setResizable(false);
                primaryStage.getIcons().add(new Image("/Resource/icon/mainicon.png"));
                primaryStage.show();
                System.out.println("onlinE");

                // create online progress indicator
                pi = new ProgressIndicator(); // create progress indicator
                //pi.setMinSize(60,60);
                pi.setPrefSize(60, 60); //set size
                pi.setLayoutX(anchorPaneOnline.getWidth() / 2 - 30);    //set location
                pi.setLayoutY(anchorPaneOnline.getHeight() / 2 - 30);
                anchorPaneOnline.getChildren().add(pi);

                mInitServiceOnline.restart();

                mInitServiceOnline.setOnSucceeded(event -> {
                    try {
                        setUpUIOnline();//执行顺利,初始化UI
                        pi.setDisable(true); // disable pi
                        pi.setVisible(false);


                        /*
                         * 翻译结果 TextArea
                         */
                        taTransResult.setOnMouseClicked(event1 -> {
                            if (event1.getClickCount() == 2) {


                                replaceWord = taTransResult.getSelectedText().trim();
                                //System.out.println(replaceWord);
                                if (!replaceWord.isEmpty()) {
                                    /**
                                     * 替换
                                     */
                                    controller.replaceWord(replaceWord);

                                    /**
                                     * 存入Hashmap，为实现恢复原词功能
                                     */
                                    transToWord.put(replaceWord, word);
                                    System.out.println(transToWord);

                                    /**
                                     * 提交最后一次选择的译文
                                     */
                                    new Thread(() -> {
                                        Platform.runLater(() -> {
                                            try {
                                                //调用提交最后一次选择的译文模块
                                                submitLastChoice(taTransResult.getSelectedText().trim());
                                            } catch (Exception e) {
                                                Alert alert = new Alert(Alert.AlertType.ERROR, "提交译文失败！");
                                                alert.setHeaderText(null);
                                                alert.showAndWait();
                                                e.printStackTrace();
                                                System.out.println("提交最后一次选择的译文失败！");
                                            }
                                        });
                                    }).start();
                                }
                            }
                        });

                        /*
                         * 生词本图标（未加入）
                         */
                        iconUnAddWord.addEventHandler(MouseEvent.MOUSE_CLICKED,
                                event1 -> new Thread(() -> Platform.runLater(() -> {
                                    try {
                                        //调用加入生词本模块
                                        addNewWord();
                                        iconUnAddWord.setVisible(false);
                                        iconAddWord.setVisible(true);
                                    } catch (Exception e) {
                                        Alert alert = new Alert(Alert.AlertType.ERROR, "加入生词本失败，请检查网络！");
                                        alert.setHeaderText(null);
                                        alert.showAndWait();
                                    }
                                })).start());

                        /*
                         * 生词本图标（已加入）
                         */
                        iconAddWord.addEventHandler(MouseEvent.MOUSE_CLICKED,
                                event1 -> new Thread(() -> {
                                    Platform.runLater(() -> {
                                        try {
                                            //调用移出生词本模块
                                            removeNewWOrd();
                                            iconAddWord.setVisible(false);
                                            iconUnAddWord.setVisible(true);
                                        } catch (Exception e) {
                                            Alert alert = new Alert(Alert.AlertType.ERROR, "移出生词本失败，请检查网络！");
                                            alert.setHeaderText(null);
                                            alert.showAndWait();
                                        }
                                    });
                                }).start());

                        /*
                         * 其他用户提交的翻译 TextArea
                         */
                        taOtherTransResult.setOnMouseClicked(event1 -> {
                            if (event1.getClickCount() == 2) {


                                replaceWord = taOtherTransResult.getSelectedText().trim();
                                //System.out.println(replaceWord);

                                if (!replaceWord.isEmpty()) {
                                    /**
                                     * 替换
                                     */
                                    controller.replaceWord(replaceWord);

                                    /**
                                     * 存入Hashmap，为实现恢复原词功能
                                     */
                                    transToWord.put(replaceWord, word);
                                    System.out.println(transToWord);

                                    /**
                                     * 提交最后一次选择的译文
                                     */
                                    new Thread(() -> {
                                        Platform.runLater(() -> {
                                            try {
                                                //调用提交最后一次选择的译文模块
                                                submitLastChoice(taOtherTransResult.getSelectedText().trim());
                                            } catch (Exception e) {
                                                Alert alert = new Alert(Alert.AlertType.ERROR, "提交译文失败！");
                                                alert.setHeaderText(null);
                                                alert.showAndWait();
                                                e.printStackTrace();
                                                System.out.println("提交最后一次选择的译文失败！");
                                            }
                                        });
                                    }).start();
                                }
                            }
                        });

                        /*
                         * 自己提交的翻译 TextArea
                         */
                        taSelfTrans.setOnMouseClicked(event1 -> {
                            if (event1.getClickCount() == 2) {


                                replaceWord = taSelfTrans.getSelectedText().trim();
                                //System.out.println(replaceWord);

                                if (!replaceWord.isEmpty()) {
                                    /**
                                     * 替换
                                     */
                                    controller.replaceWord(replaceWord);

                                    /**
                                     * 存入Hashmap，为实现恢复原词功能
                                     */
                                    transToWord.put(replaceWord, word);
                                    System.out.println(transToWord);

                                    /**
                                     * 提交最后一次选择的译文
                                     */
                                    new Thread(() -> {
                                        Platform.runLater(() -> {
                                            try {
                                                //调用提交最后一次选择的译文模块
                                                submitLastChoice(taSelfTrans.getSelectedText().trim());
                                            } catch (Exception e) {
                                                Alert alert = new Alert(Alert.AlertType.ERROR, "提交译文失败！");
                                                alert.setHeaderText(null);
                                                alert.showAndWait();
                                                e.printStackTrace();
                                                System.out.println("提交最后一次选择的译文失败！");
                                            }
                                        });
                                    }).start();
                                }
                            }
                        });

                        /*
                         * 最后一次提交的译文 TextArea
                         */
                        taLastChoice.setOnMouseClicked(event1 -> {
                            if (event1.getClickCount() == 2) {


                                replaceWord = taLastChoice.getSelectedText().trim();
                                //System.out.println(replaceWord);

                                if (!replaceWord.isEmpty()) {
                                    /**
                                     * 替换
                                     */
                                    controller.replaceWord(replaceWord);

                                    /**
                                     * 存入Hashmap，为实现恢复原词功能
                                     */
                                    transToWord.put(replaceWord, word);
                                    System.out.println(transToWord);
                                    /**
                                     * 提交最后一次选择的译文
                                     */
                                    new Thread(() -> {
                                        Platform.runLater(() -> {
                                            try {
                                                //调用提交最后一次选择的译文模块
                                                submitLastChoice(taLastChoice.getSelectedText().trim());
                                            } catch (Exception e) {
                                                Alert alert = new Alert(Alert.AlertType.ERROR, "提交译文失败！");
                                                alert.setHeaderText(null);
                                                alert.showAndWait();
                                                e.printStackTrace();
                                                System.out.println("提交最后一次选择的译文失败！");
                                            }
                                        });
                                    }).start();
                                }
                            }
                        });


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("init Translate Result UI success!");
                });

                mInitServiceOnline.setOnFailed(event -> {
                    pi.setDisable(true); // disable pi
                    pi.setVisible(false);
                    //dismiss window
                    Stage stage = (Stage) btnAddWord.getScene().getWindow();
                    stage.close();
                    //alert user
                    Alert alert = new Alert(Alert.AlertType.ERROR, "获取单词译文失败！\n网络连接异常！");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                    System.out.println("Network error!");
                });
            }

        } else {
            if (isChinese(word)) {
                /*
                 * 未登录状态，并且含有中文 - 加载 translate_result_chinese.fxml 布局
                 */
                FXMLLoader loader = new FXMLLoader(
                        getClass().getClassLoader().getResource("Resource/fxml/translate_result_chinese.fxml")
                );
                loader.setController(this);
                Parent root = loader.load();
                Scene scene = new Scene(root, 70, 25);
                primaryStage.setTitle("翻译结果");
                primaryStage.setScene(scene);
                primaryStage.setResizable(false);
                primaryStage.getIcons().add(new Image("/Resource/icon/mainicon.png"));
                primaryStage.show();
            } else {
                /*
                 * 未登录状态 加载 translate_result_offline.fxml 布局
                 */
                FXMLLoader loader = new FXMLLoader(
                        getClass().getClassLoader().getResource("Resource/fxml/translate_result_offline.fxml")
                );
                loader.setController(this);
                Parent root = loader.load();

                Scene scene = new Scene(root, 300, 250);
                primaryStage.setTitle("翻译结果");
                primaryStage.setScene(scene);
                primaryStage.setResizable(false);
                primaryStage.getIcons().add(new Image("/Resource/icon/mainicon.png"));
                primaryStage.show();
            }

            // create offline progress indicator
            pi = new ProgressIndicator(); // create progress indicator
            //pi.setMinSize(60,60);
            pi.setPrefSize(60, 60); //set size
            pi.setLayoutX(anchorPaneOffline.getWidth() / 2 - 30);    //set location
            pi.setLayoutY(anchorPaneOffline.getHeight() / 2 - 30);
            anchorPaneOffline.getChildren().add(pi);

            mInitServiceOffline.restart();

            mInitServiceOffline.setOnSucceeded(event -> {
                setUpUIOffline();//执行顺利,初始化UI
                pi.setDisable(true); // disable pi
                pi.setVisible(false);

                /*
                 * 翻译结果 TextArea
                 */
                taTransResult.setOnMouseClicked(event1 -> {
                    if (event1.getClickCount() == 2) {


                        replaceWord = taTransResult.getSelectedText().trim();
                        //System.out.println(replaceWord);
                        if (!replaceWord.isEmpty()) {
                            /**
                             * 替换
                             */
                            controller.replaceWord(replaceWord);

                            /**
                             * 存入Hashmap，为实现恢复原词功能
                             */
                            transToWord.put(replaceWord, word);
                            System.out.println(transToWord);
                        }
                    }
                });

            });

            //offline service onfailed
            mInitServiceOffline.setOnFailed(event -> {
                pi.setDisable(true); // disable pi
                pi.setVisible(false);
                //dismiss window
                Stage stage = (Stage) taTransResult.getScene().getWindow();
                stage.close();
                //alert user
                Alert alert = new Alert(Alert.AlertType.ERROR, "获取单词译文失败！\n网络连接异常！");
                alert.setHeaderText(null);
                alert.showAndWait();
                System.out.println("Network error!");
            });
        }


    }

    /**
     * offline 翻译结果
     */
    private void setUpUIOffline() {
        /*
         * 将结果加载到翻译结果页面
         */
        tSrcWord.setText(word);
        ukPhoneticSymbol.setText(Dictionary.EnglishPhoneticSymbol);
        usPhoneticSymbol.setText(Dictionary.AmericanPhoneticSymbol);
        this.ukUrl = Dictionary.EnglishAccentUrl;
        this.usUrl = Dictionary.AmericanAccentUrl;
        //无发音，则喇叭不可见
        if (ukUrl.equals(" ")) {
            ukVoice.setVisible(false);
        }
        if (usUrl.equals(" ")) {
            usVoice.setVisible(false);
        }

        taTransResult.setText(transResult);
    }

    /**
     * 显示 翻译结果 页面
     *
     * @param account    账号
     * @param isOnline   状态
     * @param srcWord    选中翻译的单词
     * @param X
     * @param Y
     * @param controller
     * @throws Exception
     */
    public void showWindow(String account, boolean isOnline,
                           String srcWord, double X, double Y, MainPage controller) throws Exception {
        this.account = account;
        this.controller = controller;
        this.word = srcWord;
        this.isOnline = isOnline;

        //获取屏幕大小并让窗口总出现在屏幕范围内
        Toolkit tool = Toolkit.getDefaultToolkit();
        Dimension screen = tool.getScreenSize();

        if (isOnline) {
            if (!isChinese(word)) {
                if (X > (screen.width - 300)) {
                    X = X - 300;
                }
                if (Y > (screen.height - 480)) {
                    Y = Y - 480;
                }
            }
        } else {
            if (X > (screen.width - 300)) {
                X = X - 300;
            }
            if (Y > (screen.height - 240)) {
                Y = Y - 250;
            }
        }
        stage.setX(X);
        stage.setY(Y);
        start(stage);
    }

    private Service<Void> mInitServiceOnline = new Service<>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<>() {
                @Override
                protected Void call() throws Exception {
                    String str = Constant.URL_GetAll + "account=" + account
                            + "&word=" + java.net.URLEncoder.encode(word, "utf-8");
                    //获取str对应Url的连接响应
                    String response = UrlUtil.openConnection(str);
                    System.out.println("response:" + response);

                    /*
                     * 解析 最后一次选择的单词
                     */
                    Pattern p = Pattern.compile("(.*)(<last>)(.*)(</last>)(.*)");
                    Matcher m = p.matcher(response);
                    if (m.find()) {
                        lastChoice = m.group(3);
                    }
                    System.out.println("lastChoice:" + lastChoice);

                    /*
                     * 解析 用户自己提交的译文
                     */
                    p = Pattern.compile("(.*)(<selftrans>)(.*)(</selftrans>)(.*)");
                    m = p.matcher(response);
                    if (m.find()) {
                        selfTranslation = m.group(3);
                    }
                    System.out.println("selfTranslation:" + selfTranslation);

                    /*
                     * 解析 其他用户的译文
                     */
                    p = Pattern.compile("(.*)(<othertrans>)(.*)(</othertrans>)(.*)");
                    m = p.matcher(response);
                    if (m.find()) {
                        otherTranslation = m.group(3);
                    }
                    System.out.println("otherTranslation:" + otherTranslation);

                    String[] otherTranslationArray = otherTranslation.split("<span>");

                    stringBuffer = new StringBuffer();
                    for (int i = 0; i < otherTranslationArray.length; i++) {
                        stringBuffer.append(otherTranslationArray[i]);
                        stringBuffer.append("\n");
                    }

                    /*
                     * 解析 用户是否将词加入生词本
                     */
                    p = Pattern.compile("(.*)(<newword>)(.*)(</newword>)(.*)");
                    m = p.matcher(response);
                    if (m.find()) {
                        queryNewWord = m.group(3);
                    }
                    System.out.println("in new wordbook?" + queryNewWord);


                    //翻译
                    transResult = MainPage.search(word);
                    System.out.println("translate result:" + transResult);
                    return null;
                }
            };
        }
    };

    private Service<Void> mInitServiceOffline = new Service<>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<>() {
                @Override
                protected Void call() throws Exception {
                    //翻译
                    transResult = MainPage.search(word);
                    System.out.println("translate result:" + transResult);
                    return null;
                }
            };
        }
    };

    /**
     * 初始化UI
     * 包括：自己提交过的译文、其他用户的译文、最后一次选择使用的译文
     *
     * @throws IOException
     */
    private void setUpUIOnline() throws IOException {

        /*
         * 将结果加载到翻译结果页面
         */
        tSrcWord.setText(word);
        ukPhoneticSymbol.setText(Dictionary.EnglishPhoneticSymbol);
        usPhoneticSymbol.setText(Dictionary.AmericanPhoneticSymbol);
        this.ukUrl = Dictionary.EnglishAccentUrl;
        this.usUrl = Dictionary.AmericanAccentUrl;
        //无发音，则喇叭不可见
        if (ukUrl.equals(" ")) {
            ukVoice.setVisible(false);
        }
        if (usUrl.equals(" ")) {
            usVoice.setVisible(false);
        }

        taTransResult.setText(transResult);

        if (!otherTranslation.isEmpty()) {
            taOtherTransResult.setText(stringBuffer.toString());
        }

        if (!selfTranslation.isEmpty() && !selfTranslation.equals("null")) {
            taSelfTrans.setText(selfTranslation);
        }

        if (!lastChoice.isEmpty() && !lastChoice.equals("null")) {
            taLastChoice.setText(lastChoice);
        }

        if (queryNewWord.equals("true")) {
            iconAddWord.setVisible(true);
            iconUnAddWord.setVisible(false);
        } else {
            iconAddWord.setVisible(false);
            iconUnAddWord.setVisible(true);
        }

    }


    /**
     * 编辑译文 按钮
     *
     * @param event
     */
    public void edit(ActionEvent event) throws Exception {
        new OfferTranslation().showWindow(account, word, TranslateResult.this);
    }

    /**
     * 恢复原文按钮
     */
    public void recover() {

        if (transToWord.get(word) != null) {
            controller.recoverWord(transToWord.get(word));
        } else {
            controller.recoverWord(word);
        }
//        System.out.println(word);
//        System.out.println(transToWord);
//        System.out.println(transToWord.get(word));

    }


    /**
     * 加入生词本模块
     */
    private void addNewWord() {

        try {

            URL url = new URL(Constant.URL_AddNewWord + "account=" + account + "&" + "word=" + word);
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

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "加入生词本成功！");
                alert.setHeaderText(null);
                alert.showAndWait();

                System.out.println("加入生词本成功！");
            } else if (sb.toString().equals(Constant.FLAG_ACCOUNT_EXIST)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "该单词已加入生词本！");
                alert.setHeaderText(null);
                alert.showAndWait();

                System.out.println("该单词已加入生词本！");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "加入生词本失败！");
                alert.setHeaderText(null);
                alert.showAndWait();

                System.out.println("加入生词本失败！");
            }
        } catch (Exception e) {
            //编码错误，即将中文加入生词本的情况
            Alert alert = new Alert(Alert.AlertType.ERROR, "仅能将英文单词加入生词本！");
            alert.setHeaderText(null);
            alert.showAndWait();

            e.printStackTrace();
        }
    }

    /**
     * 移出生词本模块
     */
    private void removeNewWOrd() {
        try {
            URL url = new URL(Constant.URL_RemoveWord + "account=" + account + "&word=" + word);
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

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "移出生词本成功！");
                alert.setHeaderText(null);
                alert.showAndWait();

                System.out.println("移出生词本成功！");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "移出生词本失败！");
                alert.setHeaderText(null);
                alert.showAndWait();

                System.out.println("移出生词本失败！");
            }

        } catch (Exception e) {
            //移出生词本失败的情况
            Alert alert = new Alert(Alert.AlertType.ERROR, "移出生词本失败！");
            alert.setHeaderText(null);
            alert.showAndWait();

            e.printStackTrace();
        }

    }

    /**
     * 英式发音
     *
     * @throws IOException
     * @throws JavaLayerException
     */
    public void pronounceUk() throws IOException, JavaLayerException {
        URL url = new URL(ukUrl);
        BufferedInputStream buffer = new BufferedInputStream(url.openStream());
        Player player = new Player(buffer);
        player.play();
    }

    /**
     * 美式发音
     *
     * @throws IOException
     * @throws JavaLayerException
     */
    public void pronounceUs() throws IOException, JavaLayerException {
        URL url = new URL(usUrl);
        BufferedInputStream buffer = new BufferedInputStream(url.openStream());
        Player player = new Player(buffer);
        player.play();
    }

    /**
     * 设置用户自己的译文
     * 在 OfferTranslation中调用
     *
     * @param selfTrans
     */
    public void setSelfTrans(String selfTrans) {

        taSelfTrans.setText(selfTrans);
    }

    /**
     * 提交最后一次选择的译文模块
     *
     * @param lastChoice
     */
    public void submitLastChoice(String lastChoice) {


        try {
            // 获取账号、单词和提交的译文
            String account = this.account;
            String word = this.word;

            /**
             * 坑来了！！
             * 传入含有中文字符的URL，需要将其进行编码
             */
            URL url = new URL(Constant.URL_SetLastChoice + "account=" + account + "&"
                    + "word=" + java.net.URLEncoder.encode(word, "utf-8") + "&"
                    + "lastchoice=" + java.net.URLEncoder.encode(lastChoice, "utf-8"));
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


                System.out.println("提交最后一次选择的译文成功！");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "提交最后一次选择的译文失败！");
                alert.setHeaderText(null);
                alert.showAndWait();

                System.out.println("提交最后一次选择的译文失败！");
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "网络连接异常，提交最后一次选择的译文失败！");
            alert.setHeaderText(null);
            alert.showAndWait();

            e.printStackTrace();
        }
    }

    /**
     * 判断所选内容中是否含有中文
     *
     * @param strName
     * @return
     */
    public boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }


    /**
     * 鼠标移出界面 事件处理
     */
    private void moveout() {
        //销毁当前窗口
        Stage stage = (Stage) btnAddWord.getScene().getWindow();
        stage.close();
    }

}
