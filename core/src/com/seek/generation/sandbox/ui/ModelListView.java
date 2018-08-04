package com.seek.generation.sandbox.ui;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.Separator;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

import java.util.HashMap;
import java.util.Map;

public class ModelListView extends Table{

    private VisTextButton models;
    private Table rootTable, collapsibleTable;
    private ScrollPane scroll;
    private CollapsibleWidget collapsibleWidget;

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

    public void update(HashMap<String, Model> loadedModels){
        collapsibleTable.clear();
        for(Map.Entry<String, Model> entry : loadedModels.entrySet()){
            VisTextButton t = new VisTextButton(entry.getKey().substring(entry.getKey().lastIndexOf("/") +1));
            collapsibleTable.add(t).row();
        }
    }
}
