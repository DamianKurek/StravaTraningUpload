package com.example.rodzina.stravatraning2;

import java.util.Date;

/**
 * Created by Rodzina on 16.11.2016.
 */

public class Trening {
    private int id;
    private Date data;
    private String nazwa;
    private int czas;
    private String opis;

    public String getNazwa() {
        return nazwa;
    }

    public Date getData() {
        return data;
    }

    public int getCzas() {
        return czas;
    }

    public String getOpis() {
        return opis;
    }

    public Trening(int id,Date data, String nazwa, int czas, String opis) {
        this.id = id;
        this.data = data;
        this.nazwa = nazwa;
        this.czas = czas;
        this.opis = opis;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public void setCzas(int czas) {
        this.czas = czas;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
