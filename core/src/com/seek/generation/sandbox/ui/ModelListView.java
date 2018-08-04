package com.seek.generation.sandbox.ui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.Separator;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.seek.generation.sandbox.ModelList;
import com.seek.generation.sandbox.objects.BoxObject;
import com.seek.generation.sandbox.objects.FloorObject;
import com.seek.generation.sandbox.objects.GameObject;
import com.seek.generation.sandbox.objects.RustCube;
import com.seek.generation.sandbox.physics.PhysicsWorld;

import java.util.HashMap;
import java.util.Map;

public class ModelListView extends Table{

    private VisTextButton models;
    private Table rootTable, collapsibleTable;
    private ScrollPane scroll;
    private CollapsibleWidget collapsibleWidget;

    private Vector3 vector3 = new Vector3();

    public ModelListView() {
        super();
        models = new VisTextButton("Models");
        rootTable = new Table();
        scroll = new ScrollPane(rootTable);
        collapsibleTable = new Table();
        collapsibleWidget = new CollapsibleWidget(collapsibleTable);

        models.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                collapsibleWidget.setCollapsed(!collapsibleWidget.isCollapsed());
            }
        });

        align(Align.topLeft);
        add(models).align(Align.top).row();
        add(new Separator()).fillX().pad(5).row();
        add(scroll);
        rootTable.add(collapsibleWidget);
    }

    public void update(final HashMap<String, Model> loadedModels, final Array<GameObject> objects, final PhysicsWorld physicsWorld, final Camera camera){
        collapsibleTable.clear();
        for(final Map.Entry<String, Model> entry : loadedModels.entrySet()){
            final VisTextButton t = new VisTextButton(entry.getKey().substring(entry.getKey().lastIndexOf("/") +1, entry.getKey().lastIndexOf(".")));
            final Model model = entry.getValue();
            t.addListener(new ChangeListener() {
                public void changed(ChangeEvent event, Actor actor) {
                    GameObject object = null;
                    if(entry.getKey().equals(ModelList.MODEL_RUST_CUBE.get())){
                        object = new RustCube(model);
                        object.setAsPhysicsObject(physicsWorld);
                    }else if(entry.getKey().equals(ModelList.MODEL_FLOOR.get())){
                        object = new FloorObject(model);
                        object.setAsPhysicsObject(physicsWorld);
                    }else if(entry.getKey().equals(ModelList.MODEL_BOX.get())){
                        object = new BoxObject(model);
                        object.setAsPhysicsObject(physicsWorld);
                    }

                    float dist = 5f;
                    vector3.set(camera.position.x + (camera.direction.x * dist), camera.position.y + (camera.direction.y * dist), camera.position.z + (camera.direction.z * dist));
                    object.translate(vector3);

                    objects.add(object);
                }
            });

            collapsibleTable.add(t).fillX().pad(2).row();
        }
    }
}
