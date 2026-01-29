package app;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        SceneManager.init(stage);
        SceneManager.showLogin();
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setTitle("Przychodnia");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
