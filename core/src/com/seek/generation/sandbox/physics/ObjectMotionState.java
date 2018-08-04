package com.seek.generation.sandbox.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

public class ObjectMotionState extends btMotionState {

    public Matrix4 transform;

    @Override
    public void getWorldTransform(Matrix4 worldTrans){
        worldTrans.set(transform);
    }

    public void setWorldTransform(Matrix4 worldTrans){
        transform.set(worldTrans);
    }
}
