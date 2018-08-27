package com.seek.generation.sandbox.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
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
import com.badlogic.gdx.physics.bullet.softbody.btSoftBody;
import com.badlogic.gdx.physics.bullet.softbody.btSoftBodyWorldInfo;
import com.badlogic.gdx.physics.bullet.softbody.btSoftRigidDynamicsWorld;

public class PhysicsWorld {

    private final btSoftRigidDynamicsWorld dynamicsWorld;
    private final btCollisionConfiguration collisionConfiguration;
    private final btConstraintSolver constrainSolver;
    private final btBroadphaseInterface broadphase;
    private final btDispatcher dispatcher;

    private btSoftBodyWorldInfo worldInfo;

    private final Vector3 tmp = new Vector3();

    private final DebugDrawer debugDrawer;

    public ClosestRayResultCallback rayResultCallback;
    public Vector3 rayFrom = new Vector3();
    public Vector3 rayTo = new Vector3();

    private final short DEFAULT_FLAG = -1;
    private final short DEFAULT_GROUP = DEFAULT_FLAG;
    private final short DEFAULT_MASK = DEFAULT_FLAG;

    public PhysicsWorld() {
        Bullet.init();

        collisionConfiguration = new btDefaultCollisionConfiguration();
        constrainSolver = new btSequentialImpulseConstraintSolver();
        broadphase = new btDbvtBroadphase();
        dispatcher = new btCollisionDispatcher(collisionConfiguration);
//        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constrainSolver, collisionConfiguration);

        dynamicsWorld = new btSoftRigidDynamicsWorld(dispatcher, broadphase, constrainSolver, collisionConfiguration);

        worldInfo = new btSoftBodyWorldInfo();
        worldInfo.setBroadphase(broadphase);
        worldInfo.setDispatcher(dispatcher);
        worldInfo.getSparsesdf().Initialize();

        debugDrawer = new DebugDrawer();
        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
        dynamicsWorld.setDebugDrawer(debugDrawer);

        rayResultCallback = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
    }

    public btCollisionObject rayTest(Camera camera) {
        Ray ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        rayFrom.set(ray.origin);
        rayTo.set(ray.direction).scl(50f).add(rayFrom);

        rayResultCallback.setCollisionObject(null);
        rayResultCallback.setClosestHitFraction(1f);
        rayResultCallback.setRayFromWorld(rayFrom);
        rayResultCallback.setRayToWorld(rayTo);

        dynamicsWorld.rayTest(rayFrom, rayTo, rayResultCallback);

        if (rayResultCallback.hasHit()) {
            final btCollisionObject obj = rayResultCallback.getCollisionObject();

            return obj;
        }

        return null;
    }

    public void debugDraw(Camera camera) {
        debugDrawer.begin(camera);
        dynamicsWorld.debugDrawWorld();
        debugDrawer.end();
    }

    public void step(float timeStep, int maxSubSteps, float fixedTimeStep) {
        dynamicsWorld.stepSimulation(timeStep, maxSubSteps, fixedTimeStep);
    }

    public void step() {
        final float timeStep = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());
        step(timeStep, 5, 1f / 60f);
    }

    public void addSoftBody(btSoftBody softBody){
        dynamicsWorld.addSoftBody(softBody, DEFAULT_GROUP, DEFAULT_MASK);
    }

    public void addSoftBody(btSoftBody softBody, short group, short mask){
        dynamicsWorld.addSoftBody(softBody, group, mask);
    }

    public void addRigidBody(btRigidBody rigidBody) {
        dynamicsWorld.addRigidBody(rigidBody, DEFAULT_GROUP, DEFAULT_MASK);
    }

    public void addRigidBody(btRigidBody rigidBody, short group, short mask) {
        dynamicsWorld.addRigidBody(rigidBody, group, mask);
    }

    public void addCollisionObject(btCollisionObject collisionObject) {
        dynamicsWorld.addCollisionObject(collisionObject);
    }

    public void addCollisionObject(btCollisionObject collisionObject, short groups, short mask) {
        dynamicsWorld.addCollisionObject(collisionObject, groups, mask);
    }

    public void addAction(btActionInterface action) {
        dynamicsWorld.addAction(action);
    }

    public void setGravity(float x, float y, float z) {
        tmp.set(x, y, z);
        dynamicsWorld.setGravity(tmp);
    }

    public btSoftBodyWorldInfo getWorldInfo()
    {
        return worldInfo;
    }

    public void dispose() {
        dynamicsWorld.dispose();
        worldInfo.dispose();
        collisionConfiguration.dispose();
        constrainSolver.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        rayResultCallback.dispose();
    }
}
