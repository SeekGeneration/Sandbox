package com.seek.generation.sandbox;

public enum ModelList {

    NULL("null"),
    SKYBOX("models/skybox/skybox.g3db", false),
    MODEL_FLOOR("models/floor/floor.g3db"),
    MODEL_BOX("models/box/box.obj"),
    MODEL_RUST_CUBE("models/rustCube/rustCube.g3db"),
    MODEL_TORUS_KNOT("models/torus/knot/torus_knot.obj"),
    MODEL_CONE("models/cone/cone.obj"),
    MODEL_CYLINDER("models/cylinder/cylinder.obj"),
    MODEL_STAIRS("models/stairs/stairs.obj"),
    //TODO fix convex hull for model blades or create an model from scratch
    MODEL_TURBINE_BLADES("models/turbine/blades.g3db"),
    MODEL_ICO("models/ico.g3db");

    String model;
    boolean isPlaceable;

    ModelList(String model) {
        this(model, true);
    }

    ModelList(String model, boolean isPlaceable){
        this.model = model;
        this.isPlaceable = isPlaceable;
    }


    public String get()
    {
        return model;
    }

    public boolean isPlaceable() {
        return isPlaceable;
    }
}
