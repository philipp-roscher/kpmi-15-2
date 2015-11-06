package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import org.sausagepan.prototyp.enums.KeySection;
import org.sausagepan.prototyp.model.Key;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.KeyViewerComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.User_Interface.Actors.KeyActor;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Bettina on 03.11.2015.
 */
public class InventorySystem extends EntitySystem {

    /*...................................................................................Atributes*/
    private ImmutableArray<Entity> characters;

    private ComponentMapper<InventoryComponent> im = ComponentMapper.getFor(InventoryComponent.class);
    private ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<TeamComponent> tm = ComponentMapper.getFor(TeamComponent.class);
    private ComponentMapper<KeyViewerComponent> kvm = ComponentMapper.getFor(KeyViewerComponent.class);

    /*...................................................................................Functions*/
    public void addedToEngine(Engine engine)
    {
        characters = engine.getEntitiesFor(Family.all(WeaponComponent.class, InventoryComponent.class, TeamComponent.class).get());
    }

    public void setWeaponInInventory()
    {
        for(Entity character: characters)
        {
            im.get(character).weapon = wm.get(character).weapon;
        }
    }

    //Hier wird bei beiden Teams der Schluesseltraeger durch Zufall ausgewaehlt und einen Schluesselteil uebergeben
    public void setUpKeyBags()
    {
        Entity[] teamOne = new Entity[2];
        int countOne = 0;
        Entity[] teamTwo = new Entity[2];
        int countTwo = 0;

        int number;

        List<Key> keys = createKeys();
        List<KeyActor> keyActors = createKeyActors();

        for(Entity character : characters)
        {
            //Game Master spielt alleine, deswegen wird im der Beute automatisch ueberwiesen
            TeamComponent tc = tm.get(character);
            if(tc.TeamId == 0)
            {
                im.get(character).createKeyBag(true);
                im.get(character).getKeyBag().add(keys.get(0));

                kvm.get(character).create();
                kvm.get(character).addKey(keyActors.get(0));
            }

            if(tc.TeamId == 1)
            {
                teamOne[countOne] = character;
                countOne++;
            }

            if(tc.TeamId == 2)
            {
                teamTwo[countTwo] = character;
                countTwo++;
            }
        }

        //2 - 1 - 1 = 0
        //2 - 0 - 1 = 1
        number = getRandomNumber();
        if(teamOne[number] !=  null) {
            im.get(teamOne[number]).createKeyBag(true);
            im.get(teamOne[number]).addKeyPart(keys.get(1));

            kvm.get(teamOne[number]).create();
            kvm.get(teamOne[number]).addKey(keyActors.get(1));
        }

        if(teamOne[2 - number - 1] !=  null) {
            im.get(teamOne[2 - number - 1]).createKeyBag(false);
            im.get(teamOne[2 - number - 1]).keyBag = im.get(teamOne[number]).keyBag;

            kvm.get(teamOne[2 - number - 1]).create();
            kvm.get(teamOne[2 - number - 1]).addKey(keyActors.get(1));
        }

        number = getRandomNumber();
        if(teamTwo[number] !=  null) {
            im.get(teamTwo[number]).createKeyBag(true);
            im.get(teamTwo[number]).addKeyPart(keys.get(2));

            kvm.get(teamTwo[number]).create();
            kvm.get(teamTwo[number]).addKey(keyActors.get(2));
        }

        if(teamTwo[2 - number - 1] !=  null) {
            im.get(teamTwo[2 - number - 1]).createKeyBag(false);
            im.get(teamTwo[2 - number - 1]).keyBag = im.get(teamTwo[number]).keyBag;

            kvm.get(teamTwo[2 - number - 1]).create();
            kvm.get(teamTwo[2 - number - 1]).addKey(keyActors.get(2));
        }

        keys.clear();
        keyActors.clear();
    }

    public void drawKeys()
    {
        for(Entity character : characters)
        {
            kvm.get(character).render();
        }
    }

    public List<Key> createKeys()
    {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("textures/spritesheets/KeySections/keyAtlas.pack"));
        Key keyPartOne = new Key(KeySection.PartOne, atlas.findRegion("PartOne"));
        Key keyPartTwo = new Key(KeySection.PartTwo, atlas.findRegion("PartTwo"));
        Key keyPartThree = new Key (KeySection.PartThree, atlas.findRegion("PartThree"));

        List<Key> keys = new LinkedList<Key>();
        keys.add(keyPartOne);
        keys.add(keyPartTwo);
        keys.add(keyPartThree);

        return keys;
    }

    public List<KeyActor> createKeyActors()
    {
        KeyActor keyPartOne = new KeyActor(new Texture(Gdx.files.internal("textures/User Interface/KeyPartOne.png")), KeySection.PartOne);
        KeyActor keyPartTwo = new KeyActor(new Texture(Gdx.files.internal("textures/User Interface/KeyPartTwo.png")), KeySection.PartTwo);
        KeyActor keyPartThree = new KeyActor(new Texture(Gdx.files.internal("textures/User Interface/KeyPartThree.png")), KeySection.PartThree);

        List<KeyActor> keys = new LinkedList<KeyActor>();
        keys.add(keyPartOne);
        keys.add(keyPartTwo);
        keys.add(keyPartThree);

        return keys;
    }

    //da math.random nur doubles erzeugt, wird es hier zu einem int konvertiert
    public int getRandomNumber()
    {
        double number = Math.random();
        Long l = Math.round(number);
        int i = Integer.valueOf(l.intValue());
        i = i%2;
        return i;
    }
}
