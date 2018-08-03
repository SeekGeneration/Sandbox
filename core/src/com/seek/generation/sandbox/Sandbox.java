package com.seek.generation.sandbox;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

public class Sandbox extends ApplicationAdapter {

    private PerspectiveCamera camera;
    private FirstPersonCameraController cameraController;
    private ModelBatch batch;
    private AssetManager assetManager;
    private Environment environment;

    private Array<ModelInstance> instances = new Array<ModelInstance>();

    private HashMap<String, Boolean> modelQue = new HashMap<String, Boolean>();
    private HashMap<String, Boolean> modelQueToRemove = new HashMap<String, Boolean>();
    private HashMap<String, Model> loadedModels = new HashMap<String, Model>();

    @Override
    public void create() {
        Gdx.gl.glClearColor(0.5f, 0.8f, 0.9f, 1);
        camera = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cameraController = new FirstPersonCameraController(camera);
        batch = new ModelBatch();
        assetManager = new AssetManager();
        environment = new Environment();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        camera.near = 0.1f;
        camera.far = 5000f;
        camera.update();

        assetManager.setLoader(Texture.class, new TextureLoaderDefault(new InternalFileHandleResolver()));

        Gdx.input.setInputProcessor(cameraController);
        loadModel(ModelList.MODEL_BOX);
        loadModel(ModelList.MODEL_RUST_CUBE);
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

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.graphics.setTitle("FPS: " + Gdx.graphics.getFramesPerSecond() + " Instances: " + instances.size);

        if (!assetManager.update()) {
            //display a loading screen/bar
        }

        handleLoading();

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            Model model = getModel(ModelList.MODEL_BOX);
            if (model != null) {
                ModelInstance instance = new ModelInstance(model);

                instances.add(instance);
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.C)){
            Model model = getModel(ModelList.MODEL_RUST_CUBE);
            if(model != null){
                ModelInstance instance = new ModelInstance(model);
                instance.transform.setToTranslation(-5, -5, -5);
                instances.add(instance);
            }
        }

        cameraController.update();

        batch.begin(camera);
        {
            batch.render(instances, environment);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetManager.dispose();
    }
}
