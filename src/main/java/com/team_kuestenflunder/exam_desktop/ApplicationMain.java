package com.team_kuestenflunder.exam_desktop;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.team_kuestenflunder.exam_desktop.Utils.CSVHandler;
import com.team_kuestenflunder.exam_desktop.Utils.JsonHandler;
import com.team_kuestenflunder.exam_desktop.moduls.DIConfigModule;
import com.team_kuestenflunder.exam_desktop.repository.DataService;
import com.team_kuestenflunder.exam_desktop.repository.QuestionRepositoryImpl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicationMain extends Application {
    Injector injector = Guice.createInjector(new DIConfigModule());
    private final SceneManager sceneManager = SceneManager.getInstance();
    private final DataService dataService = DataService.getInstance();
    private final CSVHandler csvHandler = new CSVHandler();
    private JsonHandler jsonHandler = new JsonHandler();

    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage stage) throws Exception {
        dataService.initDataService();
        Scene scene = sceneManager.getQuestionView();
        //set a Stage Title
        stage.setTitle("Fragen Liste");
        stage.setScene(scene);
        stage.show();
    }

    public void stop() throws Exception {
        csvHandler.writeCSV(dataService.getQuestions());
        jsonHandler.writeJson(dataService.getQuestions());
    }



}