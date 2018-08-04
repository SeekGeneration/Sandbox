package com.seek.generation.sandbox.objects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.seek.generation.sandbox.objects.GameObject;
import com.seek.generation.sandbox.physics.ObjectMotionState;
import com.seek.generation.sandbox.physics.PhysicsBody;
import com.seek.generation.sandbox.physics.PhysicsWorld;

public class FloorObject extends GameObject {
    public FloorObject(Model model) {
        super(model);
    }

    public void setAsPhysicsObject(PhysicsWorld physicsWorld)
    {
        Vector3 inertia = new Vector3();
        float mass = 0f;
        btBoxShape shape = new btBoxShape(model.meshParts.get(0).halfExtents);

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

}
