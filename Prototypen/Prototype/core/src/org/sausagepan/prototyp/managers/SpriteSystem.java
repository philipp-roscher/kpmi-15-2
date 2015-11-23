package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.components.CharacterSpriteComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.model.entities.EntityFamilies;
import org.sausagepan.prototyp.view.OrthogonalTiledMapRendererWithPlayers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.utils.ImmutableArray;

/**
 * Created by georg on 21.10.15.
 */
public class SpriteSystem extends EntitySystem implements EntityListener{
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
        entities = engine.getEntitiesFor(EntityFamilies.spriteFamily);

        refreshMapRenderer();
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

    @Override
    public void entityAdded(Entity entity) {
        // CHeck whether entity matters
        if(EntityFamilies.spriteFamily.matches(entity))
            addedToEngine(this.getEngine());
    }

    @Override
    public void entityRemoved(Entity entity) {
        if(entities.contains(entity, false)) {
            if (sm.get(entity) != null)
                tmr.addSprite(sm.get(entity).sprite);
            if (cm.get(entity) != null)
                tmr.addSprite(entity.getComponent(CharacterSpriteComponent.class).sprite);
            if (wm.get(entity) != null)
                tmr.addWeaponComponent(entity.getComponent(WeaponComponent.class));
            addedToEngine(this.getEngine());
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
