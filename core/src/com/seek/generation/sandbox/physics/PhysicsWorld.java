package com.seek.generation.sandbox.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btActionInterface;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;

public class PhysicsWorld {

    private final btDynamicsWorld dynamicsWorld;
    private final btCollisionConfiguration collisionConfiguration;
    private final btConstraintSolver constrainSolver;
    private final btBroadphaseInterface broadphase;
    private final btDispatcher dispatcher;

    private final Vector3 tmp = new Vector3();

    private final DebugDrawer debugDrawer;

    public PhysicsWorld(){
        Bullet.init();

        collisionConfiguration = new btDefaultCollisionConfiguration();
        constrainSolver = new btSequentialImpulseConstraintSolver();
        broadphase = new btDbvtBroadphase();
        dispatcher = new btCollisionDispatcher(collisionConfiguration);
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constrainSolver, collisionConfiguration);

        debugDrawer = new DebugDrawer();
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
        dynamicsWorld.setDebugDrawer(debugDrawer);
    }

    public void debugDraw(Camera camera){
        debugDrawer.begin(camera);
        dynamicsWorld.debugDrawWorld();
        debugDrawer.end();
    }

    public void step(float timeStep, int maxSubSteps, float fixedTimeStep){
        dynamicsWorld.stepSimulation(timeStep, maxSubSteps, fixedTimeStep);
    }

    public void step()
    {
        final float timeStep = Math.min(1f/30f, Gdx.graphics.getDeltaTime());
        step(timeStep, 5, 1f/60f);
    }

    public void addRigidBody(btRigidBody rigidBody){
        dynamicsWorld.addRigidBody(rigidBody);
    }

    public void addRigidBody(btRigidBody rigidBody, short group, short mask){
        dynamicsWorld.addRigidBody(rigidBody, group, mask);
    }

    public void addCollisionObject(btCollisionObject collisionObject){
        dynamicsWorld.addCollisionObject(collisionObject);
    }

    public void addCollisionObject(btCollisionObject collisionObject, short groups, short mask) {
        dynamicsWorld.addCollisionObject(collisionObject, groups, mask);
    }

    public void addAction(btActionInterface action){
        dynamicsWorld.addAction(action);
    }

    public void setGravity(float x, float y, float z){
        tmp.set(x, y, z);
        dynamicsWorld.setGravity(tmp);
    }

    public void dispose()
    {
        dynamicsWorld.dispose();
        collisionConfiguration.dispose();
        constrainSolver.dispose();
        broadphase.dispose();
        dispatcher.dispose();
    }
}
