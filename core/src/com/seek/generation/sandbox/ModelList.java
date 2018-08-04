package com.seek.generation.sandbox;

public enum ModelList {

    MODEL_FLOOR("models/floor/floor.g3db"),
    MODEL_BOX("models/box/box.obj"),
    MODEL_RUST_CUBE("models/rustCube/rustCube.g3db");

    String model;

    ModelList(String model) {
        this.model = model;
    }


    public String get()
    {
        return model;
    }
}
