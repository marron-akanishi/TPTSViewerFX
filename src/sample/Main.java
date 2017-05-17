package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.StatusBar;

import java.sql.ResultSet;
import java.sql.Time;
import java.util.*;
import java.io.File;
import java.sql.SQLException;


public class Main extends Application {

    @FXML
    private Label debugLabel;
    @FXML
    private Pane pane;
    @FXML
    private ImageView MainView;
    @FXML
    private Label URLLabel;
    @FXML
    private Label IDLabel;
    @FXML
    private Label FavLabel;
    @FXML
    private Label RTLabel;
    @FXML
    private Label TimeLabel;
    @FXML
    private Label HashTagLabel;
    @FXML
    private StatusBar status;

    private String BasePath;
    private Database mainDB;
    private int MaxCount;
    private int NowCount = 0;
    private List<String> Filelist = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(700);
        primaryStage.setTitle("TPTS Viewer FX");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void SceneMaker(int no) {
        File tempfile = null;
        for (String path:Filelist) {
            if(path.contains(String.format("%05d",no))){
                tempfile = new File(path);
                break;
            }
        }
        if(tempfile == null){
            status.setText("ファイルが存在しません：" + String.format("%05d",no));
            return;
        }
        SetImage(new Image(tempfile.toURI().toString()));
        try {
            ResultSet rs = mainDB.SearchFile(tempfile.getName().toString());
            while (rs.next()) {
                URLLabel.setText("URL：" + rs.getString("url"));
                IDLabel.setText("ID：" + rs.getString("username"));
                FavLabel.setText("Fav_Count：" + rs.getString("fav"));
                RTLabel.setText("RT_Count：" + rs.getString("retweet"));
                HashTagLabel.setText("HashTag：" + rs.getString("tags"));
                TimeLabel.setText("Time：" + rs.getString("time"));
            }
        }catch (Exception e){
            status.setText("DBエラー");
            return;
        }
        status.setText(tempfile.toPath().toString());
    }

    private void SetImage(Image image){
        MainView.setPreserveRatio(true);
        MainView.setSmooth(true);
        MainView.setFitWidth(image.getWidth());
        MainView.setFitHeight(image.getHeight());
        if(image.getWidth() > pane.getWidth()) MainView.setFitWidth(pane.getWidth());
        if(image.getHeight() > pane.getHeight()) MainView.setFitHeight(pane.getHeight());
        // 中心移動
        MainView.setX(pane.getWidth()/2 - MainView.getFitWidth()/2);
        MainView.setY(pane.getHeight()/2 - MainView.getFitHeight()/2);
        MainView.setImage(image);
        debugLabel.setText(pane.getWidth() + "x" + pane.getHeight() + " - " + MainView.getFitWidth() + "x" + MainView.getFitHeight());
        pane.requestLayout();
    }

    public void OpenMenuAction(ActionEvent event) throws SQLException {
        FileChooser fc = new FileChooser();
        fc.setTitle("ファイルを開く");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("DBファイル","*.db"));
        File importFile = fc.showOpenDialog(pane.getScene().getWindow());
        if (importFile != null) {
            BasePath = importFile.getParent();
            mainDB = new Database(importFile.toURI().toString());
            MaxCount = mainDB.GetMaxCount();
            File[] files = new File(BasePath).listFiles(pathName -> pathName.isFile());
            for (File file:files) {
                Filelist.add(file.toPath().toString());
            }
            SceneMaker(NowCount = 0);
        }
    }

    public void ReloadMenuAction(ActionEvent event) throws SQLException{
        if(mainDB != null) mainDB.Reload();
        MaxCount = mainDB.GetMaxCount();
        File[] files = new File(BasePath).listFiles(pathName -> pathName.isFile());
        for (File file:files) {
            Filelist.add(file.toPath().toString());
        }
        status.setText("DBファイルをリロードしました");
    }

    public void NextMenuAction(ActionEvent event){
        NowCount++;
        if(NowCount >= MaxCount) NowCount = 0;
        SceneMaker(NowCount);
    }

    public void PrevMenuAction(ActionEvent event){
        NowCount--;
        if(NowCount < 0) NowCount = MaxCount - 1;
        SceneMaker(NowCount);
    }

    public void GoTopMenuAction(ActionEvent event){
        NowCount = 0;
        SceneMaker(NowCount);
    }

    public void GoEndMenuAction(ActionEvent event){
        NowCount = MaxCount - 1;
        SceneMaker(NowCount);
    }
}
