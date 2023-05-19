package com.team_kuestenflunder.exam_desktop.entity;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.UUID;

public class Question {


    private final String id;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime creationDate;

    private String questionTitle = "Kein Titel verfuegbar"; //? Might be useful if we find meaningful titles
    private Topics topic = Topics.No_Topic;
    private String questionText = "Kein Text verfuegbar";

    private String Code = "Kein Code verfuegbar ";
    private Answers answers;

    public Question() {
        this.creationDate = LocalDateTime.now();
        this.id = UUID.randomUUID().toString();
        this.answers = new Answers();
    }

    public Question(String questionTitle, Topics topic, String questionText, String code, Answers answers) {
        this.id = UUID.randomUUID().toString();
        this.creationDate = LocalDateTime.now();
        this.questionTitle = questionTitle;
        this.topic = topic;
        this.questionText = questionText;
        Code = code;
        this.answers = answers;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Answers getAnswerList() {
        return answers;
    }

    public void setAnswerList(Answers answers) {
        this.answers = answers;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public Topics getTopic() {
        return topic;
    }

    public void setTopic(Topics topic) {
        this.topic = topic;
    }






    @Override
    public String toString() {
        return "Question{" + "\n" +
                "id='" + id + '\'' +
                ", creationDate=" + creationDate +
                ", answers=" + answers.toString().replaceAll("\n", "") +
                ", Code='" + Code + '\'' +
                ", questionText='" + questionText + '\'' +
                ", questionTitle='" + questionTitle + '\'' +
                ", topic=" + topic +
                '}' + "\n";
    }
}
