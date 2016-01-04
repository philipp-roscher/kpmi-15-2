package org.sausagepan.prototyp.view;

import java.util.Random;

import org.sausagepan.prototyp.KPMIPrototype;
import org.sausagepan.prototyp.enums.CharacterClass;
import org.sausagepan.prototyp.network.Network;
import org.sausagepan.prototyp.network.Network.MapInformation;
import org.sausagepan.prototyp.network.Network.NewHeroRequest;

import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class MainMenuScreen implements Screen {
	
	/* ........................................................ ATTRIBUTES .. */
	KPMIPrototype game;
	public OrthographicCamera camera;
	public Viewport viewport;
	private Texture bgImg;

	private int connectionStatus;
	private World world;
    private RayHandler rayHandler;
    private MapInformation mapInformation;
	String serverIp;

	private boolean heroRequestSent = false;
	private boolean mapInformationReceived = false;
	
	//chosen Player Class
	private CharacterClass clientClass;
	private boolean clientSel = false;
	private Texture SelArcherF;
	private Texture SelKnightM;
	private Texture SelFighterM;
	private Texture SelShamanM;
	private Texture SelDragonRed;
	private Texture SelNinjaF;
	private Texture SelWitchF;

    // UI
    private Stage stage;
    private Table table;
    private Skin skin;
    private final TextButton startButton, creditsButton, backButton;
    private final Label creditsArea;
	
	/* ...................................................... CONSTRUCTORS .. */
	public MainMenuScreen(KPMIPrototype game) {
		this.game  = game;
		this.world = new World(new Vector2(0,0), true);
        this.rayHandler = new RayHandler(world);
		camera = new OrthographicCamera();
		viewport = new FitViewport(800, 480, camera);
		viewport.apply();
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		connectionStatus = 0;
		
		this.bgImg = game.mediaManager.getMainMenuBackgroundImg();
		this.SelArcherF = game.mediaManager.getSelectionArcherFBig();
		this.SelDragonRed = game.mediaManager.getSelectionDragonRedBig();
		this.SelFighterM = game.mediaManager.getSelectionFighterMBig();
		this.SelKnightM = game.mediaManager.getSelectionKnightMBig();
		this.SelShamanM = game.mediaManager.getSelectionShamanMBig();
		this.SelNinjaF = game.mediaManager.getSelectionNinjaFBig();
		this.SelWitchF = game.mediaManager.getSelectionWitchFBig();

		game.client.addListener(new Listener() {
			public void received(Connection connection, Object object) {
				if (object instanceof MapInformation) {
					System.out.println("Received MapInformation");
					MainMenuScreen.this.mapInformation = (MapInformation) object;
					mapInformationReceived = true;
				}
			}

		});


		// Scene2d UI ........................................................................ UI */
        FitViewport fit = new FitViewport(800, 480);
        this.stage = new Stage(fit);
        this.skin = new Skin(Gdx.files.internal("UI/uiskin.json"));
        this.creditsArea = new Label("", skin, "default");
		this.creditsArea.setAlignment(Align.center, Align.center);
        this.creditsArea.setWrap(true);
        stage.addActor(creditsArea);
        this.startButton = new TextButton("Start Game", skin, "default");
        this.startButton.setWidth(128);
        this.startButton.setHeight(48);
        this.startButton.setPosition(400 - 64, 64);
        this.startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startConnection();
                startButton.setVisible(false);
            }
        });
        this.stage.addActor(this.startButton);
		this.creditsButton = new TextButton("Credits", skin, "default");
		this.creditsButton.setWidth(128);
		this.creditsButton.setHeight(48);
		this.creditsButton.setPosition(400 - 64, 12);
		this.creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                creditsArea.setVisible(true);
                creditsArea.setPosition(200,-800);
                startButton.setVisible(false);
                creditsButton.setVisible(false);
                backButton.setVisible(true);
            }
        });
		this.stage.addActor(this.creditsButton);

        backButton = new TextButton("Back", skin, "default");
        backButton.setWidth(128);backButton.setHeight(48);backButton.setPosition(400 - 64, 64);
        backButton.setVisible(false);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startButton.setVisible(true);
                creditsButton.setVisible(true);
                creditsArea.setVisible(false);
                backButton.setVisible(false);
            }
        });
        this.stage.addActor(backButton);
        setUpCredits();
        // Scene2d UI ........................................................................ UI */
	}


	/* ................................................................................................... METHODS .. */

	public void setUpGame() {
		game.setScreen(new InMaze(game, world, rayHandler, mapInformation, clientClass, game.TeamId));
	}

    public void startConnection() {
        // If screen is touched
        if(connectionStatus != 1) {
            Gdx.input.getTextInput(new TextInputListener() {

                @Override
                public void input(String text) {
                    // avoid connecting multiple times
                    if(connectionStatus == 1) return;

                    // trim IP to remove unnecessary whitepaces (sometimes created by android auto-correct)
                    text = text.trim();

                    Gdx.app.log("ServerConnector", "Attempting Connection to: "+ text);
                    try {
                        connectionStatus = 1;
                        game.client.connect(2000, text, Network.TCPPort, Network.UDPPort);
                        System.out.println("Established connection to "+text);

                        game.connected = true;
                    } catch (Exception e) {
                        System.out.println("Couldn't find running server at "+text);
                        e.printStackTrace();
                        connectionStatus = -1;
                        startButton.setVisible(true);
                    }
                }

                @Override
                public void canceled() {
                    startButton.setVisible(true);
                }

            }, "Bitte Server-IP eingeben", "127.0.0.1", "");
        }
    }

	public void showCreditsScreen() {
		this.startButton.setVisible(false);


	}


	//random chose Class for clients according to their TeamId
	public void randomClassSel() {

		game.batch.begin();
		//GameMaster
		if (game.TeamId == 0) {
			clientClass = CharacterClass.DRAGON;
		}
		//Teams
		else {
			Random ran = new Random();
			int x = ran.nextInt(6);                                        //TODO: raise number according to available character sheets!!!!

			//System.out.println("random number = "+x);

			switch (x) {
				case 0: {
					clientClass = CharacterClass.KNIGHT_M;
					break; }
				case 1: {
					clientClass = CharacterClass.ARCHER_F;
					break; }
				case 2: {
					clientClass = CharacterClass.SHAMAN_M;
					break; }
				case 3: {
					clientClass = CharacterClass.FIGHTER_M;
					break; }
				case 4: {
					clientClass = CharacterClass.WITCH_F;
					break; }
				case 5: {
					clientClass = CharacterClass.NINJA_F;
					break; }
				default: {
					clientClass = CharacterClass.KNIGHT_M;
					break; }
			}
		}
		game.batch.end();
		System.out.println("Chosen Client Class is: " + clientClass);
		clientSel = true;
	}

    /**
     * Manual player class selection when not playing as dragon
     */
	public  void manClassSel() {
		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);

		table.setDebug(false);		//TODO: auf false setzten wenn fertig
		//GameMaster
		if (game.TeamId == 0) {
			clientClass = CharacterClass.DRAGON;
			clientSel = true;
		}
		//Teams
		else {
			//skin for menu-buttons
			Skin mainMenuSkins = new Skin();
			mainMenuSkins.add("knight_m", SelKnightM);
			mainMenuSkins.add("archer_f", SelArcherF);
			mainMenuSkins.add("shaman_m", SelShamanM);
			mainMenuSkins.add("fighter_m", SelFighterM);
			mainMenuSkins.add("dragon_red", SelDragonRed);
			mainMenuSkins.add("witch_f", SelWitchF);
			mainMenuSkins.add("ninja_f", SelNinjaF);

			//make it a Drawable and then a ButtonStyle TODO different images for up,down.checked! (via .pack)
			Drawable drawKnightM = mainMenuSkins.getDrawable("knight_m");
			drawKnightM.setMinWidth(200f);
			drawKnightM.setMinHeight(200f);
			Drawable drawArcherF = mainMenuSkins.getDrawable("archer_f");
			drawArcherF.setMinWidth(200f);
			drawArcherF.setMinHeight(200f);
			Drawable drawShamanM = mainMenuSkins.getDrawable("shaman_m");
			drawShamanM.setMinWidth(200f);
			drawShamanM.setMinHeight(200f);
			Drawable drawFighterM = mainMenuSkins.getDrawable("fighter_m");
			drawFighterM.setMinWidth(200f);
			drawFighterM.setMinHeight(200f);
			Drawable drawNinjaF = mainMenuSkins.getDrawable("ninja_f");
			drawNinjaF.setMinWidth(200f);
			drawNinjaF.setMinHeight(200f);
			Drawable drawWitchF = mainMenuSkins.getDrawable("witch_f");
			drawWitchF.setMinWidth(200f);
			drawWitchF.setMinHeight(200f);
			
			Button.ButtonStyle knightmStyle = new Button.ButtonStyle(drawKnightM, drawKnightM, drawKnightM);
			Button.ButtonStyle archerfStyle = new Button.ButtonStyle(drawArcherF, drawArcherF, drawArcherF);
			Button.ButtonStyle ninjafStyle = new Button.ButtonStyle(drawNinjaF, drawNinjaF, drawNinjaF);
			Button.ButtonStyle witchfStyle = new Button.ButtonStyle(drawWitchF, drawWitchF, drawWitchF);
			Button.ButtonStyle fightermStyle = new Button.ButtonStyle(drawFighterM, drawFighterM, drawFighterM);
			Button.ButtonStyle shamanmStyle = new Button.ButtonStyle(drawShamanM, drawShamanM, drawShamanM);
			//buttons
			Button knightMButton = new Button();
			knightMButton.setStyle(knightmStyle);
			knightMButton.addListener(new CharacterButtonListener(CharacterClass.KNIGHT_M));

			Button archerFButton = new Button();
			archerFButton.setStyle(archerfStyle);
			archerFButton.addListener(new CharacterButtonListener(CharacterClass.ARCHER_F));

			Button ninjaFButton = new Button();
			ninjaFButton.setStyle(ninjafStyle);
			ninjaFButton.addListener(new CharacterButtonListener(CharacterClass.NINJA_F));
			
			Button witchFButton = new Button();
			witchFButton.setStyle(witchfStyle);
			witchFButton.addListener(new CharacterButtonListener(CharacterClass.WITCH_F));

			Button fighterMButton = new Button();
			fighterMButton.setStyle(fightermStyle);
			fighterMButton.addListener(new CharacterButtonListener(CharacterClass.FIGHTER_M));

			Button shamanMButton = new Button();
			shamanMButton.setStyle(shamanmStyle);
			shamanMButton.addListener(new CharacterButtonListener(CharacterClass.SHAMAN_M));

			table.add(knightMButton);
			table.add(archerFButton);
			table.add(fighterMButton);
			table.row();					//new row
			table.add(ninjaFButton);
			table.add(shamanMButton);
			table.add(witchFButton);

			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			stage.act(Gdx.graphics.getDeltaTime());
			stage.draw();
		}

	}


	/* ............................................................................................ libGDX METHODS .. */

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.batch.setProjectionMatrix(camera.combined);
		
		// Start collecting textures for OpenGL
		game.batch.begin();
			game.batch.draw(bgImg, 0, 0);
	
			if(connectionStatus == -1) {
			    game.font.setColor(1, 0, 0, 1);
				game.font.draw(game.batch, "Connection failed", 343, 350);
			    game.font.setColor(1, 1, 1, 1);
			}
			
			if(connectionStatus == 1) {
			    game.font.setColor(0, 1, 0, 1);
				game.font.draw(game.batch, "Connecting", 360, 350);
			    game.font.setColor(1, 1, 1, 1);
			}
		game.batch.end();


		if(game.connected && game.clientId != 0) {


			if (game.TeamAssignmentReceived) {

				//class selection
				if (!clientSel && game.clientCount <= game.maxClients) {
					//randomClassSel();
					manClassSel();
				}

				//send Hero/Client info to server after class Selection
				if(!heroRequestSent && clientSel && game.clientCount <= game.maxClients) {
					game.client.sendTCP(
							new NewHeroRequest(
									game.clientId,
									clientClass
							)
					);
					heroRequestSent = true;
				}
			}

			//too many players
			if(game.clientCount > game.maxClients) {
				game.batch.begin();
				game.font.setColor(1, 0, 0, 1);
				game.font.draw(game.batch, "Sorry, server is already full!"+game.clientCount+"/"+game.maxClients, 320, 380);
				game.font.setColor(1, 1, 1, 1);
				game.batch.end();
			} else {
                if (mapInformationReceived && heroRequestSent) {
                    setUpGame();
                }
            }
			dispose();
		}
		
		// Update camera
		camera.update();
        this.stage.draw();
        this.stage.act();

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this.stage);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        stage.getViewport().update(width, height);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	class CharacterButtonListener extends ClickListener {
		private CharacterClass characterClass;
		
		public CharacterButtonListener(CharacterClass characterClass) {
			this.characterClass = characterClass;
		}
		
		@Override
		public void clicked(InputEvent event, float x, float y) {
			System.out.print("selecting "+ characterClass);
			clientClass = characterClass;
			clientSel = true;
		}
	}

    public void setUpCredits() {
        creditsArea.setText(
                "MAZE\n" +
                        "\n" +
                        "by\n" +
                        "\n" +
                        "Sausage Pan\n" +
                        "\n" +
                        "\n" +
                        "Development Team\n" +
                        "\n" +
                        "Alexandra Krien\n" +
                        "Bettina Blasberg\n" +
                        "Georg Eckert\n" +
                        "Philipp Roscher\n" +
                        "Sara Gross\n" +
                        "\n" +
                        "\n" +
                        "Artwork\n" +
                        "\n" +
                        "Heros & Monsters\n" +
                        "by\n" +
                        "Sara Gross\n" +
                        "\n" +
                        "Dungeon Tileset\n" +
                        "by\n" +
                        "Georg Eckert\n" +
                        "based on\n" +
                        "Dungeon Tileset\n" +
                        "by\n" +
                        "Calciumtrice"
        );
        creditsArea.setVisible(true);
        creditsArea.setPosition(200, -800, Align.center);
        creditsArea.setWidth(400);creditsArea.setHeight(800);
        Action scrollCredits = Actions.moveBy(0,1);
        scrollCredits.setActor(creditsArea);
        creditsArea.addAction(Actions.forever(scrollCredits));

        Action resetCredits = new Action() {
            @Override
            public boolean act(float delta) {
                if(actor.getY() > 400) actor.setY(-800);
                return true;
            }
        };
        resetCredits.setActor(creditsArea);
        creditsArea.addAction(Actions.forever(resetCredits));
        creditsArea.setVisible(false);
    }
}
