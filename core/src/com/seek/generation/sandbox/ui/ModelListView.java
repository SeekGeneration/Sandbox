package com.seek.generation.sandbox.ui;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.Separator;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.spinner.FloatSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import com.seek.generation.sandbox.ModelList;
import com.seek.generation.sandbox.objects.GameObject;
import com.seek.generation.sandbox.physics.PhysicsWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ModelListView extends Table {

    private VisTextButton models;
    private Table rootTable, modelsTable, propertyTable;
    private ScrollPane scroll;
    private CollapsibleWidget collapsibleModelList, collapsiblePropertyWidget;

    private ButtonGroup buttonGroup;

    private Spinner massSpinner, frictionSpinner, restitutionSpinner, xRotSpinner, yRotSpinner, zRotSpinner;
    private Vector3 rotation = new Vector3();

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
        FloatSpinnerModel spinnerRotationXModel = new FloatSpinnerModel("0.0", "0.0", "360.0", "1.0");
        FloatSpinnerModel spinnerRotationYModel = new FloatSpinnerModel("0.0", "0.0", "360.0", "1.0");
        FloatSpinnerModel spinnerRotationZModel = new FloatSpinnerModel("0.0", "0.0", "360.0", "1.0");

        spinnerRotationXModel.setWrap(true);
        spinnerRotationYModel.setWrap(true);
        spinnerRotationZModel.setWrap(true);

        massSpinner = new Spinner("Mass: ", new FloatSpinnerModel("0.5", "0.0", "1.0", "0.1"));
        frictionSpinner = new Spinner("Friction: ", new FloatSpinnerModel("0.5", "0.0", "1.0", "0.1"));
        restitutionSpinner = new Spinner("Restitution: ", new FloatSpinnerModel("0", "0.0", "1.0", "0.1"));
        xRotSpinner = new Spinner("X Rotation: ", spinnerRotationXModel);
        yRotSpinner = new Spinner("Y Rotation: ", spinnerRotationYModel);
        zRotSpinner = new Spinner("Z Rotation: ", spinnerRotationZModel);

        models.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                collapsibleModelList.setCollapsed(!collapsibleModelList.isCollapsed());
                collapsiblePropertyWidget.setCollapsed(collapsibleModelList.isCollapsed());
            }
        });

        propertyTable.add(new VisLabel("Properties")).center().pad(2).row();
        propertyTable.add(massSpinner).right().pad(2).row();
        propertyTable.add(frictionSpinner).right().pad(2).row();
        propertyTable.add(restitutionSpinner).right().pad(2).row();
        propertyTable.add(xRotSpinner).right().pad(2).row();
        propertyTable.add(yRotSpinner).right().pad(2).row();
        propertyTable.add(zRotSpinner).right().pad(2).row();

        align(Align.topLeft);
        add(models).align(Align.top).row();
        add(new Separator()).fillX().padTop(5).row();
        add(scroll);
        rootTable.add(collapsibleModelList);
        rootTable.add(new Separator()).fillY();
        rootTable.add(collapsiblePropertyWidget).align(Align.top);
    }

    /**
     * @return returns the selected button
     */
    public ModelList getSelected() {
        if (buttonGroup.getChecked() == null) {
            return ModelList.NULL;
        } else {
            Button button = buttonGroup.getChecked();
            ModelList type = ModelList.NULL;
            for (ModelList t : ModelList.values()) {
                if (t.get().equals(button.getName())) {
                    type = t;
                    break;
                }
            }
            return button.getName() != null ? type : ModelList.NULL;
//            return button.getName() != null ? button.getName() : "null";
        }
    }

    //updates the GUI when the loaded models change
    public void update(final HashMap<ModelList, Model> loadedModels, final Array<GameObject> objects, final PhysicsWorld physicsWorld, final Camera camera) {
        modelsTable.clear();
        buttonGroup.clear();
        VisTextButton clear = new VisTextButton("None");
        clear.setName("null");
        buttonGroup.add(clear);
        modelsTable.add(clear).fillX().pad(2).row();

        HashMap<ModelList, Model> list = sort(loadedModels);

        for (final Map.Entry<ModelList, Model> entry : list.entrySet()) {
            if(!entry.getKey().isPlaceable()){
                continue;
            }
            final VisTextButton t = new VisTextButton(entry.getKey().get().substring(entry.getKey().get().lastIndexOf("/") + 1, entry.getKey().get().lastIndexOf(".")));
            t.setName(entry.getKey().get());
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

    String[] chars = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    private HashMap<ModelList, Model> sort(HashMap<ModelList, Model> loadedModels) {
        LinkedHashMap<ModelList, Model> results = new LinkedHashMap<ModelList, Model>();

        List<ModelList> sortList = new ArrayList<ModelList>(loadedModels.keySet());

        Collections.sort(sortList, new Comparator<ModelList>() {
            @Override
            public int compare(ModelList ml1, ModelList ml2) {
                String s1 = ml1.get().substring(ml1.get().lastIndexOf("/") + 1, ml1.get().lastIndexOf("."));
                String s2 = ml2.get().substring(ml2.get().lastIndexOf("/") + 1, ml2.get().lastIndexOf("."));
                int i = s1.compareToIgnoreCase(s2);
                return i;
            }
        });

        results.clear();
        for(ModelList l : sortList){
            Model ml = loadedModels.get(l);
            results.put(l, ml);
        }

        return results;

//        for (Map.Entry<ModelList, Model> e1 : results.entrySet()) {
//            for (Map.Entry<ModelList, Model> e2 : results.entrySet()) {
//                System.out.println("Loop");
//                String name1 = e1.getKey().get().substring(e1.getKey().get().lastIndexOf("/") + 1, e1.getKey().get().lastIndexOf("."));
//                String name2 = e2.getKey().get().substring(e2.getKey().get().lastIndexOf("/") + 1, e2.getKey().get().lastIndexOf("."));
//
//                int char1 = -1;
//                int char2 = -1;
//                for (int i = 0; i < chars.length; i++) {
//                    if (name1.startsWith(chars[i])) {
//                        char1 = i;
//                    }
//
//                    if (name2.startsWith(chars[i])) {
//                        char2 = i;
//                    }
//
//                    if (char1 != -1 && char2 != -1) {
//                        break;
//                    }
//
//                }
//                if (char1 < char2) {
//                    Map.Entry<ModelList, Model> tmp = e2;
//                    e2 = e1;
//                    e1 = tmp;
//                    System.out.println("switch: " + e2.getKey().get() + " + " + e1.getKey().get());
//                }
//            }
//        }
//
//        return results;
    }

    public float getMass() {
        return Float.parseFloat(massSpinner.getModel().getText());
    }

    public float getFriction() {
        return Float.parseFloat(frictionSpinner.getModel().getText());
    }

    public float getRestitution() {
        return Float.parseFloat(restitutionSpinner.getModel().getText());
    }

    public Vector3 getModelRotation() {
        rotation.set(Float.parseFloat(xRotSpinner.getModel().getText()), Float.parseFloat(yRotSpinner.getModel().getText()), Float.parseFloat(zRotSpinner.getModel().getText()));
        return rotation;
    }
}
