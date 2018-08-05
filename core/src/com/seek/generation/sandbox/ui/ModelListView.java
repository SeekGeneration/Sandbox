package com.seek.generation.sandbox.ui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.Separator;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.spinner.FloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import com.seek.generation.sandbox.objects.GameObject;
import com.seek.generation.sandbox.physics.PhysicsWorld;

import java.util.HashMap;
import java.util.Map;

public class ModelListView extends Table{

    private VisTextButton models;
    private Table rootTable, modelsTable, propertyTable;
    private ScrollPane scroll;
    private CollapsibleWidget collapsibleModelList, collapsiblePropertyWidget;

    private ButtonGroup buttonGroup;

    private FloatSpinnerModel spinnerModel = new FloatSpinnerModel("1.0", "0.0", "1.0", "0.1");
    private Spinner massSpinner, frictionSpinner, restitutionSpinner;

    public ModelListView() {
        super();
        models = new VisTextButton("Models");
        rootTable = new Table();
        scroll = new ScrollPane(rootTable);

        modelsTable = new Table();
        collapsibleModelList = new CollapsibleWidget(modelsTable);

        propertyTable = new Table();
        collapsiblePropertyWidget = new CollapsibleWidget(propertyTable);

        buttonGroup = new ButtonGroup();

        massSpinner = new Spinner("Mass: ", spinnerModel);
        frictionSpinner = new Spinner("Friction: ", new FloatSpinnerModel("0.5", "0.0", "1.0", "0.1"));
        restitutionSpinner = new Spinner("Restitution: ", new FloatSpinnerModel("0", "0.0", "1.0", "0.1"));

        models.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                collapsibleModelList.setCollapsed(!collapsibleModelList.isCollapsed());
                collapsiblePropertyWidget.setCollapsed(collapsibleModelList.isCollapsed());
            }
        });

        propertyTable.add(massSpinner).right().pad(2).row();
        propertyTable.add(frictionSpinner).right().pad(2).row();
        propertyTable.add(restitutionSpinner).right().pad(2);

        align(Align.topLeft);
        add(models).align(Align.top).row();
        add(new Separator()).fillX().padTop(5).row();
        add(scroll);
        rootTable.add(collapsibleModelList);
        rootTable.add(new Separator()).fillY();
        rootTable.add(collapsiblePropertyWidget).align(Align.top);
    }

    public String getSelected(){
        if(buttonGroup.getChecked() == null){
            return "null";
        }else {
            Button button = buttonGroup.getChecked();

            return button.getName() != null ? button.getName() : "null";
        }
    }

    public void update(final HashMap<String, Model> loadedModels, final Array<GameObject> objects, final PhysicsWorld physicsWorld, final Camera camera){
        modelsTable.clear();
        buttonGroup.clear();
        VisTextButton clear = new VisTextButton("None");
        clear.setName("null");
        buttonGroup.add(clear);
        modelsTable.add(clear).fillX().pad(2).row();
        for(final Map.Entry<String, Model> entry : loadedModels.entrySet()){
            final VisTextButton t = new VisTextButton(entry.getKey().substring(entry.getKey().lastIndexOf("/") +1, entry.getKey().lastIndexOf(".")));
            t.setName(entry.getKey());
            buttonGroup.add(t);
//            final Model model = entry.getValue();
//            t.addListener(new ChangeListener() {
//                public void changed(ChangeEvent event, Actor actor) {
//                    GameObject object = null;
//                    if(entry.getKey().equals(ModelList.MODEL_RUST_CUBE.get())){
//                        object = new RustCube(model);
//                        object.setAsPhysicsObject(physicsWorld);
//                    }else if(entry.getKey().equals(ModelList.MODEL_FLOOR.get())){
//                        object = new FloorObject(model);
//                        object.setAsPhysicsObject(physicsWorld);
//                    }else if(entry.getKey().equals(ModelList.MODEL_BOX.get())){
//                        object = new BoxObject(model);
//                        object.setAsPhysicsObject(physicsWorld);
//                    }
//
//                    float dist = 5f;
//                    vector3.set(camera.position.x + (camera.direction.x * dist), camera.position.y + (camera.direction.y * dist), camera.position.z + (camera.direction.z * dist));
//                    object.translate(vector3);
//
//                    objects.add(object);
//                }
//            });

            modelsTable.add(t).fillX().pad(2).row();
        }
    }

    public float getMass(){
        return Float.parseFloat(massSpinner.getModel().getText());
    }

    public float getFriction(){
        return Float.parseFloat(frictionSpinner.getModel().getText());
    }

    public float getRestitution()
    {
        return Float.parseFloat(restitutionSpinner.getModel().getText());
    }
}
