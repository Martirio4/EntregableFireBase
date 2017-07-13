package com.martirio.firebase_entregable.Modelo;

/**
 * Created by elmar on 11/7/2017.
 */

public class Paint {
    private String name;
    private String image;

    public Paint() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        Paint unaPintura=(Paint) obj;
        if (this.name==unaPintura.getName()){
            return true;
        }
        else{
            return false;
        }
    }
}
