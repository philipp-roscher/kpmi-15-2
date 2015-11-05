package org.sausagepan.prototyp.managers;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import org.sausagepan.prototyp.enums.KeySection;
import org.sausagepan.prototyp.model.Key;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;

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

    /*...................................................................................Functions*/
    public void addedToEngine(Engine engine)
    {
        characters = engine.getEntitiesFor(Family.all(WeaponComponent.class, InventoryComponent.class, TeamComponent.class).get());
    }

    public void setWeaponInInventory()
    {
        for(Entity character: characters)
        {
            WeaponComponent wc = wm.get(character);
            InventoryComponent ic = im.get(character);
            ic.weapon = wc.weapon;
        }
    }

    //Hier wird bei beiden Teams der Schlüsselträger durch Zufall ausgewählt und einen Schlüsselteil übergeben
    public void setUpKeyBags()
    {
        Entity[] teamOne = new Entity[2];
        int countOne = 0;
        Entity[] teamTwo = new Entity[2];
        int countTwo = 0;

        List<Key> keys = createKeys();

        for(Entity character : characters)
        {
            //Game Master spielt alleine, deswegen wird im der Beute automatisch überwiesen
            TeamComponent tc = tm.get(character);
            if(tc.TeamId == 0)
            {
                im.get(character).createKeyBag(true);
                im.get(character).getKeyBag().add(keys.get(0));
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
        int number = getRandomNumber();
        im.get(teamOne[number]).createKeyBag(true);
        im.get(teamOne[number]).addKeyPart(keys.get(1));
        im.get(teamOne[2 - number - 1]).createKeyBag(false);

        number = getRandomNumber();
        im.get(teamTwo[number]).createKeyBag(true);
        im.get(teamTwo[number]).addKeyPart(keys.get(2));
        im.get(teamTwo[2 - number - 1]).createKeyBag(false);

        keys.clear();
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

    //da math.random nur doubles erzeugt, wird es hier zu einem int konvertiert
    public int getRandomNumber()
    {
        double number = Math.random();
        number = number%2;
        Long l = Math.round(number);
        int i = Integer.valueOf(l.intValue());
        return i;
    }
}
