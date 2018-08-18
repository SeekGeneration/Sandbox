package com.seek.generation.sandbox;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;

public enum ModelList {

    NULL("null"),
    SKYBOX("models/skybox/skybox.g3db", true),
    MODEL_FLOOR("models/floor/floor.g3db"),
    MODEL_BOX("models/box/box.obj"),
    MODEL_RUST_CUBE("models/rustCube/rustCube.g3db"),
    MODEL_TORUS_KNOT("models/torus/knot/torus_knot.obj"),
    MODEL_CONE("models/cone/cone.obj"),
    MODEL_CYLINDER("models/cylinder/cylinder.obj"),
    MODEL_STAIRS("models/stairs/stairs.obj"),

    String model;
    //place holder for skybox until another alternative is found
    boolean fixSeams = false;

    ModelList(String model) {
        this.model = model;
    }

    ModelList(String model, boolean fixSeams){
        this.model = model;
        this.fixSeams = fixSeams;
    }


    public String get()
    {
        return model;
    }

    public boolean getFixSeams() {
        return fixSeams;
    }
}
