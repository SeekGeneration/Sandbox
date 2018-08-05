package com.seek.generation.sandbox.objects;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.seek.generation.sandbox.physics.ObjectMotionState;
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

//    public abstract void setAsPhysicsObject(PhysicsWorld physicsWorld);
    public void createAABB(PhysicsWorld physicsWorld, float mass){
        Vector3 inertia = new Vector3();
        inertia.set(1, 1, 1);
        btBoxShape shape = new btBoxShape(inertia);

        inertia.set(0 ,0 ,0);
        if(mass > 0f){
            shape.calculateLocalInertia(mass, inertia);
        }

        ObjectMotionState motionState = new ObjectMotionState();
        motionState.transform = transform;

        btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, inertia);
        //bouncyness
        constructionInfo.setRestitution(0f);
        PhysicsBody body = new PhysicsBody(constructionInfo);

        setupPhysicsBody(physicsWorld, body);
    }
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
