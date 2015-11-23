package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.enums.ObservableEntityMessage;

import com.badlogic.ashley.core.EntitySystem;

/**
 * Observable {@link com.badlogic.ashley.core.Engine} which notifies {@link ObservingEntitySystem}
 * if entities get removed or added to it.
 * Created by georg on 06.11.15.
 */
public abstract class ObservingEntitySystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    public abstract void addedToEngine(ObservableEngine engine);

    /**
     * Refreshes list of entities when one is added to or removed from engine
     * @param engine
     * @param message
     */
    public void getNotified(ObservableEngine engine, ObservableEntityMessage message) {
        if(message == ObservableEntityMessage.ENTITY_REMOVED ||
                message == ObservableEntityMessage.ENTITY_ADDED)
            addedToEngine(engine);
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
