package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.utils.Array;

import org.sausagepan.prototyp.enums.ObservableEntityMessage;

import java.util.Observable;

/**
 * Created by georg on 06.11.15.
 */
public class ObservableEngine extends Engine {
    /* ............................................................................ ATTRIBUTES .. */
    private Array<ObservingEntitySystem> observeringSystems;
    /* ........................................................................... CONSTRUCTOR .. */
    public ObservableEngine() {
        super();
    }
    /* ............................................................................... METHODS .. */
    public void subscribe(ObservingEntitySystem entitySystem) {
        this.observeringSystems.add(entitySystem);
    }

    @Override
    public void addEntity(Entity entity) {
        super.addEntity(entity);
    }

    private void notifyObservers(ObservableEntityMessage message) {
        for(ObservingEntitySystem oes : observeringSystems)
            oes.getNotified(this, message);
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
