package com.team_kuestenflunder.exam_desktop.controller;


import com.team_kuestenflunder.exam_desktop.SceneManager;
import com.team_kuestenflunder.exam_desktop.Utils.PDFHandler;
import com.team_kuestenflunder.exam_desktop.entity.ExamResult;
import com.team_kuestenflunder.exam_desktop.services.ExamEvaluationViewService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExamEvaluationViewController {

    ExamEvaluationViewService examEvaluationViewService;
    SceneManager sceneManager = SceneManager.getInstance();

    //TODO Dependency injection for both


    ObservableList<ExamResult> examResults = FXCollections.observableArrayList();
    @FXML
    Button bt_backToQuestionView;
    @FXML
    MenuItem mi_evaluateExams, mi_evaluateExam;
    @FXML
    TableView<ExamResult> tv_examResults;
    //TODO dependency Injection
    public ExamEvaluationViewController() {
        this.examEvaluationViewService = new ExamEvaluationViewService();
    }

    public void onBackToQuestionViewClick(ActionEvent event) {
        try {
            sceneManager.switchSceneToQuestionView(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onEvaluateMultiExamsClick(ActionEvent event) {

        List<File> examFiles = sceneManager.addFileChooserDialogMultiple(event, "PDF", "*.pdf");

        for (File file : examFiles) {
            examResults.add(
                    examEvaluationViewService.evaluateExam(
                            PDFHandler.getValuesFromTest(file)));
        }
        examEvaluationViewService.createViewTable(tv_examResults,examResults);

    }

    public void onEvaluateExamsClick(ActionEvent event) {
        File singleExam = sceneManager.addFileChooserDialogSingle(event, "PDF", "*.pdf");
        examResults.add(
                examEvaluationViewService.evaluateExam(
                        PDFHandler.getValuesFromTest(singleExam)));
        examEvaluationViewService.createViewTable(tv_examResults, examResults);
    }

}
