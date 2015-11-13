package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.ItemComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.SpriteComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.view.OrthogonalTiledMapRendererWithPlayers;

import java.util.Iterator;

/**
 * Created by georg on 13.11.15.
 */
public class ItemSystem extends ObservingEntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ComponentMapper<InventoryComponent> im
            = ComponentMapper.getFor(InventoryComponent.class);
    private ComponentMapper<ItemComponent> itemM
            = ComponentMapper.getFor(ItemComponent.class);
    private ComponentMapper<InjurableAreaComponent> am
            = ComponentMapper.getFor(InjurableAreaComponent.class);
    private ComponentMapper<SpriteComponent> sm
            = ComponentMapper.getFor(SpriteComponent.class);

    private ImmutableArray<Entity> characters;
    private ImmutableArray<Entity> items;

    private OrthogonalTiledMapRendererWithPlayers tmr;
    /* ........................................................................... CONSTRUCTOR .. */
    public ItemSystem(OrthogonalTiledMapRendererWithPlayers tmr) {
        this.tmr = tmr;
    }
    /* ............................................................................... METHODS .. */
    @Override
    public void addedToEngine(ObservableEngine engine) {
        characters = engine.getEntitiesFor(Family.all(
                WeaponComponent.class,
                InventoryComponent.class,
                TeamComponent.class,
                DynamicBodyComponent.class,
                NetworkComponent.class,
                InjurableAreaComponent.class).get());
        items = engine.getEntitiesFor(Family.all(
                ItemComponent.class,
                SpriteComponent.class,
                InjurableAreaComponent.class).get());
    }

    public void update(float deltaTime)
    {
        for (Entity entity : characters) {
            InventoryComponent inventory = im.get(entity);
            InjurableAreaComponent area  = am.get(entity);
            Iterator<Entity> itemIterator = items.iterator();
            while (itemIterator.hasNext()) {
                Entity item = itemIterator.next();
                SpriteComponent sprite = sm.get(item);
                if(area.area.overlaps(am.get(item).area)) {
                    System.out.println("Picked up Item: " + itemM.get(item).item.getClass());
                    inventory.pickUpItem(itemM.get(item).item, 1);
                    tmr.removeSprite(sprite.sprite);
                    getEngine().removeEntity(item);
                }
            }
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
