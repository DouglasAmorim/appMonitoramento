package com.example.appmonitoramento;

public class RespostaServidor {

    private String value;
    private String date;
    private String idCylinder;
    private String idMonitoramento;
    private String idSensor;

    public RespostaServidor(){}

    public String getValue() {
        return value;
    }

    public String getDate() {
        return date;
    }

    public String getIdCylinder() {
        return idCylinder;
    }

    public void setIdMonitoramento(String idMonitoramento) {
        this.idMonitoramento = idMonitoramento;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setIdCylinder(String idCylinder) {
        this.idCylinder = idCylinder;
    }

    public void setIdSensor(String idSensor) {
        this.idSensor = idSensor;
    }

    public String getIdMonitoramento() {
        return idMonitoramento;
    }

    public String getIdSensor() {
        return idSensor;
    }
}