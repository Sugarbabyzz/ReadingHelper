package Reader;

import Constant.Constant;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;

public class NewWordList extends Application {

    Stage stage = new Stage();
    String account; //传入账号

    @FXML
    ListView<String> lv_word_list;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("Layout/new_word_list.fxml")
        );
        loader.setController(this);
        Parent root = loader.load();

        primaryStage.setTitle("生词本");
        primaryStage.setScene(new Scene(root, 500, 300));
        primaryStage.getIcons().add(new Image("/Res/mainicon.png"));
        primaryStage.show();
    }

    /**
     * 显示 生词本 界面
     * @param account 账号
     */
    public void showWindow(String account) throws IOException {

        this.account = account;
        Platform.runLater(() -> {
            initWordList();
        });
        start(stage);
    }

    /**
     * 获取生词本列表
     */
    private void initWordList() {
        try {
            URL url = new URL(Constant.URL_GetNewWordList + "account=" + account);
            // 接收servlet返回值，是字节
            InputStream is = url.openStream();

            // 由于is是字节，所以我们要把它转换为String类型，否则遇到中文会出现乱码
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            //格式:  FLAG/take,where,twist,...
            String[] responseArray = sb.toString().split("/");
            switch (responseArray[0]){ // 标志位

                case Constant.FLAG_SUCCESS: //获取成功
                    //获取到生词数组
                    String[] wl = responseArray[1].split(",");
                    //设置listView数据,使用自定义布局
                    ObservableList<String> wordList = FXCollections.observableArrayList(
                            wl);
                    lv_word_list.setItems(wordList);
                    lv_word_list.setCellFactory(param -> new XCell());

                    break;
                case Constant.FLAG_FAIL: // 获取失败
                    Alert alert1 = new Alert(Alert.AlertType.ERROR, "获取失败!");
                    alert1.setHeaderText(null);
                    Optional<ButtonType> result1 = alert1.showAndWait();
                    if (result1.get() == ButtonType.OK){
                        stage.close();
                    }
                    break;
                case Constant.FLAG_NULL: // 这人没添加过任何生词
                    Alert alert2 = new Alert(Alert.AlertType.ERROR, "您尚未添加任何生词!");
                    alert2.setHeaderText(null);
                    Optional<ButtonType> result2 = alert2.showAndWait();
                    if (result2.get() == ButtonType.OK){
                        stage.close();
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            //网络不通的情况
            Alert alert = new Alert(Alert.AlertType.ERROR, "网络连接异常！");
            alert.setHeaderText(null);
            alert.showAndWait();

            e.printStackTrace();
        }
    }

    /**
     * listView子布局
     */
    static class XCell extends ListCell<String> {
        HBox hbox = new HBox();
        Label label = new Label("empty!!!");
        Pane pane = new Pane();
        Text text = new Text();
        ToggleButton button = new ToggleButton("释义");
        String lastItem;

        public XCell() {
            super();
            //hBox下放了Label:放词，button按钮，text放释义,pane用来撑开布局
            hbox.getChildren().addAll(label,button,text,pane);
            HBox.setHgrow(pane, Priority.ALWAYS);
            HBox.setMargin(button,new Insets(0,0,0,20));
            HBox.setMargin(text,new Insets(0,0,0,20));
            button.setOnAction(event -> {
                System.out.println("clicked item: " + lastItem);
                //显示与隐藏释义
                if (button.isSelected()) {
                    text.setText(MainPage.search(lastItem));
                    button.setText("隐藏");
                } else {
                    text.setText("");
                    button.setText("释义");
                }

            });
        }

        /**
         * 保证lastItem始终是最新选择的
         * @param item
         * @param empty
         */
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);  // No text in label of super class
            if (empty) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = item;
                label.setText(item!=null ? item : "<null>");
                setGraphic(hbox);
            }
        }
    }

}
