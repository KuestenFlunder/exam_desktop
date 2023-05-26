package com.team_kuestenflunder.exam_desktop.controller;

import com.google.inject.Inject;
import com.team_kuestenflunder.exam_desktop.SceneManager;
import com.team_kuestenflunder.exam_desktop.services.PdfCreationPopUpService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PdfCreationPopUpController {
    PdfCreationPopUpService pdfCreationPopUpService;

    SceneManager sceneManager = new SceneManager();
    @FXML
    Button bt_createExam, bt_cancel;
    @FXML
    TextField
            tf_testTitle,
            tf_numberOfQuestions,
            tf_testDuration;

    @Inject
    public PdfCreationPopUpController(PdfCreationPopUpService pdfCreationPopUpService) {
        this.pdfCreationPopUpService = pdfCreationPopUpService;
    }

    public void onCreateTestClick() {
        System.out.println(tf_testTitle + " " + tf_testDuration + " " + tf_numberOfQuestions);
    }

    public void onCancelClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

    }


}