package Reader;

import Constant.Constant;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import de.jensd.fx.fontawesome.Icon;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

    MainPage controller;

    private static Stage stage = new Stage();

    private static String account; //账号
    private static Boolean isOnline;
    private static String word; //当前选中的单词
    private String replaceWord; //替换的释义

    private String ukUrl; //英式发音链接
    private String usUrl; //美式发音链接

    private static HashMap<String, String> transToWord = new HashMap<String, String>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        if (isOnline) {
            /*
             * 登录状态 加载 translate_result_online.fxml 布局
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
    }

    /**
     * 显示 翻译结果 页面
     *
     * @param account    账号
     * @param isOnline   状态
     * @param srcWord    选中翻译的单词
     * @param ukPhonetic
     * @param ukUrl
     * @param usPhonetic
     * @param usUrl
     * @param result
     * @param X
     * @param Y
     * @param controller
     * @throws Exception
     */
    public void showWindow(String account, boolean isOnline,
                           String srcWord, String ukPhonetic, String ukUrl, String usPhonetic, String usUrl,
                           String result, double X, double Y, MainPage controller) throws Exception {
        this.account = account;
        this.controller = controller;
        this.word = srcWord;
        this.isOnline = isOnline;

        //获取屏幕大小并让窗口总出现在屏幕范围内
        Toolkit tool = Toolkit.getDefaultToolkit();
        Dimension screen = tool.getScreenSize();

        if (isOnline) {
            if (X > (screen.width - 300)) {
                X = X - 300;
            }
            if (Y > (screen.height - 480)) {
                Y = Y - 480;
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

        tSrcWord.setText(srcWord);
        ukPhoneticSymbol.setText(ukPhonetic);
        usPhoneticSymbol.setText(usPhonetic);
        /*
         * 无发音，则喇叭不可见
         */
        this.ukUrl = ukUrl;
        this.usUrl = usUrl;
        if (ukUrl.equals(" ")) {
            ukVoice.setVisible(false);
        }
        if (usUrl.equals(" ")) {
            usVoice.setVisible(false);
        }

        taTransResult.setText(result);
        taTransResult.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if (event.getClickCount() == 2) {

                    /**
                     * 替换
                     */
                    replaceWord = taTransResult.getSelectedText().trim();
                    //System.out.println(replaceWord);
                    if (!replaceWord.isEmpty()) {
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
            }
        });


        /*
         * 登录状态加载以下功能
         */
        if (isOnline) {

            iconUnAddWord.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                new Thread(() -> {
                    Platform.runLater(() -> {
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
                    });
                }).start();
            });

            iconAddWord.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                new Thread(() -> {
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
                }).start();
            });



            taOtherTransResult.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {
                    if (event.getClickCount() == 2) {

                        /**
                         * 替换
                         */
                        replaceWord = taOtherTransResult.getSelectedText().trim();
                        //System.out.println(replaceWord);

                        if (!replaceWord.isEmpty()) {
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
                }
            });

            taSelfTrans.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {
                    if (event.getClickCount() == 2) {

                        /**
                         * 替换
                         */
                        replaceWord = taSelfTrans.getSelectedText().trim();
                        //System.out.println(replaceWord);

                        if (!replaceWord.isEmpty()) {
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
                }
            });

            taLastChoice.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent event) {
                    if (event.getClickCount() == 2) {

                        /**
                         * 替换
                         */
                        replaceWord = taLastChoice.getSelectedText().trim();
                        //System.out.println(replaceWord);

                        if (!replaceWord.isEmpty()) {
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
                }
            });

            /**
             * 初始化数据
             */
            new Thread(() -> {
                Platform.runLater(() -> {
                    try {
                        //调用初始化数据模块
                        initData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("初始化数据失败！");
                    }
                });
            }).start();
        }


    }



    /**
     * 初始化数据
     * 包括：自己提交过的译文、其他用户的译文、最后一次选择使用的译文
     *
     * @throws IOException
     */
    private void initData() throws IOException {

        URL url = new URL(Constant.URL_GetAll + "account=" + account + "&" + "word=" + java.net.URLEncoder.encode(word, "utf-8"));
        // 接收servlet返回值，是字节
        InputStream is = url.openStream();
        // 由于is是字节，所以我们要把它转换为String类型，否则遇到中文会出现乱码
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        String response = sb.toString();
        String lastChoice = " ";
        String selfTranslation = " ";
        String otherTranslation = " ";
        String queryNewWord = "";
        System.out.println(response);

        /*
         * 解析 最后一次选择的单词
         */
        Pattern p = Pattern.compile("(.*)(<last>)(.*)(</last>)(.*)");
        Matcher m = p.matcher(response);
        if (m.find()) {
            lastChoice = m.group(3);
        }
        System.out.println(lastChoice);

        /*
         * 解析 用户自己提交的译文
         */
        p = Pattern.compile("(.*)(<selftrans>)(.*)(</selftrans>)(.*)");
        m = p.matcher(response);
        if (m.find()) {
            selfTranslation = m.group(3);
        }
        System.out.println(selfTranslation);

        /*
         * 解析 其他用户的译文
         */
        p = Pattern.compile("(.*)(<othertrans>)(.*)(</othertrans>)(.*)");
        m = p.matcher(response);
        if (m.find()) {
            otherTranslation = m.group(3);
        }
        System.out.println(otherTranslation);

        String[] otherTranslationArray = otherTranslation.split("<span>");

        StringBuffer stringBuffer = new StringBuffer();
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
        System.out.println(selfTranslation);


        /*
         * 将结果加载到翻译结果页面
         */
        if (!otherTranslation.isEmpty()) {
            taOtherTransResult.setText(stringBuffer.toString());
        }

        if (!selfTranslation.isEmpty() && !selfTranslation.equals("null")) {
            taSelfTrans.setText(selfTranslation);
        }

        if (!lastChoice.isEmpty() && !lastChoice.equals("null")) {
            taLastChoice.setText(lastChoice);
        }

        if (queryNewWord.equals("true")){
            iconAddWord.setVisible(true);
            iconUnAddWord.setVisible(false);
        }else {
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

}
