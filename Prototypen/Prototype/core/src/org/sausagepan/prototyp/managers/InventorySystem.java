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
import com.badlogic.gdx.math.Intersector;

import org.sausagepan.prototyp.enums.KeySection;
import org.sausagepan.prototyp.graphics.EntitySprite;
import org.sausagepan.prototyp.model.Key;
import org.sausagepan.prototyp.model.Maze;
import org.sausagepan.prototyp.model.components.DynamicBodyComponent;
import org.sausagepan.prototyp.model.components.HealthComponent;
import org.sausagepan.prototyp.model.components.IdComponent;
import org.sausagepan.prototyp.model.components.InjurableAreaComponent;
import org.sausagepan.prototyp.model.components.InventoryComponent;
import org.sausagepan.prototyp.model.components.KeyViewerComponent;
import org.sausagepan.prototyp.model.components.NetworkComponent;
import org.sausagepan.prototyp.model.components.TeamComponent;
import org.sausagepan.prototyp.model.components.WeaponComponent;
import org.sausagepan.prototyp.network.Network.HPUpdateRequest;
import org.sausagepan.prototyp.User_Interface.Actors.KeyActor;
import org.sausagepan.prototyp.view.OrthogonalTiledMapRendererWithPlayers;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Bettina on 03.11.2015.
 */
public class InventorySystem extends ObservingEntitySystem {

    /*...................................................................................Atributes*/
    private ImmutableArray<Entity> characters;
    private Maze maze;

    private ComponentMapper<InventoryComponent> im = ComponentMapper.getFor(InventoryComponent.class);
    private ComponentMapper<WeaponComponent> wm = ComponentMapper.getFor(WeaponComponent.class);
    private ComponentMapper<TeamComponent> tm = ComponentMapper.getFor(TeamComponent.class);
    private ComponentMapper<KeyViewerComponent> kvm = ComponentMapper.getFor(KeyViewerComponent.class);
    private ComponentMapper<DynamicBodyComponent> dbm = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<NetworkComponent> nm = ComponentMapper.getFor(NetworkComponent.class);
    private ComponentMapper<InjurableAreaComponent> iam = ComponentMapper.getFor(InjurableAreaComponent.class);
    private ComponentMapper<IdComponent> idm = ComponentMapper.getFor(IdComponent.class);

    public InventorySystem(Maze maze) {
        this.maze = maze;
    }

    /*...................................................................................Functions*/
    public void addedToEngine(ObservableEngine engine)
    {
        characters = engine.getEntitiesFor(Family.all(WeaponComponent.class, InventoryComponent.class, TeamComponent.class, KeyViewerComponent.class, DynamicBodyComponent.class, NetworkComponent.class, HealthComponent.class, InjurableAreaComponent.class).get());
    }

    public void update(OrthogonalTiledMapRendererWithPlayers renderer)
    {
        drawKeys();
        loseKeys(renderer);
        addKey(renderer);
        updateKeyBags();
        updateDoors();
    }

    public void setWeaponInInventory()
    {
        for(Entity character: characters)
        {
            im.get(character).weapon = wm.get(character).weapon;
        }
    }

