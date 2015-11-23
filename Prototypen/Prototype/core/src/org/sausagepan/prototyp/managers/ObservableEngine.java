package org.sausagepan.prototyp.managers;

import org.sausagepan.prototyp.enums.ObservableEntityMessage;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

/**
 * Created by georg on 06.11.15.
 */
public class ObservableEngine extends Engine {
    /* ............................................................................ ATTRIBUTES .. */
    private Array<ObservingEntitySystem> observingSystems;
    /* ........................................................................... CONSTRUCTOR .. */
    public ObservableEngine() {
        super();
        this.observingSystems = new Array<ObservingEntitySystem>();
    }
    /* ............................................................................... METHODS .. */
    public void subscribe(ObservingEntitySystem entitySystem) {
        this.observingSystems.add(entitySystem);
    }

    @Override
    public void addEntity(Entity entity) {
        super.addEntity(entity);
        notifyObservers(ObservableEntityMessage.ENTITY_ADDED);
    }

    @Override
    public void removeEntity(Entity entity) {
        super.removeEntity(entity);
        notifyObservers(ObservableEntityMessage.ENTITY_REMOVED);
    }

    private void notifyObservers(ObservableEntityMessage message) {
        for(ObservingEntitySystem oes : observingSystems)
            oes.getNotified(this, message);
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
