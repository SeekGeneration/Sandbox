package com.seek.generation.sandbox;

public enum ModelList {

    NULL("null"),
    MODEL_FLOOR("models/floor/floor.g3db"),
    MODEL_BOX("models/box/box.obj"),
    MODEL_RUST_CUBE("models/rustCube/rustCube.g3db"),
    MODEL_TORUS_KNOT("models/torus/knot/torus_knot.obj"),
    MODEL_CONE("models/cone/cone.obj");

    String model;

    ModelList(String model) {
        this.model = model;
    }


    public String get()
    {
        return model;
    }
}
