package com.seek.generation.sandbox;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
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
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.seek.generation.sandbox.objects.BoxObject;
import com.seek.generation.sandbox.objects.FloorObject;
import com.seek.generation.sandbox.objects.GameObject;
import com.seek.generation.sandbox.objects.RustCube;
import com.seek.generation.sandbox.objects.TorusKnot;
import com.seek.generation.sandbox.physics.PhysicsWorld;
import com.seek.generation.sandbox.ui.ModelListView;

import java.util.HashMap;
import java.util.Map;

public class Sandbox extends ApplicationAdapter implements InputProcessor {

    //graphics
    private PerspectiveCamera camera;
    private FirstPersonCameraController cameraController;
    private ModelBatch batch;
    private AssetManager assetManager;
    private Environment environment;

    private Array<GameObject> instances = new Array<GameObject>();
    //used as a place holder for selected objects
    private HashMap<String, GameObject> selectedObjects = new HashMap<String, GameObject>();
    private FloorObject floorInstance = null;

    private HashMap<String, Boolean> modelQue = new HashMap<String, Boolean>();
    private HashMap<String, Boolean> modelQueToRemove = new HashMap<String, Boolean>();
    private HashMap<String, Model> loadedModels = new HashMap<String, Model>();

    private GameObject selectedObject = null;
    private Vector3 vector3 = new Vector3();

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
        cameraController = new FirstPersonCameraController(camera);
        batch = new ModelBatch();
        assetManager = new AssetManager();
        environment = new Environment();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        camera.near = 0.1f;
        camera.far = 50000f;
        camera.position.set(0, 5, 0);
        camera.update();
        cameraController.setVelocity(50);

        assetManager.setLoader(Texture.class, new TextureLoaderDefault(new InternalFileHandleResolver()));

        physicsWorld = new PhysicsWorld();

        loadModel(ModelList.MODEL_FLOOR);
        loadModel(ModelList.MODEL_BOX);
        loadModel(ModelList.MODEL_RUST_CUBE);
        loadModel(ModelList.MODEL_TORUS_KNOT);

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
        modelQue.put(model.get(), false);
    }

    private Model getModel(ModelList model) {
        return loadedModels.get(model.get());
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
        for (Map.Entry<String, Boolean> entry : modelQue.entrySet()) {
            if (entry.getValue() == false) {
                assetManager.load(entry.getKey(), Model.class);
                entry.setValue(true);
            } else {
                if (assetManager.isLoaded(entry.getKey())) {
                    Model model = assetManager.get(entry.getKey(), Model.class);

                    loadedModels.put(entry.getKey(), model);
                    modelListView.update(loadedModels, instances, physicsWorld, camera);
                    modelQueToRemove.put(entry.getKey(), entry.getValue());
                }
            }
        }

        for (Map.Entry<String, Boolean> entry : modelQueToRemove.entrySet()) {
            if (modelQue.get(entry.getKey())) {
                modelQue.remove(entry.getKey());
            }
        }
        modelQueToRemove.clear();
    }

    private void handleInput() {
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

        String selected = modelListView.getSelected();
        if (selected.equals("null")) {
            selectedObject = null;
        } else if (selected.equals(ModelList.MODEL_BOX.get())) {
            GameObject object = selectedObjects.get(selected);
            if (object != null) {
                selectedObject = object;
            } else {
                BoxObject obj = new BoxObject(getModel(ModelList.MODEL_BOX));
                applyAlpha(obj);
                obj.setName(selected);
                selectedObjects.put(selected, obj);
                selectedObject = obj;
            }
        }else if(selected.equals(ModelList.MODEL_RUST_CUBE.get())){
            GameObject object = selectedObjects.get(selected);
            if (object != null) {
                selectedObject = object;
            } else {
                RustCube obj = new RustCube(getModel(ModelList.MODEL_RUST_CUBE));
                applyAlpha(obj);
                obj.setName(selected);
                selectedObjects.put(selected, obj);
                selectedObject = obj;
            }
        }else if(selected.equals(ModelList.MODEL_FLOOR.get())){
            GameObject object = selectedObjects.get(selected);
            if (object != null) {
                selectedObject = object;
            } else {
                FloorObject obj = new FloorObject(getModel(ModelList.MODEL_FLOOR));
                applyAlpha(obj);
                obj.setName(selected);
                selectedObjects.put(selected, obj);
                selectedObject = obj;
            }
        }else if(selected.equals(ModelList.MODEL_TORUS_KNOT.get())){
            GameObject object = selectedObjects.get(selected);
            if(object != null){
                selectedObject = object;
            }else{
                TorusKnot obj = new TorusKnot(getModel(ModelList.MODEL_TORUS_KNOT));
                applyAlpha(obj);
                obj.setName(selected);
                selectedObjects.put(selected, obj);
                selectedObject = obj;
            }
        }

        physicsWorld.step();

        if (selectedObject != null) {
            float dist = 5f;
            vector3.set(camera.position.x + (camera.direction.x * dist), camera.position.y + (camera.direction.y * dist), camera.position.z + (camera.direction.z * dist));
            selectedObject.translate(vector3);
            selectedObject.transform.rotate(Vector3.X, modelListView.getModelRotation().x);
            selectedObject.transform.rotate(Vector3.Y, modelListView.getModelRotation().y);
            selectedObject.transform.rotate(Vector3.Z, modelListView.getModelRotation().z);
        }

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
                batch.render(instances.get(i), environment);
            }
            if (selectedObject != null) {
                batch.render(selectedObject, environment);
            }
        }
        batch.end();

        physicsWorld.debugDraw(camera);

        stage.act();
        stage.draw();
    }

    private void applyAlpha(GameObject gameObject){
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
            String select = selectedObject.getName();

            if (select.equals(ModelList.MODEL_BOX.get())) {
                BoxObject object = new BoxObject(getModel(ModelList.MODEL_BOX));
                object.transform.set(selectedObject.transform);
                object.createAABB(physicsWorld, modelListView.getMass(),modelListView.getFriction(), modelListView.getRestitution());
                instances.add(object);
            } else if (select.equals(ModelList.MODEL_RUST_CUBE.get())) {
                RustCube object = new RustCube(getModel(ModelList.MODEL_RUST_CUBE));
                object.transform.set(selectedObject.transform);
                object.createAABB(physicsWorld, modelListView.getMass(),modelListView.getFriction(), modelListView.getRestitution());
                instances.add(object);
            } else if (select.equals(ModelList.MODEL_FLOOR.get())) {
                FloorObject object = new FloorObject(getModel(ModelList.MODEL_FLOOR));
                object.transform.set(selectedObject.transform);
                object.createAABB(physicsWorld, modelListView.getMass(),modelListView.getFriction(), modelListView.getRestitution());
                instances.add(object);
            }else if(select.equals(ModelList.MODEL_TORUS_KNOT.get())){
                TorusKnot object = new TorusKnot(getModel(ModelList.MODEL_TORUS_KNOT));
                object.transform.set(selectedObject.transform);
                object.createConvexHull(physicsWorld, modelListView.getMass(),modelListView.getFriction(), modelListView.getRestitution());
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
        return false;
    }
}
