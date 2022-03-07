package com.example.psmart;

import java.io.Serializable;

public class FaceData implements Serializable {
    private String name;
    private SimilarityClassifier.Recognition face;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SimilarityClassifier.Recognition getFace() {
        return face;
    }

    public String setFace(SimilarityClassifier.Recognition face) {
        this.face = face;



        return null;
    }


}