    //the keyholder is chosen by chance and the keybags are created for all the players
    public void setUpKeyBags()
    {
        Entity[] teamOne;
        int countOne = 0;
        Entity[] teamTwo;
        int countTwo = 0;

        int number;

        List<Key> keys = createKeys();

        if(characters.size() <= 3)
        {
            for(Entity character : characters)
            {
                if(tm.get(character).TeamId == 0)
                {
                    im.get(character).createKeyBag(true);
                    im.get(character).isKeyHolder = true;
                    im.get(character).addKeyPart(keys.get(0));
                    System.out.println(im.get(character).getKeyBag().size());
                    kvm.get(character).create();
                    kvm.get(character).addKey(keys.get(0).getKeyActor());
                }

                if(tm.get(character).TeamId == 1)
                {
                    im.get(character).createKeyBag(true);
                    im.get(character).isKeyHolder = true;
                    im.get(character).addKeyPart(keys.get(1));

                    kvm.get(character).create();
                    kvm.get(character).addKey(keys.get(1).getKeyActor());
                }

                if(tm.get(character).TeamId == 2)
                {
                    im.get(character).createKeyBag(true);
                    im.get(character).isKeyHolder = true;
                    im.get(character).addKeyPart(keys.get(2));

                    kvm.get(character).create();
                    kvm.get(character).addKey(keys.get(2).getKeyActor());
                }
            }
        }

        if(characters.size() == 5)
        {
            teamOne = new Entity[2];
            teamTwo = new Entity[2];

            for(Entity character : characters)
            {
                //Game Master spielt alleine, deswegen wird im der Beute automatisch ueberwiesen
                TeamComponent tc = tm.get(character);
                if(tc.TeamId == 0)
                {
                    im.get(character).createKeyBag(true);
                    im.get(character).isKeyHolder = true;
                    im.get(character).addKeyPart(keys.get(0));

                    kvm.get(character).create();
                    kvm.get(character).addKey(keys.get(0).getKeyActor());
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
                im.get(teamOne[number]).isKeyHolder = true;
                im.get(teamOne[number]).addKeyPart(keys.get(1));

                kvm.get(teamOne[number]).create();
                kvm.get(teamOne[number]).addKey(keys.get(1).getKeyActor());
            }

            if(teamOne[2 - number - 1] !=  null) {
                im.get(teamOne[2 - number - 1]).createKeyBag(false);
                im.get(teamOne[2 - number - 1]).keyBag = im.get(teamOne[number]).keyBag;

                kvm.get(teamOne[2 - number - 1]).create();
                kvm.get(teamOne[2 - number - 1]).addKey(keys.get(1).getKeyActor());
            }

            number = getRandomNumber();
            if(teamTwo[number] !=  null) {
                im.get(teamTwo[number]).createKeyBag(true);
                im.get(teamTwo[number]).isKeyHolder = true;
                im.get(teamTwo[number]).addKeyPart(keys.get(2));

                kvm.get(teamTwo[number]).create();
                kvm.get(teamTwo[number]).addKey(keys.get(1).getKeyActor());
            }

            if(teamTwo[2 - number - 1] !=  null) {
                im.get(teamTwo[2 - number - 1]).createKeyBag(false);
                im.get(teamTwo[2 - number - 1]).keyBag = im.get(teamTwo[number]).keyBag;

                kvm.get(teamTwo[2 - number - 1]).create();
                kvm.get(teamTwo[2 - number - 1]).addKey(keys.get(1).getKeyActor());
            }
        }

        keys.clear();
    }

    //the keys are shown as part of the ui
    public void drawKeys()
    {
        /*for(int x= 0; x < characters.size(); x++)
            System.out.println("TeamId: " + characters.get(x).getComponent(TeamComponent.class).TeamId);

        System.out.println("Size: " + characters.size());*/

        for(Entity character : characters)
        {
            if(character != null)
                kvm.get(character).render();
        }
    }

    /*
    hier werden die rectangles von schüssel und charakteren geprüft.
    Ich muss es so erweitern, dass nur der Schlüsselträger sie aufnehmen kann
    intersector.overlaps(rect 1, rect2) deutet die Kollision an
     */
    public void addKey(OrthogonalTiledMapRendererWithPlayers renderer)
    {
        Intersector intersector = new Intersector();
       for(Entity character : characters)
       {
           for(Key key : renderer.getKeys())
           {
               if(intersector.overlaps(key.getCollider(), iam.get(character).area))
               {
                   if(hm.get(character).HP != 0) {
                       im.get(character).addKeyPart(key);
                       kvm.get(character).addKey(key.getKeyActor());
                       // Open treasure room, when character gains all three key parts
                       if (im.get(character).getKeyBag().size() == 3)
                           maze.openTreasureRoom();

                       //updateKeyBags(tm.get(character).TeamId);
                       renderer.getKeys().remove(key);
                       //send to server
                       nm.get(character).takeKey(key.getKeySection());
                   }
               }
           }
       }

    }

    //die schlüssel vom schlüsselträger werden auf dem Partner übertragen
    //es werden dann auch die schlüssel zum keyviewercomponent hinzugefügt,
    //damit man es auf der Ui sehen kann
    public void updateKeyBags()
    {
        //Entity gameMaster = new Entity();
        Entity[] teamOne = new Entity[2];
        Entity[] teamTwo = new Entity[2];

        int x = 0;
        int y = 0;

        for(Entity character : characters)
        {
            switch(tm.get(character).TeamId)
            {
                //case 0: gameMaster = character; break;
                case 1: teamOne[x] = character; x++; break;
                case 2: teamTwo[y] = character; y++; break;
            }
        }

        /*if(!(gameMaster.equals(null))) {
            if(!im.get(gameMaster).getKeyBag().isEmpty()) {
                for (Key key : im.get(gameMaster).getKeyBag()) {
                    kvm.get(gameMaster).addKey(key.getKeyActor());
                }
            }
        }*/

        //System.out.println(teamOne[0]);
        if(teamOne[0] != null)
        {
            if(im.get(teamOne[0]).isKeyHolder) {
                if (teamOne[1] != null) {
                    im.get(teamOne[1]).keyBag = im.get(teamOne[0]).getKeyBag();
                    for (Key key : im.get(teamOne[1]).getKeyBag()) {
                        //hier wird es dann zur ui hinzugefügt
                        kvm.get(teamOne[1]).addKey(key.getKeyActor());
                    }
                }
            }
        }
        else if(teamOne[1] != null)
        {
            if(im.get(teamOne[1]).isKeyHolder) {
                if (teamOne[0] != null) {
                    im.get(teamOne[0]).keyBag = im.get(teamOne[1]).getKeyBag();
                    for (Key key : im.get(teamOne[0]).getKeyBag()) {
                        kvm.get(teamOne[0]).addKey(key.getKeyActor());
                    }
                }
            }
        }

        if(teamTwo[0] != null)
        {
            if(im.get(teamTwo[0]).isKeyHolder) {
                if (teamTwo[1] != null) {
                    im.get(teamTwo[1]).keyBag = im.get(teamTwo[0]).getKeyBag();
                    for (Key key : im.get(teamTwo[1]).getKeyBag()) {
                        kvm.get(teamTwo[1]).addKey(key.getKeyActor());
                    }
                }
            }
        }
        else if(teamTwo[1] != null)
        {
            if(im.get(teamTwo[1]).isKeyHolder) {
                if (teamTwo[0] != null) {
                    im.get(teamTwo[0]).keyBag = im.get(teamTwo[1]).getKeyBag();
                    for (Key key : im.get(teamOne[0]).getKeyBag()) {
                        kvm.get(teamTwo[0]).addKey(key.getKeyActor());
                    }
                }
            }
        }


    }

    /*
    ein sehr banaler Ausweg f�r die letzte Minute war, ein Liste im OrthoganlTiledMapRendererWithPlayers
    einzuf�gen, f�r die beta werde ich mir eine neue klasse daf�r ausdenken, um die items zu rendern
    hier werden die charaktere nach ihren hp�s gefragt, ob diese 0 ist. dann sieht man nach, ob der spieler
    der schl�sseltr�ger ist. Falls der Tr�ger Schl�sselteile hat, verliert er diese und werden zum der liste
    im Renderer �bertragen
    */
    public void loseKeys(OrthogonalTiledMapRendererWithPlayers renderer)
    {
        List<Key> keys;
        for(Entity character : characters)
        {
            if(hm.get(character).HP == 0)
            {
                System.out.println("Enter hp");
                if(im.get(character).isKeyHolder)
                {
                    System.out.println("Enter keyholder");
                    maze.lockTreasureRoom();    // lock treasure room again
                    System.out.println(im.get(character).getKeyBag().size());
                    if(im.get(character).getKeyBag().size() != 0)
                    {
                        keys = im.get(character).loseKeys();
                        kvm.get(character).removeKeys();
                        //System.out.println("Anzahl Schlüssel: " + keys.size());
                        for(Key key : keys)
                        {
                            /* key wird jetzt bei netzwerkbenachrichtigung erst sichtbar
                            key.getSprite().visible = true;
                            key.getSprite().setPosition(dbm.get(character).dynamicBody.getPosition().x + 1f, dbm.get(character).dynamicBody.getPosition().y);
                            key.getCollider().setPosition(key.getSprite().getX(), key.getSprite().getY());
                            renderer.getKeys().add(key);*/
                            nm.get(character).loseKey(key.getKeySection(), dbm.get(character).dynamicBody.getPosition().x + 1f, dbm.get(character).dynamicBody.getPosition().y);
                            //System.out.println(renderer.getKeys().size());
                        }

                    }
                }
            
            nm.get(character).sendHPUpdate(new HPUpdateRequest(idm.get(character).id, hm.get(character).initialHP));
            hm.get(character).HP = hm.get(character).initialHP;
            dbm.get(character).resetToStartPosition();
            }
        }

        //ab hier verlieren die partner ihre schlüssel, derzeitig nur vom client aus
        Entity[] teamOne = new Entity[2];
        Entity[] teamTwo = new Entity[2];
        int x = 0, y = 0;

        for(Entity character : characters)
        {
            switch(tm.get(character).TeamId)
            {
                case 1: teamOne[x] = character; x++; break;
                case 2: teamTwo[y] = character; y++; break;
            }
        }

        if(teamOne[0] != null) {
            if (im.get(teamOne[0]).isKeyHolder && im.get(teamOne[0]).getKeyBag().size() == 0) {
                im.get(teamOne[1]).loseKeys();
                kvm.get(teamOne[1]).removeKeys();
            }
        }

        if(teamOne[1] != null) {
            if (im.get(teamOne[1]).isKeyHolder && im.get(teamOne[1]).getKeyBag().size() == 0) {
                im.get(teamOne[0]).loseKeys();
                kvm.get(teamOne[0]).removeKeys();
            }
        }

        if(teamTwo[0] != null) {
            if (im.get(teamTwo[0]).isKeyHolder && im.get(teamTwo[0]).getKeyBag().size() == 0) {
                im.get(teamTwo[1]).loseKeys();
                kvm.get(teamTwo[1]).removeKeys();
            }
        }

        if(teamTwo[1] != null) {
            if (im.get(teamTwo[1]).isKeyHolder && im.get(teamTwo[1]).getKeyBag().size() == 0) {
                im.get(teamTwo[0]).loseKeys();
                kvm.get(teamTwo[0]).removeKeys();
            }
        }


    }


    public List<Key> createKeys()
    {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("textures/spritesheets/KeySections/keyAtlas.pack"));
        Key keyPartOne = new Key(KeySection.PartOne, atlas.findRegion("PartOne"), new KeyActor(new Texture(Gdx.files.internal("textures/User Interface/KeyPartOne.png")), KeySection.PartOne));
        Key keyPartTwo = new Key(KeySection.PartTwo, atlas.findRegion("PartTwo"), new KeyActor(new Texture(Gdx.files.internal("textures/User Interface/KeyPartTwo.png")), KeySection.PartTwo));
        Key keyPartThree = new Key (KeySection.PartThree, atlas.findRegion("PartThree"), new KeyActor(new Texture(Gdx.files.internal("textures/User Interface/KeyPartThree.png")), KeySection.PartThree));

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
        Long l = Math.round(number);
        int i = Integer.valueOf(l.intValue());
        i = i%2;
        return i;
    }

    public void updateDoors()
    {
        for(Entity character : characters)
        {
            if(im.get(character).getKeyBag().size() == 0)
                maze.lockTreasureRoom();

            if(im.get(character).getKeyBag().size() == 3)
                maze.openTreasureRoom();
        }
    }
}
