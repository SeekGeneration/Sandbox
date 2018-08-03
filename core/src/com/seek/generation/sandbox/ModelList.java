package com.seek.generation.sandbox;

public enum ModelList {

    MODEL_BOX("models/box/box.obj");

    String model;

    ModelList(String model) {
        this.model = model;
    }


    public String get()
    {
        return model;
    }
}
