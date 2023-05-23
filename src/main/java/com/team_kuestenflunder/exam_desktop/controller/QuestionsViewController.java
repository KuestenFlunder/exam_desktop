package com.team_kuestenflunder.exam_desktop.controller;

import com.google.inject.Inject;
import com.team_kuestenflunder.exam_desktop.SceneManager;
import com.team_kuestenflunder.exam_desktop.entity.Question;
import com.team_kuestenflunder.exam_desktop.services.QuestionsViewServiceImpl;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class QuestionsViewController implements Initializable {

    private final QuestionsViewServiceImpl questionsViewService;
    private final SceneManager sceneManager = new SceneManager();

    Question question;

    @FXML
    Button newQuestion_btn, bt_updateQuestion, bt_deleteQuestion;

    @FXML
    ListView<Question> lstw_QuestionList;

    @Inject
    public QuestionsViewController(QuestionsViewServiceImpl questionsViewService) {
        this.questionsViewService = questionsViewService;
    }

    public void onNewQuestionClick(ActionEvent event) {
        try {
            sceneManager.switchSceneToQuestionForm(event,new Question());}
        catch (Exception e) {e.printStackTrace();}
    }

    public void onUpdateButtonClick(ActionEvent event) {
        System.out.println("\"on updateClick\" = " + "on updateClick");

    }


    public void onDeleteButtonClick() {
        Question selectedQuestion =  lstw_QuestionList.getSelectionModel().getSelectedItem();
        try{
            if (selectedQuestion != null) {
                Alert alert = alertMessage(Alert.AlertType.WARNING, "Frage löschen", "MÖCHTEN SIE DIESE FRAGE UNWIEDERRUFLICH LÖSCHEN ?");
                if (alert.getResult() == ButtonType.OK){
                    questionsViewService.deleteQuestion(selectedQuestion);
                }
            }else {
                alertMessage(Alert.AlertType.INFORMATION, "Bitte Frage wählen", "UM ZU LÖSCHEN WÄHLEN SIE ERSTMAL EINE FRAGE");
            }
        }catch (Exception e) {
            e.printStackTrace();}
    }


    private Alert alertMessage(Alert.AlertType alertType, String titelText, String messageText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(titelText);
        alert.setContentText(messageText);
        alert.showAndWait();
        return alert;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lstw_QuestionList.setItems((ObservableList<Question>) questionsViewService.getQuestions());
        lstw_QuestionList.setCellFactory(param -> new ListCell<Question>() {
                @Override
                protected void updateItem(Question question, boolean empty) {
                    super.updateItem(question, empty);
                    if (empty || question == null) {
                        setText(null);
                    } else {
                        setText(question.getId() + " - " + question.getTopic()  + " - " + question.getQuestionTitle() );
                    }
                }
            });
    }





}
