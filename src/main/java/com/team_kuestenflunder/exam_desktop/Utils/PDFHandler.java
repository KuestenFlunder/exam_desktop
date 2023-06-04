package com.team_kuestenflunder.exam_desktop.Utils;

import com.google.inject.Inject;
import com.team_kuestenflunder.exam_desktop.entity.ExamValues;
import com.team_kuestenflunder.exam_desktop.entity.Question;
import com.team_kuestenflunder.exam_desktop.services.PdfCreationPopUpService;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class PDFHandler {
    @Inject
    PdfCreationPopUpService popUpService;

    public PDFHandler(){ }
    public PDFHandler(PdfCreationPopUpService popUpService) {
        this.popUpService = popUpService;
    }


// ---- METHODS FOR CREATING A SINGLE PDF-FILE ----                                       // Set<Question> examQuestions
    public void createPersonalExamTest (String testTitel,  Set<Question> examQuestions, int testDuration, String name, String surname) {
        PDFMergerUtility pdfTest = new PDFMergerUtility();
        try {
            // TitelPage
            String path_ToTitelPage = createTitelPage(testTitel, examQuestions, testDuration, name, surname);
            pdfTest.addSource(path_ToTitelPage);
            // n * QuestionPages
            int counter = 0;
            for (Question question : examQuestions) {
                counter++;
                String path_ToQuestionPage = createQuestionPage(examQuestions.size(), question, counter);
                pdfTest.addSource(path_ToQuestionPage);
            }
            // PDF zusammengefügt
            String path_toTestFile = "src/main/Output/TestFor_" + name + "_" + surname   +".pdf";
            pdfTest.setDestinationFileName(path_toTestFile);
            pdfTest.mergeDocuments(null);
            // Output-Ordner bereinigen
            cleanOutputDirectory(examQuestions);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Der namentliche PDF-Test wurde erstellt");
    }

    public  void createExamPDF(String testTitel, Set<Question> examQuestions, int testDuration) {    // TODO statt numberOfQuestion ein Set<Question> zu setzten
        //Set<Question> examQuestions = popUpService.getRandomExamQuestions(numberOfQuestions); // TODO

        PDFMergerUtility pdfTest = new PDFMergerUtility();
        try {
            // TitelPage
            String path_ToTitelPage = createTitelPage(testTitel, examQuestions, testDuration, null, null);
            pdfTest.addSource(path_ToTitelPage);
            // n * QuestionPages
            int counter = 0;
            for (Question question : examQuestions) {
                counter++;
                String path_ToQuestionPage = createQuestionPage(examQuestions.size(), question, counter);
                pdfTest.addSource(path_ToQuestionPage);
            }
            // PDF zusammengefügt
            String path_toTestFile = "src/main/Output/Test.pdf";
            pdfTest.setDestinationFileName(path_toTestFile);
            pdfTest.mergeDocuments(null);
            // Output-Ordner bereinigen
            cleanOutputDirectory(examQuestions);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("PDF-Dokument wurde erstellt");
    }

    private  String createTitelPage(String testTitel, Set<Question> examQuestions, int testDuration, String name, String surname) throws IOException {
        // DataTime
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.GERMAN);
        String dateOfTestCteation = date.format(formatter);
        // PDF-Page
        PDDocument titelPage = PDDocument.load(new File("src/main/resources/com/team_kuestenflunder/exam_desktop/templates/TitlePageLayout.pdf"));
        PDAcroForm acroFormTitelPage = titelPage.getDocumentCatalog().getAcroForm();
        setValueToField(acroFormTitelPage, "nameField", name);
        setValueToField(acroFormTitelPage, "surnameField", surname);
        setValueToField(acroFormTitelPage, "courseNameField", "Java Entwickler Oracle Certified Professional SE");
        setValueToField(acroFormTitelPage, "testTitelField", testTitel);
        setValueToField(acroFormTitelPage, "dateOfTestField", dateOfTestCteation);
        setValueToField(acroFormTitelPage, "numberOfQuestionsField", String.valueOf(examQuestions.size()));
        setValueToField(acroFormTitelPage, "testDutarionField", String.valueOf(testDuration));
        // PDF-File
        String filePath = "src/main/Output/TitlePage.pdf";
        titelPage.save(filePath);
        titelPage.close();

        return filePath;
    }

    private String createQuestionPage(int numberOfQuestions, Question question, int counter) throws IOException {
        PDDocument questionPage = PDDocument.load(new File("src/main/resources/com/team_kuestenflunder/exam_desktop/templates/QuestionLayout.pdf"));
        PDAcroForm acroQuestionPage = questionPage.getDocumentCatalog().getAcroForm();
        // set TextFields
        setValueToField(acroQuestionPage, "QuestionID_Field", question.getId());
        setValueToField(acroQuestionPage, "questionNumberField", String.valueOf(counter));
        setValueToField(acroQuestionPage, "numberOfQuestionsField", String.valueOf(numberOfQuestions));
        setValueToField(acroQuestionPage, "QuestionTextField", question.getQuestionText());
        setValueToField(acroQuestionPage, "QuestionCodeField", question.getQuestionCode());
        setValueToField(acroQuestionPage, "correctAnswersField", String.valueOf(question.getAnswers().getCorrectAnswers()));
        //mappingNames for QuestionID´s
        PDTextField tf_UUID = (PDTextField) acroQuestionPage.getField("QuestionID_Field");
        String tf_UUID_MappingName = "QuestionID_Field_" + counter + "" + 0;
        tf_UUID.setMappingName(tf_UUID_MappingName);
        // set AnswerFields
        for (int i = 0; i < question.getAnswers().getAnswerList().size(); i++) {
            setValueToField(acroQuestionPage, ("AnswerTextField_" + i), String.valueOf(question.getAnswers().getAnswerList().get(i).getAnswerText()));
            setValueToField(acroQuestionPage, ("AnswerCodeField_" + i), String.valueOf(question.getAnswers().getAnswerList().get(i).getAnswerDescription())); //TODO 'Description' durch 'AnswerCode' ersetzen
            //hidden correctAnswerBox to keep the correct answers
            PDCheckBox correctAnswerBox = (PDCheckBox) acroQuestionPage.getField("CorrectAnswerBox_" + i);
            PDAnnotationWidget widget = correctAnswerBox.getWidgets().get(0);
//            widget.setHidden(true);    //hide correctAnswers
            if (question.getAnswers().getAnswerList().get(i).isCorrectAnswer()) {
                correctAnswerBox.setValue(correctAnswerBox.getOnValue());
            }
            //mappingNames for correctAnswerBoxes
            String correctAnswerBox_MappingName = "CorrectAnswerBox_" + counter + "" +i;
            correctAnswerBox.setMappingName(correctAnswerBox_MappingName);
            //mappingNames for AnswerBoxes
            PDCheckBox answerBox = (PDCheckBox) acroQuestionPage.getField(("AnswerBox_" + i));
            String answerBox_NewMappingName = "AnswerBox_" + counter + "" +i;
            answerBox.setMappingName(answerBox_NewMappingName);
            acroQuestionPage.refreshAppearances();
        }
        String pathName = "src/main/Output/qsPage" + counter + ".pdf";
        questionPage.save(pathName);
        questionPage.close();

        return pathName;
    }

    private String createLastPage (int numberOfQuestions) throws IOException {
        PDDocument lastPage = PDDocument.load(new File("src/main/resources/com/team_kuestenflunder/exam_desktop/templates/LastPageLayout.pdf"));
        PDAcroForm acroFormLastPage = lastPage.getDocumentCatalog().getAcroForm();
        setValueToField(acroFormLastPage, "ResultTextField", "BESTANDEN");
        setValueToField(acroFormLastPage, "numberOfQuestionsField", String.valueOf(numberOfQuestions));
        setValueToField(acroFormLastPage, "correctAnswersAmountField", "?");

//????????? TODO Funktion in JavaScript
        PDPushButton evaluateButton = (PDPushButton) acroFormLastPage.getField("evaluateTheTest_Button");
        PDActionJavaScript button_JavaScript = new PDActionJavaScript();
        String absoluteFilePath = "/Users/jan-hendrykpassler/IdeaProjects/exam_desktop/src/main/Output/Test.pdf";
        String javascriptCode = "app.launchURL('mailto:kuestenflunder@gmail.com?subject=');";
        button_JavaScript.setAction(javascriptCode);
        PDAnnotationWidget evaluateButtonWidget = evaluateButton.getWidgets().get(0);
        evaluateButtonWidget.setAction(button_JavaScript);
//?????????
        String filePath = "src/main/Output/LastPage.pdf";
        lastPage.save(filePath);
        lastPage.close();

        return filePath;
    }

    private void cleanOutputDirectory (Set<Question> examQuestions){
        File titelPageFile = new File("src/main/Output/TitlePage.pdf");
        titelPageFile.delete();
        File lastPageFile = new File("src/main/Output/LastPage.pdf");
        lastPageFile.delete();
        for (int i = 1; i <= examQuestions.size(); i++) {
            File questionFile = new File("src/main/Output/qsPage" + i + ".pdf");
            questionFile.delete();
        }
    }

    private void setValueToField(PDAcroForm pdAcroForm, String fieldName, String value) throws IOException {
        PDField field = pdAcroForm.getField(fieldName);
        field.setValue(value);
    }



// ------ METHODS FOR EVALUATING A PDF-FILE -----------
    public ExamValues getValuesFromTest(File pdfFile) {
        ExamValues examValues = new ExamValues();
        try {
            examValues.setStudentName(getStudentNameFromFile(pdfFile));
            examValues.setStudentSurname(getStudentSurnameFromFile(pdfFile));
            examValues.setDateOfTest(getDate_FromFile(pdfFile));
            examValues.setNumberOfQuestions(getNumberOfQuestions_FromFile(pdfFile));
            examValues.setUUID_Map(getMap_UUIDsFromFile(pdfFile));
            examValues.setAnswer_Map(getMap_AnswerBoxesFromFile(pdfFile));
            examValues.setCorrectAnswer_Map(getMap_CorrectAnswerBoxesFromFile(pdfFile));
            examValues.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return examValues;
    }

    private String getStudentNameFromFile(File pdfFile) throws IOException{
        PDDocument document = PDDocument.load(pdfFile);
        PDAcroForm acroFormTitelPage = document.getDocumentCatalog().getAcroForm();
        PDTextField nameField = (PDTextField)acroFormTitelPage.getField("nameField");
        String name = nameField.getValue();
        document.close();
        return name;
    }

    private String getStudentSurnameFromFile(File pdfFile) throws IOException{
        PDDocument document = PDDocument.load(pdfFile);
        PDAcroForm acroFormTitelPage = document.getDocumentCatalog().getAcroForm();
        PDTextField surnameField = (PDTextField)acroFormTitelPage.getField("surnameField");
        String surname = surnameField.getValue();
        document.close();
        return surname;
    }

    private String getDate_FromFile(File pdfFile) throws IOException{
        PDDocument document = PDDocument.load(pdfFile);
        PDAcroForm acroFormTitelPage = document.getDocumentCatalog().getAcroForm();
        PDTextField textField = (PDTextField)acroFormTitelPage.getField("dateOfTestField");
        String date = textField.getValue();
        document.close();
        return date;
    }

    private int getNumberOfQuestions_FromFile(File pdfFile) throws IOException{
        PDDocument document = PDDocument.load(pdfFile);
        PDAcroForm acroFormTitelPage = document.getDocumentCatalog().getAcroForm();
        PDTextField textField = (PDTextField)acroFormTitelPage.getField("numberOfQuestionsField");
        int numberOfQuestions = Integer.parseInt(textField.getValue());
        document.close();
        return numberOfQuestions;
    }

    private Map<String, String> getMap_UUIDsFromFile (File pdfFile) throws IOException {
        PDDocument document = PDDocument.load(pdfFile);
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        List<PDField> fields = acroForm.getFields();

        Map<String, String> map_UUID = new TreeMap<>();
        for (PDField field : fields) {
            if (field instanceof PDTextField && field.getMappingName() != null) {
                map_UUID.put(field.getMappingName(), field.getValueAsString());
            }
        }
        return  map_UUID;
    }

    private Map<String, String> getMap_AnswerBoxesFromFile(File pdfFile) throws IOException{
        PDDocument document = PDDocument.load(pdfFile);
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        List<PDField> fields = acroForm.getFields();
        Map<String,String> answerBoxMap = new TreeMap<>();
        for (PDField field : fields){
            if (field instanceof PDCheckBox && field.getMappingName().contains("AnswerBox_")){
                answerBoxMap.put(field.getMappingName(), field.getValueAsString());
            }
        }
        return answerBoxMap;
    }

    private Map<String, String> getMap_CorrectAnswerBoxesFromFile (File pdfFile) throws IOException{
        PDDocument document = PDDocument.load(pdfFile);
        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
        List<PDField> fields = acroForm.getFields();
        Map<String,String> correctAnswerBoxMap = new TreeMap<>();
        for (PDField field : fields){
            if (field instanceof PDCheckBox && field.getMappingName().contains("CorrectAnswerBox_")){
                correctAnswerBoxMap.put(field.getMappingName(), field.getValueAsString());
            }
        }
        return correctAnswerBoxMap;
    }

    }
