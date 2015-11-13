package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.EntitySystem;

import org.sausagepan.prototyp.enums.ObservableEntityMessage;

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
    public void getNotified(ObservableEngine engine, ObservableEntityMessage message) {
        if(message == ObservableEntityMessage.ENTITY_REMOVED ||
                message == ObservableEntityMessage.ENTITY_ADDED)
            addedToEngine(engine);
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
