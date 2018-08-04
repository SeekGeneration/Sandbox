package com.seek.generation.sandbox.objects;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.seek.generation.sandbox.physics.PhysicsBody;
import com.seek.generation.sandbox.physics.PhysicsWorld;

public abstract class GameObject extends ModelInstance{

    private PhysicsBody body = null;
    private String name = "";

    public GameObject(Model model) {
        super(model);
    }

    public void setupPhysicsBody(PhysicsWorld world, PhysicsBody body){
        this.body = body;
        world.addRigidBody(body);
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void translate(Vector3 position){
            transform.setToTranslation(position);
        if(body != null) {
            body.setWorldTransform(transform);
        }
    }

    public void rotate(Vector3 rotation){
        transform.rotate(Vector3.X, rotation.x);
        transform.rotate(Vector3.Y, rotation.y);
        transform.rotate(Vector3.Z, rotation.z);
    }

    public abstract void setAsPhysicsObject(PhysicsWorld physicsWorld);
    public PhysicsBody getBody() {
         return body;
    }

    public void dispose()
    {
        if(body != null){
            body.dispose();
        }
    }
}
