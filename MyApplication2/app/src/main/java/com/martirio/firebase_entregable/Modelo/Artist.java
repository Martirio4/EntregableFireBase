package com.martirio.firebase_entregable.Modelo;

import java.util.List;

/**
 * Created by elmar on 11/7/2017.
 */

public class Artist {

    private String name;
    private List<Paint> paints;

    public Artist() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Paint> getPaints() {
        return paints;
    }

    public void setPaints(List<Paint> paints) {
        this.paints = paints;
    }

    @Override
    public String toString() {
        return name+" "+paints;
    }


}
