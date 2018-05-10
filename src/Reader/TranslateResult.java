package Reader;

import Constant.Constant;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.*;
import java.net.URL;

public class TranslateResult extends Application {


    @FXML
    private Text tSrcWord;
    @FXML
    private TextArea taTransResult; //翻译结果
    @FXML
    private TextArea taOtherTransResult; // 别人提供的译文
    @FXML
    private TextArea taSelfTrans; //自己提供的译文
    @FXML
    private TextArea taLastChoice; //自己LastChoice
    @FXML
    private Button btnQuit;
    @FXML
    private Text ukPhoneticSymbol; //英式音标
    @FXML
    private Text usPhoneticSymbol; //美式音标
    @FXML
    private ImageView ukVoice; //英式发音图标
    @FXML
    private ImageView usVoice; //美式发音图标
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnAddWord;

    MainPage controller;

    private static String account; //账号
    private static String word; //当前选中的单词
    private String replaceWord; //替换的释义

    private String ukUrl; //英式发音链接
    private String usUrl; //美式发音链接

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {


        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("Layout/translate_result.fxml")
        );
        loader.setController(this);
        Parent root = loader.load();

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("翻译结果");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

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

        Stage stage = new Stage();
        stage.setX(X);
        stage.setY(Y);
        start(stage);

        tSrcWord.setText(srcWord);
        ukPhoneticSymbol.setText(ukPhonetic);
        usPhoneticSymbol.setText(usPhonetic);
        this.ukUrl = ukUrl;
        this.usUrl = usUrl;
        taTransResult.setText(result);
        taTransResult.setEditable(false);
        taTransResult.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if (event.getClickCount() == 2) {

                    /**
                     * 替换
                     */
                    replaceWord = taTransResult.getSelectedText().trim();
                    //System.out.println(replaceWord);

                    controller.replaceWord(replaceWord);

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
        btnEdit.setDisable(!isOnline);
        btnAddWord.setDisable(!isOnline);

        if (isOnline){
            /**
             * 初始化数据
             */
            new Thread(() -> {
                Platform.runLater(() -> {
                    try {
                        //调用初始化数据模块
                        initData();
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "初始化数据失败！");
                        alert.setHeaderText(null);
                        alert.showAndWait();
                        e.printStackTrace();
                        System.out.println("初始化数据失败！");
                    }
                });
            }).start();
        }
    }

    private void initData() throws IOException {
        URL url = new URL(Constant.URL_GetAll + "account=" + account + "&" + "word=" + java.net.URLEncoder.encode(word));
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
        System.out.println(response);
        String[] responseArray = response.split("///");

        String lastChoice = responseArray[0];
        String selfTranslation = responseArray[1];
        String otherTranslation = responseArray[2];

        String[] otherTranslationArray = otherTranslation.split(",,,");

        StringBuffer stringBuffer = new StringBuffer();
        for (int i=0; i<otherTranslationArray.length ; i++){
            stringBuffer.append(otherTranslationArray[i]);
            stringBuffer.append("\n");
        }
        if (!otherTranslation.equals(" ")){
            taOtherTransResult.setText(stringBuffer.toString());
        }
        if (!selfTranslation.equals(" ")){
            taSelfTrans.setText(selfTranslation);
        }
        if (!lastChoice.equals(" ")){
            taLastChoice.setText(lastChoice);
        }

        taOtherTransResult.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if (event.getClickCount() == 2) {

                    /**
                     * 替换
                     */
                    replaceWord = taTransResult.getSelectedText().trim();
                    //System.out.println(replaceWord);

                    controller.replaceWord(replaceWord);

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

        taSelfTrans.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if (event.getClickCount() == 2) {

                    /**
                     * 替换
                     */
                    replaceWord = taTransResult.getSelectedText().trim();
                    //System.out.println(replaceWord);

                    controller.replaceWord(replaceWord);

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
    }

    /**
     * 加入生词本按钮
     *
     * @param event
     */
    public void addWord(ActionEvent event) {

    }

    /**
     * 编辑按钮
     *
     * @param event
     */
    public void edit(ActionEvent event) throws Exception {
        new OfferTranslation().showWindow(account, word, TranslateResult.this);
    }

    /**
     * 恢复原文按钮
     *
     */
    public void recover() {

        controller.recoverWord(word);
    }


    /**
     * 退出按钮
     *
     * @param event
     */
    @FXML
    public void quit(ActionEvent event) {

        //销毁当前窗口
        Stage stage = (Stage) btnQuit.getScene().getWindow();
        stage.close();
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
            String lastchoice = java.net.URLEncoder.encode(lastChoice, "utf-8");

            URL url = new URL(Constant.URL_SetLastChoice + "account=" + account + "&" + "word=" + word + "&" + "lastchoice=" + lastchoice);
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
            //网络不通的情况
            Alert alert = new Alert(Alert.AlertType.ERROR, "网络连接异常，提交最后一次选择的译文失败！");
            alert.setHeaderText(null);
            alert.showAndWait();

            e.printStackTrace();
        }
    }
}
