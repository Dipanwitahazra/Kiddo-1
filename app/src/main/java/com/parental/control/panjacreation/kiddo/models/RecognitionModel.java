package com.parental.control.panjacreation.kiddo.models;

import com.parental.control.panjacreation.kiddo.tflite.SimilarityClassifier;

public class RecognitionModel {
    private final String name;
    private final SimilarityClassifier.Recognition rec;
    private final boolean isParent;

    public RecognitionModel(String name, boolean isParent, SimilarityClassifier.Recognition rec) {
        this.name = name;
        this.rec = rec;
        this.isParent = isParent;
    }

    public String getName() {
        return name;
    }

    public SimilarityClassifier.Recognition getRec() {
        return rec;
    }

    public boolean isParent() {
        return isParent;
    }
}
