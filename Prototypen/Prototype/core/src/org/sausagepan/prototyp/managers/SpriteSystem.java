package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.enums.ObservableEntityMessage;
import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.view.OrthogonalTiledMapRendererWithPlayers;

import java.util.Iterator;

/**
 * Created by georg on 21.10.15.
 */
public class SpriteSystem extends ObservingEntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private ComponentMapper<SpriteComponent> sm
            = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<WeaponComponent> wm
            = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<CharacterSpriteComponent> cm
            = ComponentMapper.getFor(CharacterSpriteComponent.class);
    private OrthogonalTiledMapRendererWithPlayers tmr;
    /* ........................................................................... CONSTRUCTOR .. */
    public SpriteSystem(Maze maze) {
        this.tmr = maze.getTiledMapRenderer();
    }
    /* ............................................................................... METHODS .. */
    public void addedToEngine(ObservableEngine engine) {
        // Get all entities with either Sprite-, Weapon- or CharacterSprite Components
        entities = engine.getEntitiesFor(Family.one(
                SpriteComponent.class,
                WeaponComponent.class,
                CharacterSpriteComponent.class).get());

        refreshMapRenderer();
    }

    @Override
    public void getNotified(ObservableEngine engine, ObservableEntityMessage message) {
        if(message == ObservableEntityMessage.ENTITY_REMOVED ||
                message == ObservableEntityMessage.ENTITY_ADDED) {
            addedToEngine(engine);
        }
    }

    public void refreshMapRenderer() {
        tmr.clearSprites();
        for(Entity e : entities) {
            if(sm.get(e) != null) tmr.addSprite(sm.get(e).sprite);
            if(cm.get(e) != null) tmr.addSprite(e.getComponent(CharacterSpriteComponent.class).sprite);
            if(wm.get(e) != null) tmr.addWeaponComponent(e.getComponent(WeaponComponent.class));
        }
    }

    public void update(float deltaTime) {
        // TODO
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
