package com.seek.generation.sandbox.physics;


import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.softbody.btSoftBody;

public class PhysicsObject {

    private Type type;

    //rigid body;
    private btRigidBody rigidBody;
    private btCollisionShape shape;
    private btRigidBody.btRigidBodyConstructionInfo constructionInfo;

    //soft body
    private btSoftBody softBody;

    public PhysicsObject(btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
        type = Type.RIGIDBODY;
        this.rigidBody = new btRigidBody(constructionInfo);
        this.shape = constructionInfo.getCollisionShape();
        this.constructionInfo = constructionInfo;
    }

    public PhysicsObject(btSoftBody softBody){
        type = Type.SOFTBODY;
        this.softBody = softBody;
    }

    public btCollisionObject getBody(){
        if(type == Type.RIGIDBODY){
            return rigidBody;
        }else if(type == Type.SOFTBODY){
            return softBody;
        }else{
            return null;
        }
    }

    public void dispose()
    {
        if(type == Type.RIGIDBODY){
            rigidBody.dispose();
            constructionInfo.dispose();
            shape.dispose();
        }else if(type == Type.SOFTBODY){
            softBody.dispose();
        }
    }

    public enum Type{
        RIGIDBODY, SOFTBODY
    }
}
