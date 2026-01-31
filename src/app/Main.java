package app;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.stage.Stage;

import service.DBService;

public class Main extends Application 
{

    @Override
    public void start(Stage stage) 
    {
        SceneManager.init(stage);
        SceneManager.showLogin();
        stage.setWidth(800);
        stage.setHeight(600);
        stage.centerOnScreen();
        stage.setTitle("Przychodnia");
        stage.show();
    }

    public static void main(String[] args) 
    {
        try 
        {
            DBService dbService = DBService.getInstance();
            dbService.initializeDatabase();
            launch(args);
        } catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
    }
}
