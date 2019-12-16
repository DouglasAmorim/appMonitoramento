package com.example.appmonitoramento;

public class RespostaServidor {

    private String weight;
    private String date;
    private String score;

    public RespostaServidor(){}

    public String getWeight() {
        return weight;
    }

    public String getScore() {
        return score;
    }

    public String getDate() {
        return date;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setDate(String date) {
        this.date = date;
    }
}