package com.seek.generation.sandbox;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.seek.generation.sandbox.objects.BoxObject;
import com.seek.generation.sandbox.objects.ConeObject;
import com.seek.generation.sandbox.objects.CylinderObject;
import com.seek.generation.sandbox.objects.FloorObject;
import com.seek.generation.sandbox.objects.GameObject;
import com.seek.generation.sandbox.objects.IcoSphereObject;
import com.seek.generation.sandbox.objects.RustCube;
import com.seek.generation.sandbox.objects.StairsObject;
import com.seek.generation.sandbox.objects.TorusKnot;
import com.seek.generation.sandbox.objects.TurbineBladesObject;
import com.seek.generation.sandbox.physics.PhysicsWorld;
import com.seek.generation.sandbox.ui.ModelListView;

import java.util.HashMap;
import java.util.Map;

public class Sandbox extends ApplicationAdapter implements InputProcessor {

    //graphics
    private PerspectiveCamera camera;
    private CameraController cameraController;
    private ModelBatch batch;
    private AssetManager assetManager;
    private Environment environment;

    private Array<GameObject> instances = new Array<GameObject>();
    //used as a place holder for selected objects
    private HashMap<ModelList, GameObject> selectedObjects = new HashMap<ModelList, GameObject>();
    private FloorObject floorInstance = null;
    private Skybox skybox = null;

    private HashMap<ModelList, Boolean> modelQue = new HashMap<ModelList, Boolean>();
    private HashMap<ModelList, Boolean> modelQueToRemove = new HashMap<ModelList, Boolean>();
    private HashMap<ModelList, Model> loadedModels = new HashMap<ModelList, Model>();

    private GameObject selectedObject = null;
    private Vector3 vector3 = new Vector3();
    private float selectedObjectZoom = 1f;

    //physics
    private PhysicsWorld physicsWorld;

    //ui
    private Stage stage;
    private Table rootTable;
    private ModelListView modelListView;
    private VisProgressBar loadingBar;

    @Override
    public void create() {
        Gdx.gl.glClearColor(0.5f, 0.8f, 0.9f, 1);

        VisUI.load();
        camera = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraController = new CameraController(camera);
        batch = new ModelBatch();
        assetManager = new AssetManager();
        environment = new Environment();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        camera.near = 0.1f;
        camera.far = 3000f;
        camera.position.set(0, 5, 0);
        camera.update();
//        cameraController.setVelocity(50);

        assetManager.setLoader(Texture.class, new TextureLoaderDefault(new InternalFileHandleResolver()));

        physicsWorld = new PhysicsWorld();

//        loadModel(ModelList.SKYBOX);
//        loadModel(ModelList.MODEL_FLOOR);
//        loadModel(ModelList.MODEL_BOX);
//        loadModel(ModelList.MODEL_RUST_CUBE);
//        loadModel(ModelList.MODEL_TORUS_KNOT);
//        loadModel(ModelList.MODEL_CONE);
//        loadModel(ModelList.MODEL_CYLINDER);
//        loadModel(ModelList.MODEL_STAIRS);

        ModelList[] enums = ModelList.values();
        for(ModelList m : enums){
            if(m == ModelList.NULL){
                continue;
            }
            loadModel(m);
        }

        //setup UI
        stage = new Stage(Gdx.app.getType() == Application.ApplicationType.Desktop ? new ScreenViewport() : new FitViewport(1920 / 3, 1080 / 3));
        rootTable = new Table();
        modelListView = new ModelListView();
        loadingBar = new VisProgressBar(0, 1f, 0.1f, false);

        rootTable.add(modelListView).grow().align(Align.left);
        rootTable.add(loadingBar).align(Align.bottomRight);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);
//        stage.setDebugAll(true);

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, cameraController, this));
    }

    private void loadModel(ModelList model) {
        modelQue.put(model, false);
    }

    private Model getModel(ModelList model) {
        return loadedModels.get(model);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        stage.getViewport().update(width, height, true);
    }

    private void handleLoading() {
        //checks if value is false, if false then asset manager needs to load the model if true then
        //the asset manager has already started loading the model and if true we will check if it
        //has finished loading then if isLoaded returns true we add it to the loaded models array
        for (Map.Entry<ModelList, Boolean> entry : modelQue.entrySet()) {
            if (entry.getValue() == false) {
                assetManager.load(entry.getKey().get(), Model.class);
                entry.setValue(true);
            } else {
                if (assetManager.isLoaded(entry.getKey().get())) {
                    Model model = assetManager.get(entry.getKey().get(), Model.class);

                    loadedModels.put(entry.getKey(), model);
                    modelListView.update(loadedModels, instances, physicsWorld, camera);
                    modelQueToRemove.put(entry.getKey(), entry.getValue());
                }
            }
        }

        for (Map.Entry<ModelList, Boolean> entry : modelQueToRemove.entrySet()) {
            if (modelQue.get(entry.getKey())) {
                modelQue.remove(entry.getKey());
            }
        }
        modelQueToRemove.clear();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
        }

