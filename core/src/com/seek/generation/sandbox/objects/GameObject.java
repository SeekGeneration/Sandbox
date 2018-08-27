package com.seek.generation.sandbox.objects;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btShapeHull;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.softbody.btSoftBody;
import com.badlogic.gdx.utils.BufferUtils;
import com.seek.generation.sandbox.ModelList;
import com.seek.generation.sandbox.physics.ObjectMotionState;
import com.seek.generation.sandbox.physics.PhysicsObject;
import com.seek.generation.sandbox.physics.PhysicsWorld;

import java.nio.ShortBuffer;

public abstract class GameObject extends ModelInstance {

    private PhysicsObject physicsObject = null;
    private ModelList name = ModelList.NULL;
    private Vector3 inertia = new Vector3();

    private final Vector3 center = new Vector3();
    private final Vector3 dimensions = new Vector3();
    private final BoundingBox bounds = new BoundingBox();

    public GameObject(Model model) {
        super(model);
        calculateBoundingBox(bounds);
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
    }

    public void setupPhysicsBody(PhysicsWorld world, PhysicsObject physicsObject, PhysicsObject.Type type) {
        this.physicsObject = physicsObject;
        if(type == PhysicsObject.Type.RIGIDBODY) {
            world.addRigidBody((btRigidBody) physicsObject.getBody());
        }else if(type == PhysicsObject.Type.SOFTBODY){
            world.addSoftBody((btSoftBody)physicsObject.getBody());
        }
    }

    public void setName(ModelList name) {
        this.name = name;
    }

    public ModelList getName() {
        return name;
    }

    public void translate(Vector3 position) {
        transform.setToTranslation(position);
        if (physicsObject != null) {
            physicsObject.getBody().setWorldTransform(transform);
        }
    }

    public void rotate(Vector3 rotation) {
        transform.rotate(Vector3.X, rotation.x);
        transform.rotate(Vector3.Y, rotation.y);
        transform.rotate(Vector3.Z, rotation.z);
    }

    //    public abstract void setAsPhysicsObject(PhysicsWorld physicsWorld);
    public void createAABB(PhysicsWorld physicsWorld, float mass, float friction, float restitution) {
        btBoxShape shape = new btBoxShape(model.meshParts.get(0).halfExtents);

        inertia.set(0, 0, 0);
        if (mass > 0f) {
            shape.calculateLocalInertia(mass, inertia);
        }

        ObjectMotionState motionState = new ObjectMotionState();
        motionState.transform = transform;

        btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, inertia);
        //bouncyness
        constructionInfo.setRestitution(restitution);
        constructionInfo.setFriction(friction);
        PhysicsObject physicsObject = new PhysicsObject(constructionInfo);

        setupPhysicsBody(physicsWorld, physicsObject, PhysicsObject.Type.RIGIDBODY);
    }

    public void createCone(PhysicsWorld physicsWorld, float radius, float height, float mass, float friction, float restituion) {
        btConeShape shape = new btConeShape(radius, height);

        inertia.set(0, 0, 0);
        if (mass > 0f) {
            shape.calculateLocalInertia(mass, inertia);
        }

        ObjectMotionState motionState = new ObjectMotionState();
        motionState.transform = transform;

        btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, inertia);

        constructionInfo.setRestitution(restituion);
        constructionInfo.setFriction(friction);
        PhysicsObject physicsObject = new PhysicsObject(constructionInfo);

        setupPhysicsBody(physicsWorld, physicsObject, PhysicsObject.Type.RIGIDBODY);

    }

    public void createCylinder(PhysicsWorld physicsWorld, Vector3 halfExtents, float mass, float friction, float restitution) {
        btCylinderShape shape = new btCylinderShape(halfExtents);

        inertia.set(0, 0, 0);
        if (mass > 0f) {
            shape.calculateLocalInertia(mass, inertia);
        }

        ObjectMotionState motionState = new ObjectMotionState();
        motionState.transform = transform;

        btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, inertia);
        //bouncyness
        constructionInfo.setRestitution(restitution);
        constructionInfo.setFriction(friction);
        PhysicsObject physicsObject = new PhysicsObject(constructionInfo);

        setupPhysicsBody(physicsWorld, physicsObject, PhysicsObject.Type.RIGIDBODY);
    }

    public void createConvexHull(PhysicsWorld physicsWorld, float mass, float friction, float restitution) {
        final Mesh mesh = model.meshes.get(0);
        final btConvexHullShape tmpShape = new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(), mesh.getVertexSize());

        final btShapeHull hull = new btShapeHull(tmpShape);
        hull.buildHull(tmpShape.getMargin());
        final btConvexHullShape shape = new btConvexHullShape(hull);

        tmpShape.dispose();
        hull.dispose();

        inertia.set(0, 0, 0);
        if (mass > 0f) {
            shape.calculateLocalInertia(mass, inertia);
        }

        ObjectMotionState motionState = new ObjectMotionState();
        motionState.transform = transform;

        btRigidBody.btRigidBodyConstructionInfo constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, motionState, shape, inertia);
        constructionInfo.setRestitution(restitution);
        constructionInfo.setFriction(friction);

        PhysicsObject physicsObject = new PhysicsObject(constructionInfo);
        setupPhysicsBody(physicsWorld, physicsObject, PhysicsObject.Type.RIGIDBODY);

    }

    public void createSoftBody(PhysicsWorld physicsWorld, Model objectModel){
        System.out.println(objectModel.nodes.get(0).parts.size);
        MeshPart meshPart = objectModel.nodes.get(0).parts.get(0).meshPart;
        meshPart.mesh.scale(6, 6, 6);

        ShortBuffer indexMap = BufferUtils.newShortBuffer(meshPart.size);

        int positionOffset = meshPart.mesh.getVertexAttribute(VertexAttributes.Usage.Position).offset;
        int normalOffset = meshPart.mesh.getVertexAttribute(VertexAttributes.Usage.Normal).offset;

        btSoftBody softBody = new btSoftBody(physicsWorld.getWorldInfo(), meshPart.mesh.getVerticesBuffer(), meshPart.mesh.getVertexSize(), positionOffset, normalOffset, meshPart.mesh.getIndicesBuffer(), meshPart.offset, meshPart.size, indexMap, 0);

        softBody.setMass(0, 0);

        btSoftBody.Material pm = softBody.appendMaterial();
        pm.setKLST(0.2f);
        pm.setFlags(0);
        softBody.generateBendingConstraints(2, pm);

        softBody.setConfig_piterations(7);
        softBody.setConfig_kDF(0.2f);
        softBody.randomizeConstraints();
        softBody.setTotalMass(1);

//        physicsWorld.addSoftBody(softBody);

        PhysicsObject physicsObject = new PhysicsObject(softBody);
        setupPhysicsBody(physicsWorld, physicsObject, PhysicsObject.Type.SOFTBODY);
//        PhysicsBody body = new PhysicsBody(softBody);
//        setupPhysicsBody(physicsWorld, body);
    }


    public PhysicsObject getPhysicsObject() {
        return physicsObject;
    }

    public Vector3 getCenter() {
        return center;
    }

    public Vector3 getDimensions() {
        return dimensions;
    }

    public void dispose() {
        if (physicsObject != null) {
            physicsObject.dispose();
        }
    }
}
