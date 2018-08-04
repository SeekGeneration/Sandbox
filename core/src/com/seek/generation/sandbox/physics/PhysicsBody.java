package com.seek.generation.sandbox.physics;


import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class PhysicsBody extends btRigidBody {

    private btCollisionShape shape;
    private btRigidBodyConstructionInfo constructionInfo;

    public PhysicsBody(btRigidBodyConstructionInfo constructionInfo) {
        super(constructionInfo);
        this.shape = constructionInfo.getCollisionShape();
        this.constructionInfo = constructionInfo;
    }

    public void dispose()
    {
        super.dispose();
        shape.dispose();
        constructionInfo.dispose();
    }
}
