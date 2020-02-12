package com.appdate.agendamento.model;

import java.io.Serializable;
import java.util.Date;

public class Agendamento implements Serializable {


    private String nomeCliente;
    private String procedimento;
    private Double valor;
    private Long dataEHora;

    public Agendamento(){

    }
    public Agendamento(String nomeCliente, String procedimento, Double valor, Long dataEHora) {
        this.nomeCliente = nomeCliente;
        this.procedimento = procedimento;
        this.valor = valor;
        this.dataEHora = dataEHora;
    }



    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getProcedimento() {
        return procedimento;
    }

    public void setProcedimento(String procedimento) {
        this.procedimento = procedimento;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Long getDataEHora() {
        return dataEHora;
    }

    public void setDataEHora(Long dataEHora) {
        this.dataEHora = dataEHora;
    }
}
