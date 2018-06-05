package Reader;

import Constant.Constant;
import Util.AlertMaker;
import Util.UrlUtil;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class NewWordList extends Application {

    private Stage stage = new Stage();
    private String account; //传入账号

    private String word;
    @FXML
    private
    ProgressIndicator pi;
    @FXML
    private
    TableView<WordBook> tableView;
    @FXML
    private
    TableColumn colWord; //word column
    @FXML
    private
    TableColumn colTranslation; //translation column
    @FXML
    private
    AnchorPane anchorPane; //root pane


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("Resource/fxml/new_word_list.fxml")
        );
        loader.setController(this);
        Parent root = loader.load();
        primaryStage.setTitle("生词本");
        primaryStage.setScene(new Scene(root, 500, 600));
        primaryStage.getIcons().add(new Image("/Resource/icon/mainicon.png"));
        primaryStage.show();

        // progress indicator
        pi = new ProgressIndicator(); // create progress indicator
        pi.setPrefSize(60, 60); //set size
        pi.setLayoutX(anchorPane.getWidth() / 2 - 30);    //set location
        pi.setLayoutY(anchorPane.getHeight() / 2 - 30);
        anchorPane.getChildren().add(pi); //add to anchorPane
        // bind progress to wordTask so that it shows progress accordingly
        // very important
        pi.progressProperty().bind(getWordTask.progressProperty());
        new Thread(getWordTask).start(); // the actual thread to get <getWordTask> going


        // ******************
        // 注意下这里进行的onSucceeded不是指wordTask获取到成功标志而做的事情
        // 只是wordTask顺利完成了call中执行的语句后的回调,无法判断执行后是否是成功还是失败
        // 只要该task顺利完成，过程中没有语句错误,即认为Task succeeded
        // same like service down below
        // ******************
        getWordTask.setOnSucceeded(event -> {
            //so we have to add custom logic here
            switch (getWordTask.getValue()) { //getValue method get the return value of the call method in Task
                case Constant.FLAG_SUCCESS: // success
                    pi.setDisable(true); // disable pi
                    pi.setVisible(false);
                    System.out.println("get new word list SUCCEEDED");
                    //resize column to fit screen
                    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                    tableView.setVisible(true);
                    break;
                case Constant.FLAG_NULL: // when user got no new word in word book
                    System.out.println("get new word list NULL");
                    stage.close();
                    AlertMaker.showSimpleAlert("Alert", "您尚未添加任何单词!");
                    break;
                case Constant.FLAG_FAIL: // almost never happens
                    System.out.println("get new word list FAILED");
                    stage.close();
                    AlertMaker.showErrorMessage("Error", "获取列表错误!");
                    break;
            }
        });
        // This part is only called when the call method in Task
        // Encountered something wrong
        // Also almost never happens
        getWordTask.setOnFailed(event -> {
            stage.close();
            AlertMaker.showErrorMessage("Error", "网络错误!请检查网络连接");
        });


        // just like in task
        mRemoveService.setOnSucceeded(event -> {
            switch (mRemoveService.getValue()){
                case Constant.FLAG_SUCCESS:
                    //get selected row and delete it if it's been deleted from database
                    WordBook i = tableView.getSelectionModel().getSelectedItem();
                    tableView.getItems().remove(i);// remove it
                    // get a log
                    System.out.println("Word:" + word + " removeD");
                    break;
                case Constant.FLAG_FAIL:
                    AlertMaker.showErrorMessage("Error","移除失败!");
                    break;
            }
        });
        // again, almost never happens
        mRemoveService.setOnFailed(event -> AlertMaker.showErrorMessage("Error", "网络错误!请检查网络连接"));

        // set row right click event
        tableView.setRowFactory(tv -> {
            TableRow<WordBook> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                // detect right click
                if (event.getButton() == MouseButton.SECONDARY && (!row.isEmpty())) {
                    // pop up menu
                    ContextMenu cm = new ContextMenu();
                    MenuItem mi = new MenuItem("移除");
                    //set menu click event
                    mi.setOnAction(event1 -> {
                        WordBook rowData = row.getItem();
                        word = rowData.getWord();
                        // Has to call restart method since service have to initialize
                        // Status to reExecute
                        mRemoveService.restart();
                    });
                    cm.getItems().add(mi);//add it to context menu
                    cm.show(stage,event.getScreenX(),event.getScreenY());//show it on screen
                }
            });
            return row;
        });
    }

    /**
     * 显示 生词本 界面
     *
     * @param account 账号
     */
    public void showWindow(String account) throws IOException {

        this.account = account;

        start(stage);
    }

    /**
     * Get word list task
     * Differentiate from Service
     */
    private Task<String> getWordTask = new Task<>() {

        @Override
        protected String call() {
            String FLAG = null;
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
                switch (responseArray[0]) { // flag of success or failure

                    case Constant.FLAG_SUCCESS: //on success

                        String[] wl = responseArray[1].split(",");//get word array
                        colWord.setCellValueFactory(
                                new PropertyValueFactory<>("word")
                        ); //set column <word>

                        colTranslation.setCellValueFactory(
                                new PropertyValueFactory<>("translation")
                        );//set column <transaltion>
                        ArrayList<WordBook> al = new ArrayList<>(); //WordBook arraylist to store Wordbook records
                        for (int i = 0; i < wl.length; i++) {
                            String trans = MainPage.search(wl[i]); //search word for translation
                            al.add(new WordBook(wl[i], trans)); //create WordBook to store this record and add it to ArrayList
                            // this is where we update the progress
                            // so that progressIndicator that binds this wordTask updates
                            updateProgress(i, wl.length);
                        }
                        ObservableList<WordBook> data = FXCollections.observableArrayList(al);// has to be observableList to set tableVIew data Source
                        tableView.setItems(data); //set tableView data source
                        FLAG = Constant.FLAG_SUCCESS; //set FLAG
                        break;
                    case Constant.FLAG_FAIL: //on fail
                        FLAG = Constant.FLAG_FAIL; //set FLAG
                        break;
                    case Constant.FLAG_NULL: //on word book null
                        FLAG = Constant.FLAG_NULL; //set FLAG
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return FLAG;
        }
    };

    /**
     * Remove word service
     * Do not use task since it can only be exeD once
     *
     */
    private Service<String> mRemoveService = new Service<>() {
        @Override
        protected Task<String> createTask() {
            return new Task<>() { // just like in task
                @Override
                protected String call() {
                    String FLAG = null;
                    try {
                        String str = Constant.URL_RemoveWord +
                                "account=" + account
                                + "&word=" + word;
                        String result = UrlUtil.openConnection(str);

                        // judge results
                        switch (result) { // flag of success or failure
                            case Constant.FLAG_SUCCESS: //on success
                                FLAG = Constant.FLAG_SUCCESS;
                                break;
                            case Constant.FLAG_FAIL: //on fail
                                FLAG = Constant.FLAG_FAIL; //set FLAG
                                break;
                            case Constant.FLAG_NULL: //on word book null
                                FLAG = Constant.FLAG_NULL; //set FLAG
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return FLAG;
                }
            };
        }
    };

    /**
     * Did it with inner class
     * This is torture, writing java ui with bare codes
     * And with scene builder and loaded demo not working...
     */
    public static class WordBook {

        private final SimpleStringProperty word;
        private final SimpleStringProperty translation;

        private WordBook(String word, String translation) {
            this.word = new SimpleStringProperty(word);
            this.translation = new SimpleStringProperty(translation);
        }

        public String getWord() {
            return word.get();
        }

        public void setWord(String word) {
            this.word.set(word);
        }

        public String getTranslation() {
            return translation.get();
        }

        public void setTranslation(String translation) {
            this.translation.set(translation);
        }
    }
}


