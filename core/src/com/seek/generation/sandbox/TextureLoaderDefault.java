package com.seek.generation.sandbox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;

public class TextureLoaderDefault extends TextureLoader {
    public TextureLoaderDefault(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, TextureParameter parameter) {
        if(!file.exists()){
            System.out.println("Missing file: " + file);
            file = Gdx.files.internal("default.jpg");
        }

        super.loadAsync(manager, fileName, file, parameter);
    }
}
