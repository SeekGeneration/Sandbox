package com.seek.generation.sandbox.objects;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btShapeHull;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.seek.generation.sandbox.ModelList;
import com.seek.generation.sandbox.physics.ObjectMotionState;
import com.seek.generation.sandbox.physics.PhysicsBody;
import com.seek.generation.sandbox.physics.PhysicsWorld;

public abstract class GameObject extends ModelInstance{

    private PhysicsBody body = null;
    private ModelList name = ModelList.NULL;
    private Vector3 inertia = new Vector3();

    public GameObject(Model model) {
        super(model);
    }

    public void setupPhysicsBody(PhysicsWorld world, PhysicsBody body){
        this.body = body;
        world.addRigidBody(body);
    }

    public void setName(ModelList name){
        this.name = name;
    }

    public ModelList getName()
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
    public void createAABB(PhysicsWorld physicsWorld, float mass, float friction, float restitution){
        btBoxShape shape = new btBoxShape(model.meshParts.get(0).halfExtents);

        inertia.set(0 ,0 ,0);
        if(mass > 0f){
            shape.calculateLocalInertia(mass, inertia);
        }

        ObjectMotionState motionState = new ObjectMotionState();
        motionState.transform = transform;

        btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, inertia);
        //bouncyness
        constructionInfo.setRestitution(restitution);
        constructionInfo.setFriction(friction);
        PhysicsBody body = new PhysicsBody(constructionInfo);

        setupPhysicsBody(physicsWorld, body);
    }

    public void createCone(PhysicsWorld physicsWorld, float radius, float height, float mass, float friction, float restituion){
        btConeShape shape = new btConeShape(radius, height);

        inertia.set(0, 0, 0);
        if(mass > 0f){
            shape.calculateLocalInertia(mass, inertia);
        }

        ObjectMotionState motionState = new ObjectMotionState();
        motionState.transform = transform;

        btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, inertia);

        constructionInfo.setRestitution(restituion);
        constructionInfo.setFriction(friction);
        PhysicsBody body = new PhysicsBody(constructionInfo);

        setupPhysicsBody(physicsWorld, body);

    }

    public void createConvexHull(PhysicsWorld physicsWorld, float mass, float friction, float restitution){
        final Mesh mesh = model.meshes.get(0);
        final btConvexHullShape tmpShape = new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(), mesh.getVertexSize());

        final btShapeHull hull = new btShapeHull(tmpShape);
        hull.buildHull(tmpShape.getMargin());
        final btConvexHullShape shape = new btConvexHullShape(hull);

        tmpShape.dispose();
        hull.dispose();

        inertia.set(0, 0, 0);
        if(mass > 0f){
            shape.calculateLocalInertia(mass, inertia);

            ObjectMotionState motionState = new ObjectMotionState();
            motionState.transform = transform;

            btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, inertia);
            constructionInfo.setRestitution(restitution);
            constructionInfo.setFriction(friction);

            PhysicsBody body = new PhysicsBody(constructionInfo);
            setupPhysicsBody(physicsWorld, body);
        }
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
