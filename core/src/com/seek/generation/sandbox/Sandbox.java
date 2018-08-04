package com.seek.generation.sandbox;

import com.badlogic.gdx.InputMultiplexer;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.seek.generation.sandbox.objects.BoxObject;
import com.seek.generation.sandbox.objects.FloorObject;
import com.seek.generation.sandbox.objects.GameObject;
import com.seek.generation.sandbox.objects.RustCube;
import com.seek.generation.sandbox.physics.PhysicsWorld;
import com.seek.generation.sandbox.ui.ModelListView;

import java.util.HashMap;
import java.util.Map;

public class Sandbox extends ApplicationAdapter {

    //graphics
    private PerspectiveCamera camera;
    private FirstPersonCameraController cameraController;
    private ModelBatch batch;
    private AssetManager assetManager;
    private Environment environment;

    private Array<GameObject> instances = new Array<GameObject>();
    private FloorObject floorInstance = null;

    private HashMap<String, Boolean> modelQue = new HashMap<String, Boolean>();
    private HashMap<String, Boolean> modelQueToRemove = new HashMap<String, Boolean>();
    private HashMap<String, Model> loadedModels = new HashMap<String, Model>();

    //physics
    private PhysicsWorld physicsWorld;

    //ui
    private Stage stage;
    private Table rootTable;
    private ModelListView modelListView;

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

        //setup UI
        stage = new Stage();
        rootTable = new Table();
        modelListView = new ModelListView();

        rootTable.add(modelListView).grow().align(Align.left);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);
        stage.setDebugAll(true);

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, cameraController));
    }

    private void loadModel(ModelList model){
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

    private void handleInput()
    {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            Model model = getModel(ModelList.MODEL_BOX);
            if (model != null) {
                GameObject instance = new BoxObject(model);
                instances.add(instance);

                instance.setAsPhysicsObject(physicsWorld);
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.C)){
            Model model = getModel(ModelList.MODEL_RUST_CUBE);
            if(model != null){
                GameObject instance = new RustCube(model);
                instance.transform.setToTranslation(-5, 25, -5);
                instance.transform.rotate(Vector3.X, 25);
                instance.transform.rotate(Vector3.Y, 25);
                instances.add(instance);

                instance.setAsPhysicsObject(physicsWorld);
            }
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond() + " Instances: " + instances.size);

        if (!assetManager.update()) {
            //display a loading screen/bar
        }

        handleLoading();
        handleInput();

        physicsWorld.step();

        if(floorInstance == null){
            Model model = getModel(ModelList.MODEL_FLOOR);
            if(model != null){
                floorInstance = new FloorObject(model);
                instances.add(floorInstance);
                floorInstance.setAsPhysicsObject(physicsWorld);
            }
        }

        cameraController.update();

        batch.begin(camera);
        {
            for(int i = instances.size - 1; i >= 0; i--) {
                batch.render(instances.get(i), environment);
            }
        }
        batch.end();

        physicsWorld.debugDraw(camera);

        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        physicsWorld.dispose();

        for(GameObject go : instances){
            go.dispose();
        }

        stage.dispose();
        VisUI.dispose();
        batch.dispose();
        assetManager.dispose();
    }
}
