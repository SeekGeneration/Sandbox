package com.seek.generation.sandbox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

public class CameraController extends InputAdapter{

    private final Camera camera;
    private final IntIntMap keys = new IntIntMap();
    private final Vector3 tmp = new Vector3();
    private final Vector3 tmp2 = new Vector3();
    private final Vector3 tmp3 = new Vector3();
    private float velocity = 5f;
    private float ySensitivity = 0.5f;
    private float xSensitivity = 0.5f;

    private int FORWARD = Input.Keys.W;
    private int LEFT = Input.Keys.A;
    private int RIGHT = Input.Keys.D;
    private int BACK = Input.Keys.S;

    public CameraController(Camera camera){
        this.camera = camera;
    }

    public void setVelocity(float velocity){
        this.velocity = velocity;
    }

    public void setySensitivty(float sensitivity){
        this.ySensitivity = sensitivity;
    }

    public void setxSensitivty(float sensitivity){
        this.xSensitivity = sensitivity;
    }

    public boolean mouseMoved (int screenX, int screenY) {
        if(Gdx.input.isCursorCatched()){
            float dx = -Gdx.input.getDeltaX() * xSensitivity;
            float dy = -Gdx.input.getDeltaY() * ySensitivity;
            camera.direction.rotate(camera.up, dx);
            tmp.set(camera.direction).crs(camera.up).nor();
//            camera.direction.rotate(tmp, dy);

            //code to prevent camera flipping when looking up or down
            Vector3 oldPitchAxis = tmp.set(camera.direction).crs(camera.up).nor();
            Vector3 newDirection = tmp2.set(camera.direction).rotate(tmp, dy);
            Vector3 newPitchAxis = tmp3.set(tmp2).crs(camera.up);
            if (!newPitchAxis.hasOppositeDirection(oldPitchAxis))
                camera.direction.set(newDirection);

            return true;
        }
        return false;
    }

    @Override
    public boolean keyDown (int keycode) {
        keys.put(keycode, keycode);
        return true;
    }

    @Override
    public boolean keyUp (int keycode) {
        keys.remove(keycode, 0);
        return true;
    }

    public void update(){
        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float deltaTime){
        if (keys.containsKey(FORWARD)) {
            tmp.set(camera.direction).nor().scl(deltaTime * velocity);
            tmp.y = 0;
            camera.position.add(tmp);
        }
        if (keys.containsKey(BACK)) {
            tmp.set(camera.direction).nor().scl(-deltaTime * velocity);
            tmp.y = 0;
            camera.position.add(tmp);
        }
        if (keys.containsKey(LEFT)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(-deltaTime * velocity);
            tmp.y = 0;
            camera.position.add(tmp);
        }
        if (keys.containsKey(RIGHT)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(deltaTime * velocity);
            tmp.y = 0;
            camera.position.add(tmp);
        }
        camera.update(true);
    }


}