//        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
//            btCollisionObject obj = physicsWorld.rayTest(camera);
//            for (GameObject go : instances) {
//                if (go.getPhysicsObject().getBody().equals(obj)) {
//                    go.getPhysicsObject().getBody().activate();
//                    go.getPhysicsObject().getBody().applyCentralForce(new Vector3(camera.direction).scl(70f));
//                    break;
//                }
//            }
//        }
//        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
//            Model model = getModel(ModelList.MODEL_BOX);
//            if (model != null) {
//                GameObject instance = new BoxObject(model);
//                instances.add(instance);
//
//                instance.createAABB(physicsWorld, 1f);
//            }
//        }
//
//        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
//            Model model = getModel(ModelList.MODEL_RUST_CUBE);
//            if (model != null) {
//                GameObject instance = new RustCube(model);
//                instance.transform.setToTranslation(-5, 25, -5);
//                instance.transform.rotate(Vector3.X, 25);
//                instance.transform.rotate(Vector3.Y, 25);
//                instances.add(instance);
//
//                instance.createAABB(physicsWorld, 1f);
//            }
//        }
    }

    /**
     * checks the GUI which button is selected and sets the selectedObject instance
     */
    private void handleSelectedObject() {
//        String selected = modelListView.getSelected();
        ModelList selected = modelListView.getSelected();
        if (selected == ModelList.NULL) {
            selectedObject = null;
        } else if (selected == ModelList.MODEL_BOX) {
            GameObject object = selectedObjects.get(selected);
            if (object != null) {
                selectedObject = object;
            } else {
                BoxObject obj = new BoxObject(getModel(ModelList.MODEL_BOX));
                setupPlaceHolder(obj, selected);
                selectedObject = obj;
            }
        } else if (selected == ModelList.MODEL_RUST_CUBE) {
            GameObject object = selectedObjects.get(selected);
            if (object != null) {
                selectedObject = object;
            } else {
                RustCube obj = new RustCube(getModel(ModelList.MODEL_RUST_CUBE));
                setupPlaceHolder(obj, selected);
                selectedObject = obj;
            }
        } else if (selected == ModelList.MODEL_FLOOR) {
            GameObject object = selectedObjects.get(selected);
            if (object != null) {
                selectedObject = object;
            } else {
                FloorObject obj = new FloorObject(getModel(ModelList.MODEL_FLOOR));
                setupPlaceHolder(obj, selected);
                selectedObject = obj;
            }
        } else if (selected == ModelList.MODEL_TORUS_KNOT) {
            GameObject object = selectedObjects.get(selected);
            if (object != null) {
                selectedObject = object;
            } else {
                TorusKnot obj = new TorusKnot(getModel(ModelList.MODEL_TORUS_KNOT));
                setupPlaceHolder(obj, selected);
                selectedObject = obj;
            }
        } else if (selected == ModelList.MODEL_CONE) {
            GameObject object = selectedObjects.get(selected);
            if (object != null) {
                selectedObject = object;
            } else {
                ConeObject obj = new ConeObject(getModel(ModelList.MODEL_CONE));
                setupPlaceHolder(obj, selected);
                selectedObject = obj;
            }
        } else if (selected == ModelList.MODEL_CYLINDER) {
            GameObject object = selectedObjects.get(selected);
            if (object != null) {
                selectedObject = object;
            } else {
                CylinderObject obj = new CylinderObject(getModel(ModelList.MODEL_CYLINDER));
                setupPlaceHolder(obj, selected);
                selectedObject = obj;
            }
        }else if(selected == ModelList.MODEL_STAIRS){
            GameObject object = selectedObjects.get(selected);
            if(object != null){
                selectedObject = object;
            }else{
                StairsObject obj = new StairsObject(getModel(ModelList.MODEL_STAIRS));
                setupPlaceHolder(obj, selected);
                selectedObject = obj;
            }
        }else if(selected == ModelList.MODEL_TURBINE_BLADES){
            GameObject object = selectedObjects.get(selected);
            if(object != null){
                selectedObject = object;
            }else{
                TurbineBladesObject obj = new TurbineBladesObject(getModel(ModelList.MODEL_TURBINE_BLADES));
                setupPlaceHolder(obj, selected);
                selectedObject = obj;
            }
        }else if(selected == ModelList.MODEL_ICO){
            GameObject object = selectedObjects.get(selected);
            if(object != null){
                selectedObject = object;
            }else{
                IcoSphereObject obj = new IcoSphereObject(getModel(ModelList.MODEL_ICO));
                setupPlaceHolder(obj, selected);
                selectedObject = obj;
            }
        }
    }

    private void setupPlaceHolder(GameObject obj, ModelList selected) {
        applyAlpha(obj);
        obj.setName(selected);
        selectedObjects.put(selected, obj);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        String selectedpos = selectedObject != null ? selectedObject.transform.getTranslation(vector3) + "" : "";
        Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond() + " Instances: " + instances.size + " Pos: " + camera.position + " selectedPos: " + selectedpos);

        loadingBar.setVisible(assetManager.getProgress() < 1f);
        loadingBar.setValue(assetManager.getProgress());

        if (!assetManager.update()) {
            //display a loading screen/bar
        }

        handleLoading();
        handleInput();
        handleSelectedObject();

        physicsWorld.step();

        // checks for the selected object and displays it in front of the player
        if (selectedObject != null) {
            float dist = 5f * selectedObjectZoom;
            vector3.set(camera.position.x + (camera.direction.x * dist), camera.position.y + (camera.direction.y * dist), camera.position.z + (camera.direction.z * dist));
            selectedObject.translate(vector3);
            selectedObject.transform.rotate(Vector3.X, modelListView.getModelRotation().x);
            selectedObject.transform.rotate(Vector3.Y, modelListView.getModelRotation().y);
            selectedObject.transform.rotate(Vector3.Z, modelListView.getModelRotation().z);
        }

        //create the skybox
        if(skybox == null){
            Model model = getModel(ModelList.SKYBOX);
            if(model != null){
                skybox = new Skybox(model);
//                instances.add(skybox);
            }
        }

        //creates a floor for the player to land on
        if (floorInstance == null) {
            Model model = getModel(ModelList.MODEL_FLOOR);
            if (model != null) {
                floorInstance = new FloorObject(model);
                instances.add(floorInstance);
                floorInstance.createAABB(physicsWorld, 0f, 0.5f, 0f);
            }
        }

        cameraController.update();

        batch.begin(camera);
        {
            for (int i = instances.size - 1; i >= 0; i--) {
                if(isVisible(instances.get(i))){
                    batch.render(instances.get(i), environment);
                }
            }

            if (selectedObject != null) {
                batch.render(selectedObject, environment);
            }

            if(skybox != null){
//                skybox.transform = camera.combined;
                skybox.transform.setToTranslation(camera.position);
                float scale = camera.far - (camera.far * 0.5f);
                skybox.transform.scale(scale, scale, scale);
                batch.render(skybox);
            }
        }
        batch.end();

        physicsWorld.debugDraw(camera);

        stage.act();
        stage.draw();

    }

    public boolean isVisible(GameObject gameObject){
        gameObject.transform.getTranslation(vector3);
        vector3.add(gameObject.getCenter());
        return camera.frustum.boundsInFrustum(vector3, gameObject.getDimensions());
    }

    //makes the object see through
    private void applyAlpha(GameObject gameObject) {
        Material material = gameObject.materials.get(0);
        ColorAttribute colorAttribute = new ColorAttribute(ColorAttribute.Diffuse, new Color(1, 1, 1, 0.7f));
        BlendingAttribute blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        material.set(colorAttribute);
        material.set(blendingAttribute);
    }

    @Override
    public void dispose() {
        physicsWorld.dispose();

        for (GameObject go : instances) {
            go.dispose();
        }

        stage.dispose();
        VisUI.dispose();
        batch.dispose();
        assetManager.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT && selectedObject != null) {
//            String select = selectedObject.getName();
            ModelList select = selectedObject.getName();

            GameObject object = null;
            if (select == ModelList.MODEL_BOX) {
                object = new BoxObject(getModel(ModelList.MODEL_BOX));
                object.createAABB(physicsWorld, modelListView.getMass(), modelListView.getFriction(), modelListView.getRestitution());
            } else if (select == ModelList.MODEL_RUST_CUBE) {
                object = new RustCube(getModel(ModelList.MODEL_RUST_CUBE));
                object.createAABB(physicsWorld, modelListView.getMass(), modelListView.getFriction(), modelListView.getRestitution());
            } else if (select == ModelList.MODEL_FLOOR) {
                object = new FloorObject(getModel(ModelList.MODEL_FLOOR));
                object.createAABB(physicsWorld, modelListView.getMass(), modelListView.getFriction(), modelListView.getRestitution());
            } else if (select == ModelList.MODEL_TORUS_KNOT) {
                object = new TorusKnot(getModel(ModelList.MODEL_TORUS_KNOT));
                object.createConvexHull(physicsWorld, modelListView.getMass(), modelListView.getFriction(), modelListView.getRestitution());
            } else if (select == ModelList.MODEL_CONE) {
                object = new ConeObject(getModel(ModelList.MODEL_CONE));
                object.createCone(physicsWorld, 1, 2, modelListView.getMass(), modelListView.getFriction(), modelListView.getRestitution());
            } else if (select == ModelList.MODEL_CYLINDER) {
                object = new CylinderObject(getModel(ModelList.MODEL_CYLINDER));
                object.createCylinder(physicsWorld, new Vector3(1, 1, 1), modelListView.getMass(), modelListView.getFriction(), modelListView.getRestitution());
            }else if(select == ModelList.MODEL_STAIRS){
                object = new StairsObject(getModel(ModelList.MODEL_STAIRS));
                object.createConvexHull(physicsWorld, modelListView.getMass(), modelListView.getFriction(), modelListView.getRestitution());
            }else if(select == ModelList.MODEL_TURBINE_BLADES){
                object = new TurbineBladesObject(getModel(ModelList.MODEL_TURBINE_BLADES));
                object.createAABB(physicsWorld, modelListView.getMass(), modelListView.getFriction(), modelListView.getRestitution());
//                object.createConvexHull(physicsWorld, modelListView.getMass(), modelListView.getFriction(), modelListView.getRestitution());
            }else if(select == ModelList.MODEL_ICO){
                object = new IcoSphereObject(getModel(ModelList.MODEL_ICO));
//                object.createSoftBody(physicsWorld, getModel(ModelList.MODEL_ICO));
//                object.createSphere(physicsWorld, modelListView.getMass(), modelListView.getFriction(), modelListView.getRestitution());
            }

            if (object != null) {
                if(object.getPhysicsObject() != null) {
                    object.getPhysicsObject().getBody().setWorldTransform(selectedObject.transform);
                }
                object.transform.set(selectedObject.transform);
                object.setName(selectedObject.getName());
                instances.add(object);
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        selectedObjectZoom += (amount * 0.1);
        if (selectedObjectZoom < 0.5f) {
            selectedObjectZoom = 0.5f;
        }
        if (selectedObjectZoom > 25f) {
            selectedObjectZoom = 25f;
        }
        return false;
    }
}
