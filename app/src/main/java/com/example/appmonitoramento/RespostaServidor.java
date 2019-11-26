package com.example.appmonitoramento;

public class RespostaServidor {

    private int idUsuario;

    private String data;
    private String idBotijao;
    private int idMonitoramento;
    private int idSensor;
    private String valor;


    public RespostaServidor(){}

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getData() {
        return data;
    }

    public String getIdBotijao() {
        return idBotijao;
    }

    public int getIdMonitoramento() {
        return idMonitoramento;
    }

    public int getIdSensor() {
        return idSensor;
    }

    public String getValor() {
        return valor;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setIdBotijao(String idBotijão) {
        this.idBotijao = idBotijão;
    }

    public void setIdMonitoramento(int idMonitoramento) {
        this.idMonitoramento = idMonitoramento;
    }

    public void setIdSensor(int idSensor) {
        this.idSensor = idSensor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}