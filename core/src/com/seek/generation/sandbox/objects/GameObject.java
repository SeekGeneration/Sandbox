package com.seek.generation.sandbox.objects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.seek.generation.sandbox.physics.PhysicsBody;
import com.seek.generation.sandbox.physics.PhysicsWorld;

public abstract class GameObject extends ModelInstance{

    private PhysicsBody body = null;

    public GameObject(Model model) {
        super(model);
    }

    public void setupPhysicsBody(PhysicsWorld world, PhysicsBody body){
        this.body = body;
        world.addRigidBody(body);
    }

    public void dispose()
    {
        if(body != null){
            body.dispose();
        }
    }
}
