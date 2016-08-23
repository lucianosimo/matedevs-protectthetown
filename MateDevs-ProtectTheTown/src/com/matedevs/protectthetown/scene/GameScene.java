package com.matedevs.protectthetown.scene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseElasticInOut;
import org.andengine.util.modifier.ease.EaseElasticOut;
import org.andengine.util.modifier.ease.IEaseFunction;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.games.Games;
import com.matedevs.protectthetown.base.BaseScene;
import com.matedevs.protectthetown.manager.SceneManager;
import com.matedevs.protectthetown.manager.SceneManager.SceneType;
import com.matedevs.protectthetown.object.Bomb;
import com.matedevs.protectthetown.object.Floor;
import com.matedevs.protectthetown.object.House;
import com.matedevs.protectthetown.object.LargeHouse;
import com.matedevs.protectthetown.object.LargeRock;
import com.matedevs.protectthetown.object.Repair;
import com.matedevs.protectthetown.object.Rock;
import com.matedevs.protectthetown.object.Satelite;
import com.matedevs.protectthetown.object.Shield;
import com.matedevs.protectthetown.object.Shot;
import com.matedevs.protectthetown.object.SmallHouse;
import com.matedevs.protectthetown.object.SmallRock;
import com.matedevs.protectthetown.object.Ufo;
import com.matedevs.protectthetown.pools.ExplosionPool;
import com.matedevs.protectthetown.pools.SmallExplosionPool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class GameScene extends BaseScene{
	
	//Scene indicators
	private HUD gameHud;
	
	//Physics world variable
	private PhysicsWorld physicsWorld;
	
	//HUD sprites
	private Sprite soundDisabled;
	private Sprite soundButton;
	private Sprite gameScore;
	private TiledSprite[] gameScoreTiles;
	private TiledSprite[] currentScore;
	private TiledSprite[] finalScore;
	
	//Constants	
	private float screenWidth;
	private float screenHeight;
	
	//Instances
	private SmallHouse smallHouse;
	private House house;
	private LargeHouse largeHouse;
	
	//Booleans
	private boolean availablePause = false;
	private boolean gameOver = false;
	private boolean destroyAllEnemies = false;
	private boolean domeActivated = false;
	private boolean scoreChanged = false;
	
	//Integers
	private int score = 0;
	private int previousHighScore = 0;
	
	//Windows
	private Sprite helpWindow;
	private Sprite gameOverWindow;
	private Sprite pauseWindow;
	private Sprite newRecord;
	
	//Buttons
	private Sprite resumeButton;
	private Sprite retryButton;
	private Sprite quitButton;
	private Sprite pauseButton;
	//private Sprite submitScoreButton;
	private Sprite twitterButton;
	
	//Dome
	private Sprite dome;
	private Sprite shieldBarFrame;
	private Sprite shieldBarLogo;
	
	//Rectangle
	private Rectangle fade;
	private Rectangle shieldBar;
	
	//Countdown
	private Sprite countdownFrame1;
	private Sprite countdownFrame2;
	private Sprite countdownFrame3;
	private Sprite countdownFrame4;
	
	//Counters
	private int firstGame = 0;
	
	//Rocks
	private LargeRock largeRock1;
	private LargeRock largeRock2;
	private LargeRock largeRock3;
	
	private ArrayList<Rock> rockList;
	private ArrayList<SmallRock> smallRockList;
	
	//Satelite
	private Satelite satelite;
	
	//Ufos
	private Ufo ufo1;
	private Ufo ufo2;
	
	//Boxes
	private Bomb bombBox;
	private Repair repairBox;
	private Shield shieldBox;
	
	//Pools
	private ExplosionPool explosionPool;
	private SmallExplosionPool smallExplosionPool;
	
	//Explosions
	private AnimatedSprite explosion;
	private AnimatedSprite small_explosion;
	
	//Ease functions
	private static final IEaseFunction[][] EASEFUNCTIONS = new IEaseFunction[][] {
		new IEaseFunction[] { 
				EaseElasticInOut.getInstance(),
				EaseElasticOut.getInstance()},
	};

	//Variables	
	private final static int HELP_WINDOW_MOVE_MODIFIER_DURATION_MILISECONDS = 3;
	private final static int HELP_WINDOW_INITIAL_Y = 1100;
	
	private final static int BASE_FLOOR_POSITION_Y = -200;
	private final static int FLOOR_FIRST_POSITION_X = 80;
	private final static int FLOOR_FIRST_POSITION_Y = 35;
	private final static int FLOOR_DISTANCE_BETWEEN = 160;
	
	private static final int ROCK_POSITIVE_VEL_X = 1;
	private static final int ROCK_NEGATIVE_VEL_X = -1;
	
	private static final int ROCK_INITIAL_Y = 805;
	private static final int BOX_INITIAL_Y = 900;
	private static final int UFO_INITIAL_Y = 600;
	private static final int SATELITE_INITIAL_Y = 1500;
	
	private static final int LARGE_ROCK_MAX_RANDOM_Y_VEL = 2;
	private static final int LARGE_ROCK_MIN_RANDOM_Y_VEL = 1;
	
	private static final int ROCK_MAX_RANDOM_Y_VEL = 2;
	private static final int ROCK_MIN_RANDOM_Y_VEL = 1;
	
	private static final int SMALL_ROCK_MAX_RANDOM_Y_VEL = 3;
	private static final int SMALL_ROCK_MIN_RANDOM_Y_VEL = 2;
	
	private static final int VELOCITY_MULTIPLIER_MAX_RANDOM = 5;
	private static final int VELOCITY_MULTIPLIER_MIN_RANDOM = 4;
	
	private static final int ROCK_MAX_RANDOM_X = 840;
	private static final int ROCK_MIN_RANDOM_X = 400;
	
	private static final int LARGE_ROCK_SCORE = 100;
	private static final int ROCK_SCORE = 250;
	private static final int SMALL_ROCK_SCORE = 500;
	private static final int UFO_SCORE = 1000;
	private static final int SATELITE_SCORE = 1000;
	
	private static final int START_GAME_UPDATES = 280;
	private static final int LARGE_ROCK_CREATION_UPDATES = 500;
	
	private static final float SHIELD_DURATION = 10f;
	
	//If negative, never collides between groups, if positive yes
	//private static final int GROUP_ENEMY = -1;

	@Override
	public void createScene() {
		screenWidth = resourcesManager.camera.getWidth();
		screenHeight = resourcesManager.camera.getHeight();
		createBackground();
		createPhysics();
		createHud();
		createWindows();
		initializeGame();
		//DebugRenderer debug = new DebugRenderer(physicsWorld, vbom);
        //GameScene.this.attachChild(debug);
	}
	
	private void initializeGame() {
		createFloor();
		createHouses();
		createDecoration();
		createDome();
		createUfos();
		createRocks();
		createSatelite();
		createBoxes();
		createCountdown();
		firstGame();
		checkSoundStatus();
		createExplosionsPools();
		
		loadAds();
		
		engine.registerUpdateHandler(new IUpdateHandler() {
			private int updates = 0;
			private int difficulty = 0;
			
			@Override
			public void reset() {

			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				if (helpWindow.isVisible()) {
					updates = 0;
				}
				Random rand = new Random();
				int randomBox;
				updates++;
				
				if (updates < START_GAME_UPDATES) {
					availablePause = false;
				}
				
				if (difficulty < (LARGE_ROCK_CREATION_UPDATES - 50) && (updates % 750) == 0) {
					difficulty += 50;
				}

				if (updates == 70) {
					countdownFrame1.setVisible(false);
					countdownFrame2.setVisible(true);
				}
				if (updates == 140) {
					countdownFrame2.setVisible(false);
					countdownFrame3.setVisible(true);
				}
				if (updates == 210) {
					countdownFrame3.setVisible(false);
					countdownFrame4.setVisible(true);
				}
				if (updates == START_GAME_UPDATES) {
					countdownFrame4.setVisible(false);
					availablePause = true;
					largeRock1.getLargeRockBody().setActive(true);
				}
				
				if ((updates % 250) == 0 && availablePause) {
					if (!largeRock1.getLargeRockBody().isActive()) {
						largeRock1.getLargeRockBody().setActive(true);
					}					
				}
				
				if ((updates > 2500) && (updates % 250) == 0 && availablePause) {
					if (!largeRock2.getLargeRockBody().isActive()) {
						largeRock2.getLargeRockBody().setActive(true);
					}					
				}
				
				if ((updates > 7500) && (updates % 250) == 0 && availablePause) {
					if (!largeRock3.getLargeRockBody().isActive()) {
						largeRock3.getLargeRockBody().setActive(true);
					}					
				}
				
				if ((updates > 1500) && (updates % 250) == 0 && availablePause) {
					if (!ufo1.getUfoBody().isActive()) {
						ufo1.getUfoBody().setActive(true);
					}
				}
				
				if ((updates > 4500) && (updates % 400) == 0 && availablePause) {
					if (!ufo2.getUfoBody().isActive()) {
						ufo2.getUfoBody().setActive(true);
					}
				}
				
				if ((updates > 3500) && (updates % 750) == 0 && availablePause) {
					if (!satelite.getSateliteBody().isActive()) {
						regenerateSatelite(satelite.getSateliteBody());
						satelite.getSateliteBody().setActive(true);
					}
				}
				
				if ((updates > 1500) && (updates % 750) == 0 && availablePause) {
					//n = rand.nextInt(max - min + 1) + min;
					randomBox = rand.nextInt(3) + 1;
					
					switch (randomBox) {
						case 1:
							if (!bombBox.getBombBody().isActive()) {
								bombBox.getBombBody().setActive(true);
							}
							break;
						case 2:
							if (!repairBox.getRepairBody().isActive()) {
								repairBox.getRepairBody().setActive(true);
							}
							break;
						case 3:
							if (!shieldBox.getShieldBody().isActive()) {
								shieldBox.getShieldBody().setActive(true);
							}
							break;
						default:
							break;
					}
				}
				
				if (domeActivated && availablePause) {
					if (shieldBar.getWidth() > 0) {
						shieldBar.setSize(shieldBar.getWidth() - pSecondsElapsed * 18.5f, shieldBar.getHeight());
					}
					shieldBar.setPosition((screenWidth/2 + screenWidth/4 - 190) + shieldBar.getWidth() / 2, shieldBar.getY());
				}
			}
		});
	}
	
	private void loadAds() {

		if (!Chartboost.hasInterstitial(CBLocation.LOCATION_GAMEOVER)) {
			Chartboost.cacheInterstitial(CBLocation.LOCATION_GAMEOVER);
		}
		
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (!activity.getAdmobInterstitialAd().isLoaded()) {
					AdRequest adRequest = new AdRequest.Builder()
			                  .build();
					
					activity.getAdmobInterstitialAd().loadAd(adRequest);
				}
				
			}
		});
	}
	
	private void checkSoundStatus() {
		//If soundEnabled = 0, enabled..if 1 disabled
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		int soundEnabled = sharedPreferences.getInt("soundEnabled", 0);
		
		if (soundEnabled == 1) {
			activity.enableSound(false);
		} else if (soundEnabled == 0) {
			activity.enableSound(true);
		}
	}
	
	private void createExplosionsPools() {
		explosionPool = new ExplosionPool(resourcesManager.game_explosion_region, vbom, GameScene.this);
		smallExplosionPool = new SmallExplosionPool(resourcesManager.game_small_explosion_region, vbom, GameScene.this);
	}
	
	/*
	 * Creates floor on level generation
	 */
	private void createFloor() {
		Floor[] floor = new Floor[8];
 		
		Sprite base_floor = new Sprite(screenWidth/2, BASE_FLOOR_POSITION_Y, resourcesManager.game_base_floor_region, vbom);
		Body base_floor_body = PhysicsFactory.createBoxBody(physicsWorld, base_floor, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		
		base_floor_body.setUserData("base_floor");
		base_floor.setCullingEnabled(true);
		
		GameScene.this.attachChild(base_floor);
		
		for (int i = 0; i < floor.length; i++) {
			floor[i] = new Floor(i * FLOOR_DISTANCE_BETWEEN + FLOOR_FIRST_POSITION_X, FLOOR_FIRST_POSITION_Y, vbom, camera, physicsWorld);
			floor[i].setCullingEnabled(true);
			GameScene.this.attachChild(floor[i]);
		}
	}
	
	public void firstGame() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		firstGame = sharedPreferences.getInt("firstGame", 0);
		if (firstGame == 0) {
			Editor editor = sharedPreferences.edit();
			firstGame++;
			editor.putInt("firstGame", firstGame);
			editor.commit();
			displayHelpWindow();			
		}
	}
	
	private void displayHelpWindow() {
		final IEaseFunction[] easeFunction = EASEFUNCTIONS[0];
		
		fade = new Rectangle(screenWidth/2, screenHeight/2, screenWidth, screenHeight, vbom);
		helpWindow.setPosition(screenWidth/2, 1500);
		
		camera.setChaseEntity(null);
        availablePause = false;
		
        gameHud.setVisible(false);
		helpWindow.setVisible(true);
		
		helpWindow.registerEntityModifier(new MoveModifier(HELP_WINDOW_MOVE_MODIFIER_DURATION_MILISECONDS, screenWidth/2, 
				HELP_WINDOW_INITIAL_Y, screenWidth/2, screenHeight/2, easeFunction[0]));
		
		fade.setColor(Color.BLACK);
		fade.setAlpha(0.35f);
		
		GameScene.this.attachChild(fade);
		GameScene.this.attachChild(helpWindow);
        GameScene.this.registerTouchArea(helpWindow);
	}
	
	private void destroySatelite(Satelite sat) {
		regenerateSatelite(sat.getSateliteBody());
		sat.getSateliteBody().setActive(false);
		createExplosion(sat.getX(), sat.getY());
	}
	
	private void destroyUfo(Ufo ufo) {
		regenerateUfo(ufo.getUfoBody());
		ufo.getUfoBody().setActive(false);
		createExplosion(ufo.getX(), ufo.getY());
	}
	
	private void destroyLargeRock(LargeRock largeRock) {
		regenerateRocks(largeRock.getLargeRockBody());
		largeRock.getLargeRockBody().setActive(false);
		createExplosion(largeRock.getX(), largeRock.getY());
	}
	
	private void destroyRock(Rock rock) {
		createExplosion(rock.getX(), rock.getY());
		
		rock.setVisible(false);
		rock.getRockBody().setActive(false);
		rock.setIgnoreUpdate(true);

		rockList.add(rock);
	}
	
	private void destroySmallRock(SmallRock smallRock) {
		createSmallExplosion(smallRock.getX(), smallRock.getY());
		
		smallRock.setVisible(false);
		smallRock.getSmallRockBody().setActive(false);
		smallRock.setIgnoreUpdate(true);

		smallRockList.add(smallRock);
		
	}
	
	private void createBoxes() {
		createBombBox();
		createRepairBox();
		createShieldBox();
	}
	
	private void createRocks() {
		largeRock1 = createLargeRock();
		largeRock2 = createLargeRock();
		largeRock3 = createLargeRock();
		
		rockList = new ArrayList<Rock>();
		smallRockList = new ArrayList<SmallRock>();
		
		for (int i = 0; i < 10; i++) {
			Rock rock = getNewRock();
			addRockToArrayList(rock);
		}
		
		for (int i = 0; i < 20; i++) {
			SmallRock smallRock = getNewSmallRock();
			addSmallRockToArrayList(smallRock);
		}
	}
	
	private void addRockToArrayList(Rock rock) {
		GameScene.this.attachChild(rock);
		GameScene.this.registerTouchArea(rock);
		rock.setCullingEnabled(true);
		
		rockList.add(rock);
	}
	
	private void addSmallRockToArrayList(SmallRock smallRock) {
		GameScene.this.attachChild(smallRock);
		GameScene.this.registerTouchArea(smallRock);
		smallRock.setCullingEnabled(true);
		
		smallRockList.add(smallRock);
	}
	
	private Rock getNewRock() {
		Rock rock = new Rock(10000, 10000, vbom, camera, physicsWorld) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if (destroyAllEnemies && this.getRockBody().isActive() && availablePause && !gameOver) {
					if (this.getX() > 0 && this.getX() < 1280 && this.getY() > 0 && this.getY() < 720) {
						final Rock ref = this;
						engine.runOnUpdateThread(new Runnable() {
							
							@Override
							public void run() {
								addScore(ROCK_SCORE);
								destroyRock(ref);
							}
						});
					}
				}
				
				if (this.getX() < 0) {
					regenerateRocks(this.getRockBody());
				}
				
				if (this.collidesWith(dome) && this.getRockBody().isActive()) {
					final Rock ref = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							addScore(ROCK_SCORE);
							destroyRock(ref);
						}
					});
				}
			}
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final Rock ref = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							if (ref.getRockBody().isActive() && availablePause && !gameOver) {
								createSmallRockFromRock(ref.getX() + 5, ref.getY(), ROCK_POSITIVE_VEL_X);
								createSmallRockFromRock(ref.getX() - 5, ref.getY(), ROCK_NEGATIVE_VEL_X);
								addScore(ROCK_SCORE);
								destroyRock(ref);
							}										
						}
					});	
					
					setTouchAreaBindingOnActionDownEnabled(false);
					setTouchAreaBindingOnActionMoveEnabled(false);
				}
							
				return true;
			}
			
		};
		
		return rock;
	}
	
	private SmallRock getNewSmallRock() {
		SmallRock smallRock = new SmallRock(10000, 10000, vbom, camera, physicsWorld) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if (destroyAllEnemies && this.getSmallRockBody().isActive() && availablePause && !gameOver) {
					if (this.getX() > 0 && this.getX() < 1280 && this.getY() > 0 && this.getY() < 720) {
						final SmallRock ref = this;
						engine.runOnUpdateThread(new Runnable() {
							
							@Override
							public void run() {
								addScore(SMALL_ROCK_SCORE);
								destroySmallRock(ref);
							}
						});
					}
				}
				
				if (this.getX() < 0) {
					regenerateRocks(this.getSmallRockBody());
				}
				
				if (this.collidesWith(dome) && this.getSmallRockBody().isActive()) {
					final SmallRock ref = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							addScore(SMALL_ROCK_SCORE);
							destroySmallRock(ref);
						}
					});
				}
			}
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final SmallRock ref = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							if (ref.getSmallRockBody().isActive() && availablePause && !gameOver) {
								addScore(SMALL_ROCK_SCORE);
								destroySmallRock(ref);
							}
						}
					});
					
					setTouchAreaBindingOnActionDownEnabled(false);
					setTouchAreaBindingOnActionMoveEnabled(false);
				}
								
				return true;
			}

		};
		return smallRock;
	}
	
	private void createUfos() {
		ufo1 = createUfo();
		ufo2 = createUfo();
	}
	
	private void createSatelite() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		final int whichHouse = rand.nextInt(3) + 1;
		final float initialX;
		
		switch (whichHouse) {
			case 1:
				if (!smallHouse.isSmallHouseDestroyed()) {
					initialX = smallHouse.getX();
				} else if (!house.isHouseDestroyed()) {
					initialX = house.getX();
				} else {
					initialX = largeHouse.getX();
				}				
				break;
			case 2:
				if (!house.isHouseDestroyed()) {
					initialX = house.getX();
				} else if (!smallHouse.isSmallHouseDestroyed()) {
					initialX = smallHouse.getX();
				} else {
					initialX = largeHouse.getX();
				}
				break;
			case 3:
				if (!largeHouse.isLargeHouseDestroyed()) {
					initialX = largeHouse.getX();
				} else if (!smallHouse.isSmallHouseDestroyed()) {
					initialX = smallHouse.getX();
				} else {
					initialX = house.getX();
				}
				break;
			default:
				initialX = screenWidth/2;
				break;
		}
		
		satelite = new Satelite(initialX, SATELITE_INITIAL_Y, vbom, camera, physicsWorld) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if ((destroyAllEnemies && this.getSateliteBody().isActive() && availablePause && !gameOver) || 
						(this.collidesWith(dome) && this.getSateliteBody().isActive())) {
					
					if (this.getX() > 0 && this.getX() < screenWidth && this.getY() > 0 && this.getY() < screenHeight) {
						final Satelite ref = this;
						engine.runOnUpdateThread(new Runnable() {
							
							@Override
							public void run() {
								addScore(SATELITE_SCORE);
								destroySatelite(ref);
							}
						});
					}
				}
				
			}
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final Satelite ref = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							if (ref.getSateliteBody().isActive() && availablePause && !gameOver) {
								addScore(SATELITE_SCORE);
								destroySatelite(ref);
							}						
						}
					});
					
					setTouchAreaBindingOnActionDownEnabled(false);
					setTouchAreaBindingOnActionMoveEnabled(false);
				}				
				return true;
			}
		};

		satelite.getSateliteBody().setActive(false);
		satelite.setCullingEnabled(true);
		GameScene.this.attachChild(satelite);
		GameScene.this.registerTouchArea(satelite);
	}
	
	private Ufo createUfo() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		
		final int ufoLimit = 200;
		final int ufo_initial_x;
		
		final int ufoSpeed = rand.nextInt(3) + 5;	
		final int appereanceSide = rand.nextInt(2) + 1;
		final int ufoRandomRegion = rand.nextInt(3) + 1;
		
		final ITextureRegion ufoRegion;
		
		final Rectangle smallSensor = new Rectangle(smallHouse.getX(), screenHeight/2 , 1f, screenHeight, vbom);
		final Rectangle sensor = new Rectangle(house.getX(), screenHeight/2 , 1f, screenHeight, vbom);
		final Rectangle largeSensor = new Rectangle(largeHouse.getX(), screenHeight/2 , 1f, screenHeight, vbom);
		
		smallSensor.setVisible(false);
		sensor.setVisible(false);
		largeSensor.setVisible(false);
		
		switch (ufoRandomRegion) {
		case 1:
			ufoRegion = resourcesManager.game_ufo_1_region;
			break;
		case 2:
			ufoRegion = resourcesManager.game_ufo_2_region;
			break;
		case 3:
			ufoRegion = resourcesManager.game_ufo_3_region;
			break;
		default:
			ufoRegion = resourcesManager.game_ufo_1_region;
			break;
		}
		
		if (appereanceSide == 1) {
			ufo_initial_x = 1500;
		} else {
			ufo_initial_x = -250;
		}
		
		Ufo ufo = new Ufo(ufo_initial_x, UFO_INITIAL_Y, vbom, camera, physicsWorld, ufoRegion) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final Ufo ref = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							if (ref.getUfoBody().isActive() && availablePause && !gameOver) {
								addScore(UFO_SCORE);
								destroyUfo(ref);
							}
							
						}
					});
					
					setTouchAreaBindingOnActionDownEnabled(false);
					setTouchAreaBindingOnActionMoveEnabled(false);
				}
				
				return true;
			}
			
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if (destroyAllEnemies && this.getUfoBody().isActive() && availablePause && !gameOver) {
					if (this.getX() > 0 && this.getX() < 1280 && this.getY() > 0 && this.getY() < 720) {
						final Ufo ref = this;
						engine.runOnUpdateThread(new Runnable() {
							
							@Override
							public void run() {
								addScore(UFO_SCORE);
								destroyUfo(ref);
							}
						});
					}
				}
				
				if (this.collidesWith(dome)) {
					final Ufo ref = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							addScore(UFO_SCORE);
							destroyUfo(ref);
						}
					});
				}
				
				if (this.getX() > (screenWidth + ufoLimit)) {
					this.setUfoVelocityX(-ufoSpeed);
					smallSensor.setPosition(smallHouse.getX() - 50, screenHeight/2);
					sensor.setPosition(house.getX() - 50, screenHeight/2);
					largeSensor.setPosition(largeHouse.getX() - 50, screenHeight/2);
				} else if (this.getX() < (-ufoLimit)) {
					this.setUfoVelocityX(ufoSpeed);
					smallSensor.setPosition(smallHouse.getX() + 50, screenHeight/2);
					sensor.setPosition(house.getX() + 50, screenHeight/2);
					largeSensor.setPosition(largeHouse.getX() + 50, screenHeight/2);
				}
				
				if (this.getY() > UFO_INITIAL_Y + 100) {
					this.setUfoVelocityY(-5);
				} else if (this.getY() < UFO_INITIAL_Y - 100) {
					this.setUfoVelocityY(5);
				}
				
				if (this.collidesWith(smallSensor) && !smallHouse.isSmallHouseDestroyed()) {
					resourcesManager.shotSound.play();
					Shot shot = new Shot(this.getX(), this.getY(), vbom, camera, physicsWorld){
						protected void onManagedUpdate(float pSecondsElapsed) {
							if (this.collidesWith(dome)) {
								if (this.getX() > 0 && this.getX() < 1280 && this.getY() > 0 && this.getY() < 720) {
									final Shot ref = this;
									engine.runOnUpdateThread(new Runnable() {
										
										@Override
										public void run() {
											ref.setVisible(false);
											ref.getShotBody().setActive(false);
											ref.setIgnoreUpdate(true);
										}
									});
								}
							} 
						};
					};
					smallSensor.setPosition(10000, 10000);
					GameScene.this.attachChild(shot);
				}
				
				if (this.collidesWith(sensor) && !house.isHouseDestroyed()) {
					resourcesManager.shotSound.play();
					Shot shot = new Shot(this.getX(), this.getY(), vbom, camera, physicsWorld) {
						protected void onManagedUpdate(float pSecondsElapsed) {
							if (this.collidesWith(dome)) {
								if (this.getX() > 0 && this.getX() < 1280 && this.getY() > 0 && this.getY() < 720) {
									final Shot ref = this;
									engine.runOnUpdateThread(new Runnable() {
										
										@Override
										public void run() {
											ref.setVisible(false);
											ref.getShotBody().setActive(false);
											ref.setIgnoreUpdate(true);
										}
									});
								}
							} 
						};
					};
					sensor.setPosition(10000, 10000);
					GameScene.this.attachChild(shot);
				}
				
				if (this.collidesWith(largeSensor) && !largeHouse.isLargeHouseDestroyed()) {
					resourcesManager.shotSound.play();
					Shot shot = new Shot(this.getX(), this.getY(), vbom, camera, physicsWorld) {
						protected void onManagedUpdate(float pSecondsElapsed) {
							if (this.collidesWith(dome)) {
								if (this.getX() > 0 && this.getX() < 1280 && this.getY() > 0 && this.getY() < 720) {
									final Shot ref = this;
									engine.runOnUpdateThread(new Runnable() {
										
										@Override
										public void run() {
											ref.setVisible(false);
											ref.getShotBody().setActive(false);
											ref.setIgnoreUpdate(true);
										}
									});
								}
							} 
						};
					};
					largeSensor.setPosition(10000, 10000);
					GameScene.this.attachChild(shot);
				}
				
			}
		};
		
		ufo.setCullingEnabled(true);
		
		ufo.getUfoBody().setActive(false);
		
		GameScene.this.attachChild(smallSensor);
		GameScene.this.attachChild(sensor);
		GameScene.this.attachChild(largeSensor);
		GameScene.this.attachChild(ufo);
		
		GameScene.this.registerTouchArea(ufo);
		
		return ufo;
	}
	
	/*
	 * Creates a new large rock
	 */
	private LargeRock createLargeRock() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		final int x = rand.nextInt(ROCK_MAX_RANDOM_X - ROCK_MIN_RANDOM_X + 1) + ROCK_MIN_RANDOM_X;
		final float yVel = -(rand.nextInt(LARGE_ROCK_MAX_RANDOM_Y_VEL) + LARGE_ROCK_MIN_RANDOM_Y_VEL);
		
		LargeRock largeRock = new LargeRock(x, ROCK_INITIAL_Y, vbom, camera, physicsWorld) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				if (destroyAllEnemies && this.getLargeRockBody().isActive() && availablePause && !gameOver) {
					if (this.getX() > 0 && this.getX() < 1280 && this.getY() > 0 && this.getY() < 720) {
						final LargeRock ref = this;
						engine.runOnUpdateThread(new Runnable() {
							
							@Override
							public void run() {
								addScore(LARGE_ROCK_SCORE);
								destroyLargeRock(ref);
							}
						});
					}
				}
				if (this.collidesWith(dome) && this.getLargeRockBody().isActive()) {
					final LargeRock ref = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							addScore(LARGE_ROCK_SCORE);
							destroyLargeRock(ref);
						}
					});
				}
				
				if (this.getX() < 0) {
					regenerateRocks(this.getLargeRockBody());
				}
			}
			
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final LargeRock ref = this;
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							if (ref.getLargeRockBody().isActive() && availablePause && !gameOver) {
								createRockFromLargeRock(ref.getX() + 5, ref.getY(), ROCK_POSITIVE_VEL_X);
								createRockFromLargeRock(ref.getX() - 5, ref.getY(), ROCK_NEGATIVE_VEL_X);
								addScore(LARGE_ROCK_SCORE);
								destroyLargeRock(ref);
							}						
						}
					});
					
					setTouchAreaBindingOnActionDownEnabled(false);
					setTouchAreaBindingOnActionMoveEnabled(false);
				}
				
				return true;
			}
		};
		
		setRockDirection(x, largeRock.getLargeRockBody(), yVel);
		
		largeRock.getLargeRockBody().setActive(false);
		largeRock.setCullingEnabled(true);
		GameScene.this.attachChild(largeRock);
		GameScene.this.registerTouchArea(largeRock);
		
		return largeRock;
	}
	
	/*
	 * Creates a new rock when a large rock is destroyed
	 */
	private void createRockFromLargeRock(float x, float y, float xVel) {
		Random rand = new Random();
		final float yVel = -(rand.nextInt(ROCK_MAX_RANDOM_Y_VEL) + ROCK_MIN_RANDOM_Y_VEL);
		final float velocityMultiplier = rand.nextInt(VELOCITY_MULTIPLIER_MAX_RANDOM - VELOCITY_MULTIPLIER_MIN_RANDOM + 1) + VELOCITY_MULTIPLIER_MIN_RANDOM;
		
		if (rockList.size() == 0) {
			Rock rock = getNewRock();
			addRockToArrayList(rock);
		}
		
		Rock rock = rockList.get(rockList.size() - 1);
		rockList.remove(rockList.size() - 1);
		
		rock.setPosition(x, y);
		rock.getRockBody().setTransform(x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rock.getRockBody().getAngle());

		rock.setRockDirection(velocityMultiplier * xVel, yVel);
		
		rock.setVisible(true);
		rock.getRockBody().setActive(true);
		rock.setIgnoreUpdate(false);
		
		rock.setCullingEnabled(true);		
	}
	
	/*
	 * Creates a new small rock when a medium rock is destroyed
	 */
	private void createSmallRockFromRock(float x, float y, float xVel) {
		Random rand = new Random();
		final float yVel = -(rand.nextInt(SMALL_ROCK_MAX_RANDOM_Y_VEL) + SMALL_ROCK_MIN_RANDOM_Y_VEL);
		final float velocityMultiplier = rand.nextInt(VELOCITY_MULTIPLIER_MAX_RANDOM - VELOCITY_MULTIPLIER_MIN_RANDOM + 1) + VELOCITY_MULTIPLIER_MIN_RANDOM;
		
		if (smallRockList.size() == 0) {
			SmallRock smallRock = getNewSmallRock();
			addSmallRockToArrayList(smallRock);
		}
		
		SmallRock smallRock = smallRockList.get(smallRockList.size() - 1);
		smallRockList.remove(smallRockList.size() - 1);
		
		smallRock.setPosition(x, y);
		smallRock.getSmallRockBody().setTransform(x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, smallRock.getSmallRockBody().getAngle());

		smallRock.setSmallRockDirection(velocityMultiplier * xVel, yVel);
		
		smallRock.setVisible(true);
		smallRock.getSmallRockBody().setActive(true);
		smallRock.setIgnoreUpdate(false);
				
		smallRock.setCullingEnabled(true);	
	}
	
	/*
	 * Used to regenerate the rocks when they touch the floor.
	 */
	private void regenerateBoxes(Body boxBody) {
		Random rand = new Random();
		
		int positionIndex = rand.nextInt(4);
		int[] box_position = {100, 400, 800, 1200};
		
		boxBody.setTransform(box_position[positionIndex] / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (ROCK_INITIAL_Y + 250) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, boxBody.getAngle());
	}
	
	/*
	 * Used to regenerate the rocks when they touch the floor.
	 */
	private void regenerateRocks(Body rockBody) {
		Random rand = new Random();
		final int x = rand.nextInt(ROCK_MAX_RANDOM_X - ROCK_MIN_RANDOM_X + 1) + ROCK_MIN_RANDOM_X;
		final float yVel = -(rand.nextInt(ROCK_MAX_RANDOM_Y_VEL) + ROCK_MIN_RANDOM_Y_VEL);
		
		rockBody.setTransform(x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, (ROCK_INITIAL_Y + 50) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, rockBody.getAngle());
		if (x > screenWidth/2) {
			rockBody.setLinearVelocity(ROCK_POSITIVE_VEL_X, yVel);
		} else {
			rockBody.setLinearVelocity(ROCK_NEGATIVE_VEL_X, yVel);
		}
	}
	
	private void regenerateUfo(Body ufoBody) {
		Random rand = new Random();
		final int side = rand.nextInt(2) + 1;
		int x = 1580;
		
		switch (side) {
		case 1:
			x = 1580;
			break;
		case 2:
			x = -300;
			break;
		default:
			break;
		}
		
		ufoBody.setTransform(x / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, UFO_INITIAL_Y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, ufoBody.getAngle());
	}
	
	private void regenerateSatelite(Body sateliteBody) {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		final int whichHouse = rand.nextInt(3) + 1;
		final float initialX;
		int fallVelocity = -6;
		
		switch (whichHouse) {
			case 1:
				if (!smallHouse.isSmallHouseDestroyed()) {
					initialX = smallHouse.getX();
				} else if (!house.isHouseDestroyed()) {
					initialX = house.getX();
				} else {
					initialX = largeHouse.getX();
				}				
				break;
			case 2:
				if (!house.isHouseDestroyed()) {
					initialX = house.getX();
				} else if (!smallHouse.isSmallHouseDestroyed()) {
					initialX = smallHouse.getX();
				} else {
					initialX = largeHouse.getX();
				}
				break;
			case 3:
				if (!largeHouse.isLargeHouseDestroyed()) {
					initialX = largeHouse.getX();
				} else if (!smallHouse.isSmallHouseDestroyed()) {
					initialX = smallHouse.getX();
				} else {
					initialX = house.getX();
				}
				break;
			default:
				initialX = screenWidth/2;
				break;
		}
		
		sateliteBody.setLinearVelocity(0, fallVelocity);
		sateliteBody.setTransform(initialX / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, SATELITE_INITIAL_Y / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, sateliteBody.getAngle());
	}
	
	/*
	 * Creates houses on level generation
	 */
	private void createHouses() {
		ITextureRegion mediumHouseRegion;
		ITextureRegion largeHouseRegion;
				
		int smallHouseInitialX = 225;
		int mediumHouseInitialX = 1050;
		int largeHouseInitialX = 600;
		int smallHouseInitialY= 175;
		int mediumHouseInitialY = 175;
		int largeHouseInitialY = 175;
		
		final int healthBarWidth = 152;
		final int healthBarHeight = 15;
		
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		
		int whichMediumHouse = rand.nextInt(4) + 1;
		int whichLargeHouse = rand.nextInt(2) + 1;
		int smallOffsetY = rand.nextInt(50) + 1;
		int mediumOffsetY = rand.nextInt(50) + 1;
		int largeOffsetY = rand.nextInt(25) + 1;
		
		smallHouseInitialY = smallHouseInitialY - smallOffsetY;
		mediumHouseInitialY = mediumHouseInitialY - mediumOffsetY;
		largeHouseInitialY = largeHouseInitialY - largeOffsetY;
		
		switch (whichMediumHouse) {
		case 1:
			mediumHouseRegion = resourcesManager.game_house_1_region;
			break;
		case 2:
			mediumHouseRegion = resourcesManager.game_house_2_region;
			break;
		case 3:
			mediumHouseRegion = resourcesManager.game_house_3_region;
			break;
		case 4:
			mediumHouseRegion = resourcesManager.game_house_4_region;
			break;
		default:
			mediumHouseRegion = resourcesManager.game_house_1_region;
			break;
		}
		
		switch (whichLargeHouse) {
		case 1:
			largeHouseRegion = resourcesManager.game_large_house_1_region;
			break;
		case 2:
			largeHouseRegion = resourcesManager.game_large_house_2_region;
			break;
		default:
			largeHouseRegion = resourcesManager.game_large_house_1_region;
			break;
		}
		
		
		final int smallInitialX = 62;
		final int smallInitialY = -25;
		final int houseInitialX = 82;
		final int houseInitialY = -25;
		final int largeInitialX = 82;
		final int largeInitialY = -25;
		
		final Rectangle smallHouseHealthBarBackground = new Rectangle(smallInitialX, smallInitialY, healthBarWidth, healthBarHeight, vbom);
		final Rectangle smallHouseHealthBar = new Rectangle(smallInitialX, smallInitialY, healthBarWidth, healthBarHeight, vbom);
		final Rectangle houseHealthBarBackground = new Rectangle(houseInitialX, houseInitialY, healthBarWidth, healthBarHeight, vbom);
		final Rectangle houseHealthBar = new Rectangle(houseInitialX, houseInitialY, healthBarWidth, healthBarHeight, vbom);
		final Rectangle largeHouseHealthBarBackground = new Rectangle(largeInitialX, largeInitialY, healthBarWidth, healthBarHeight, vbom);
		final Rectangle largeHouseHealthBar = new Rectangle(largeInitialX, largeInitialY, healthBarWidth, healthBarHeight, vbom);
		
		final Sprite smallHealthBarFrame = new Sprite(smallInitialX, smallInitialY, resourcesManager.game_health_bar_frame_region, vbom);
		final Sprite healthBarFrame = new Sprite(houseInitialX, houseInitialY, resourcesManager.game_health_bar_frame_region, vbom);
		final Sprite largeHealthBarFrame = new Sprite(largeInitialX, largeInitialY, resourcesManager.game_health_bar_frame_region, vbom);
		
		houseHealthBarBackground.setColor(0.722f, 0.176f, 0.239f);
		houseHealthBar.setColor(0.514f, 0.729f, 0.188f);
		smallHouseHealthBarBackground.setColor(0.722f, 0.176f, 0.239f);
		smallHouseHealthBar.setColor(0.514f, 0.729f, 0.188f);
		largeHouseHealthBarBackground.setColor(0.722f, 0.176f, 0.239f);
		largeHouseHealthBar.setColor(0.514f, 0.729f, 0.188f);

		smallHouse = new SmallHouse(smallHouseInitialX, smallHouseInitialY, vbom, camera, physicsWorld) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				float energyWidthFactor = healthBarWidth / this.getMaxEnergy();
				if (this.isSmallHouseDestroyed() && this.getSmallHouseBody().isActive()) {
					this.setVisible(false);
					this.getSmallHouseBody().setActive(false);
					createExplosion(this.getX(), this.getY());
				}
				smallHouseHealthBar.setSize(this.getSmallHouseEnergy() * energyWidthFactor, smallHouseHealthBar.getHeight());
				smallHouseHealthBar.setPosition((this.getSmallHouseEnergy() * energyWidthFactor) / 2 - 13, smallHouseHealthBar.getY());
			}

			@Override
			public void onDie() {
				if (this.isSmallHouseDestroyed() && house.isHouseDestroyed() && largeHouse.isLargeHouseDestroyed()) {
					engine.runOnUpdateThread(new Runnable() {
						public void run() {
							if (!gameOver) {
								gameOver();
							}
						}
					});
				}
			}
		};
		
		largeHouse = new LargeHouse(largeHouseInitialX, largeHouseInitialY, vbom, camera, physicsWorld, largeHouseRegion, whichLargeHouse) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				float energyWidthFactor = healthBarWidth / this.getMaxEnergy();
				if (this.isLargeHouseDestroyed() && this.getLargeHouseBody().isActive()) {
					this.setVisible(false);
					this.getLargeHouseBody().setActive(false);
					createExplosion(this.getX(), this.getY());
				}
				largeHouseHealthBar.setSize(this.getLargeHouseEnergy() * energyWidthFactor, largeHouseHealthBar.getHeight());
				largeHouseHealthBar.setPosition((this.getLargeHouseEnergy() * energyWidthFactor) / 2 + 7, largeHouseHealthBar.getY());
			}

			@Override
			public void onDie() {
				if (this.isLargeHouseDestroyed() && house.isHouseDestroyed() && smallHouse.isSmallHouseDestroyed()) {
					engine.runOnUpdateThread(new Runnable() {
						public void run() {
							if (!gameOver) {
								gameOver();
							}							
						}
					});
				}
			}
		};
		
		house = new House(mediumHouseInitialX, mediumHouseInitialY, vbom, camera, physicsWorld, mediumHouseRegion, whichMediumHouse) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				super.onManagedUpdate(pSecondsElapsed);
				float energyWidthFactor = healthBarWidth / this.getMaxEnergy();
				if (this.isHouseDestroyed() && this.getHouseBody().isActive()) {
					this.setVisible(false);
					this.getHouseBody().setActive(false);
					createExplosion(this.getX(), this.getY());
				}
				houseHealthBar.setSize(this.getHouseEnergy() * energyWidthFactor, houseHealthBar.getHeight());
				houseHealthBar.setPosition((this.getHouseEnergy() * energyWidthFactor) / 2 + 7, houseHealthBar.getY());
			}

			@Override
			public void onDie() {
				if (this.isHouseDestroyed() && smallHouse.isSmallHouseDestroyed() && largeHouse.isLargeHouseDestroyed()) {
					engine.runOnUpdateThread(new Runnable() {
						public void run() {
							if (!gameOver) {
								gameOver();
							}
						}
					});					
				}
				
			}
		};
		
		house.attachChild(houseHealthBarBackground);
		house.attachChild(houseHealthBar);
		house.attachChild(healthBarFrame);
		
		smallHouse.attachChild(smallHouseHealthBarBackground);
		smallHouse.attachChild(smallHouseHealthBar);
		smallHouse.attachChild(smallHealthBarFrame);
		
		largeHouse.attachChild(largeHouseHealthBarBackground);
		largeHouse.attachChild(largeHouseHealthBar);
		largeHouse.attachChild(largeHealthBarFrame);
		
		GameScene.this.attachChild(house);
		GameScene.this.attachChild(smallHouse);
		GameScene.this.attachChild(largeHouse);
	}
	
	private void createDecoration() {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		
		int x = rand.nextInt(1281) + 1;
		int y = rand.nextInt(321) + 400;
		int cloudSpeed = -(rand.nextInt(11) + 35);
		int farCloudSpeed = -(rand.nextInt(11) + 10);
		int numberOfTrees = 6;
		int treeHeight = 75;
		
		int[] trees_positions = {80, 350, 425, 750, 850, 1200};
		ITextureRegion region = resourcesManager.game_trees_1_region;
		Sprite treeSprite = new Sprite(0, 0, region.deepCopy(), vbom);
		
		for (int i = 0; i < numberOfTrees; i++) {
			
			int tree = rand.nextInt(6);
			int treeOffsetX = rand.nextInt(51) - 25;
			int treeOffsetY = rand.nextInt(101);
			
			switch (tree) {
			case 0:
				region = resourcesManager.game_trees_1_region;
				break;
			case 1:
				region = resourcesManager.game_trees_2_region;
				break;
			case 2:
				region = resourcesManager.game_trees_3_region;
				break;
			case 3:
				region = resourcesManager.game_trees_4_region;
				break;
			case 4:
				region = resourcesManager.game_trees_5_region;
				break;
			case 5:
				region = resourcesManager.game_trees_6_region;
				break;
			default:
				break;
			}
			treeSprite = new Sprite(trees_positions[i] + treeOffsetX, treeHeight + treeOffsetY, region.deepCopy(), vbom);
			GameScene.this.attachChild(treeSprite);
		}
		
			Sprite cloud1 = new Sprite(x, y, resourcesManager.game_cloud_1_region, vbom) {
				protected void onManagedUpdate(float pSecondsElapsed) {
					super.onManagedUpdate(pSecondsElapsed);
					if (this.getX() < -200) {
						this.setPosition(1480, this.getY());
					}
				};
			};
			PhysicsHandler handler = new PhysicsHandler(cloud1);
			cloud1.registerUpdateHandler(handler);
			handler.setVelocity(cloudSpeed, 0);
			
			x = rand.nextInt(1281) + 1;
			y = rand.nextInt(321) + 400;
			cloudSpeed = -(rand.nextInt(11) + 35);
			
			Sprite cloud2 = new Sprite(x, y, resourcesManager.game_cloud_2_region, vbom) {
				protected void onManagedUpdate(float pSecondsElapsed) {
					super.onManagedUpdate(pSecondsElapsed);
					if (this.getX() < -200) {
						this.setPosition(1480, this.getY());
					}
				};
			};
			PhysicsHandler handler2 = new PhysicsHandler(cloud2);
			cloud1.registerUpdateHandler(handler2);
			handler2.setVelocity(cloudSpeed, 0);
			
			x = rand.nextInt(1281) + 1;
			y = rand.nextInt(321) + 400;
			
			Sprite farCloud1 = new Sprite(x, y, resourcesManager.game_far_cloud_1_region, vbom) {
				protected void onManagedUpdate(float pSecondsElapsed) {
					super.onManagedUpdate(pSecondsElapsed);
					if (this.getX() < -200) {
						this.setPosition(1480, this.getY());
					}
				};
			};
			PhysicsHandler handler4 = new PhysicsHandler(farCloud1);
			farCloud1.registerUpdateHandler(handler4);
			handler4.setVelocity(farCloudSpeed, 0);
			
			x = rand.nextInt(1281) + 1;
			y = rand.nextInt(321) + 400;
			farCloudSpeed = -(rand.nextInt(11) + 10);
			
			Sprite farCloud2 = new Sprite(x, y, resourcesManager.game_far_cloud_2_region, vbom) {
				protected void onManagedUpdate(float pSecondsElapsed) {
					super.onManagedUpdate(pSecondsElapsed);
					if (this.getX() < -200) {
						this.setPosition(1480, this.getY());
					}
				};
			};
			PhysicsHandler handler5 = new PhysicsHandler(farCloud2);
			farCloud2.registerUpdateHandler(handler5);
			handler5.setVelocity(farCloudSpeed, 0);
			
			GameScene.this.attachChild(cloud1);
			GameScene.this.attachChild(cloud2);
			GameScene.this.attachChild(farCloud1);
			GameScene.this.attachChild(farCloud2);
	}
	
	private void setRockDirection(float x, Body body, float yVel) {
		if (x > screenWidth/2) {
			body.setLinearVelocity(ROCK_POSITIVE_VEL_X, yVel);
		} else {
			body.setLinearVelocity(ROCK_NEGATIVE_VEL_X, yVel);
		}
	}
	
	private void createBackground() {
		ParallaxBackground background = new ParallaxBackground(0, 0, 0);
		background.attachParallaxEntity(new ParallaxEntity(0, new Sprite(screenWidth/2, screenHeight/2, resourcesManager.game_background_region, vbom)));
		this.setBackground(background);
	}
	
	private void createWindows() {
		helpWindow = new Sprite(0, 0, resourcesManager.game_help_window_region, vbom) {
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        		if (pSceneTouchEvent.isActionDown()) {
        			final IEaseFunction[] easeFunction = EASEFUNCTIONS[0];
        			
	    			GameScene.this.detachChild(fade);
	    			
	    			helpWindow.registerEntityModifier(new MoveModifier(HELP_WINDOW_MOVE_MODIFIER_DURATION_MILISECONDS, screenWidth/2, 
	    					helpWindow.getY(), screenWidth/2, helpWindow.getY() + HELP_WINDOW_INITIAL_Y, easeFunction[0]) {
	    				@Override
	    				protected void onModifierFinished(IEntity pItem) {
	    					super.onModifierFinished(pItem);
	    					
	    					availablePause = true;
	    					gameHud.setVisible(true);
	    					helpWindow.setVisible(false);
	    	    			GameScene.this.setIgnoreUpdate(false);
	    	    			GameScene.this.unregisterTouchArea(helpWindow);
	    				}
	    			});
	    			
	    			setTouchAreaBindingOnActionDownEnabled(false);
					setTouchAreaBindingOnActionMoveEnabled(false);
	    			
        		}
        		return true;
        	};
		};
		gameOverWindow = new Sprite(0, 0, resourcesManager.game_over_window_region, vbom);
		pauseWindow = new Sprite(0, 0, resourcesManager.game_pause_window_region, vbom);
		fade = new Rectangle(screenWidth/2, screenHeight/2, screenWidth, screenHeight, vbom);
		
		fade.setColor(Color.BLACK);
		fade.setAlpha(0.75f);
		
		helpWindow.setVisible(false);
	}
	
	private void createHud() {
		gameHud = new HUD();	

		gameScoreTiles = new TiledSprite[6];
		
		gameScore = new Sprite(75, 685, resourcesManager.game_score_region, vbom);
		score = 0;
		
		gameScoreTiles[0] = new TiledSprite(175 + 0 * 60, 670, resourcesManager.game_score_tiled_region.deepCopy(), vbom) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				final TiledSprite scoreRef = this;
				super.onManagedUpdate(pSecondsElapsed);
				String test = Integer.toString(score);
				if (score > 0 || scoreChanged) {
					if (score < 1000) {
						test = "000" + test;
					} else if (score < 10000) {
						test = "00" + test;
					} else if (score < 100000) {
						test = "0" + test;
					}
					String indexString = Character.toString(test.charAt(0));
					int index = Integer.parseInt(indexString);
					scoreRef.setCurrentTileIndex(index);
				}
			}
		};		
		gameScoreTiles[1] = new TiledSprite(175 + 1 * 60, 670, resourcesManager.game_score_tiled_region.deepCopy(), vbom) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				final TiledSprite scoreRef = this;
				super.onManagedUpdate(pSecondsElapsed);
				String test = Integer.toString(score);
				if (score > 0 || scoreChanged) {
					if (score < 1000) {
						test = "000" + test;
					} else if (score < 10000) {
						test = "00" + test;
					} else if (score < 100000) {
						test = "0" + test;
					}
					String indexString = Character.toString(test.charAt(1));
					int index = Integer.parseInt(indexString);
					scoreRef.setCurrentTileIndex(index);
				}
			}
		};
		gameScoreTiles[2] = new TiledSprite(175 + 2 * 60, 670, resourcesManager.game_score_tiled_region.deepCopy(), vbom) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				final TiledSprite scoreRef = this;
				super.onManagedUpdate(pSecondsElapsed);
				String test = Integer.toString(score);
				if (score > 0 || scoreChanged) {
					if (score < 1000) {
						test = "000" + test;
					} else if (score < 10000) {
						test = "00" + test;
					} else if (score < 100000) {
						test = "0" + test;
					}
					String indexString = Character.toString(test.charAt(2));
					int index = Integer.parseInt(indexString);
					scoreRef.setCurrentTileIndex(index);
				}
			}
		};
		gameScoreTiles[3] = new TiledSprite(175 + 3 * 60, 670, resourcesManager.game_score_tiled_region.deepCopy(), vbom) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				final TiledSprite scoreRef = this;
				super.onManagedUpdate(pSecondsElapsed);
				String test = Integer.toString(score);
				if (score > 0 || scoreChanged) {
					if (score < 1000) {
						test = "000" + test;
					} else if (score < 10000) {
						test = "00" + test;
					} else if (score < 100000) {
						test = "0" + test;
					}
					String indexString = Character.toString(test.charAt(3));
					int index = Integer.parseInt(indexString);
					scoreRef.setCurrentTileIndex(index);
				}
			}
		};
		gameScoreTiles[4] = new TiledSprite(175 + 4 * 60, 670, resourcesManager.game_score_tiled_region.deepCopy(), vbom) {
			@Override
			protected void onManagedUpdate(float pSecondsElapsed) {
				final TiledSprite scoreRef = this;
				super.onManagedUpdate(pSecondsElapsed);
				String test = Integer.toString(score);
				if (score > 0 || scoreChanged) {
					if (score < 1000) {
						test = "000" + test;
					} else if (score < 10000) {
						test = "00" + test;
					} else if (score < 100000) {
						test = "0" + test;
					}
					String indexString = Character.toString(test.charAt(4));
					int index = Integer.parseInt(indexString);
					scoreRef.setCurrentTileIndex(index);
				} 
			}
		};
		gameScoreTiles[5] = new TiledSprite(175 + 5 * 60, 670, resourcesManager.game_score_tiled_region.deepCopy(), vbom);
		pauseButton = new Sprite(1230, 670, resourcesManager.game_pause_button_region, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (availablePause) {
					displayPauseWindow();
				}
				
				setTouchAreaBindingOnActionDownEnabled(false);
				setTouchAreaBindingOnActionMoveEnabled(false);
				
				return true;
			}
		};

		gameHud.attachChild(gameScore);
		for (int i = 0; i < gameScoreTiles.length; i++) {
			gameHud.attachChild(gameScoreTiles[i]);
		}
		gameHud.attachChild(pauseButton);
		
		GameScene.this.registerTouchArea(pauseButton);

		camera.setHUD(gameHud);
	}
	
	private void createCountdown() {
		countdownFrame1 = new Sprite(screenWidth/2, screenHeight/2, resourcesManager.game_countdown_frame_1_region, vbom);
		countdownFrame2 = new Sprite(screenWidth/2, screenHeight/2, resourcesManager.game_countdown_frame_2_region, vbom);
		countdownFrame3 = new Sprite(screenWidth/2, screenHeight/2, resourcesManager.game_countdown_frame_3_region, vbom);
		countdownFrame4 = new Sprite(screenWidth/2, screenHeight/2, resourcesManager.game_countdown_frame_4_region, vbom);
		
		countdownFrame2.setVisible(false);
		countdownFrame3.setVisible(false);
		countdownFrame4.setVisible(false);
		
		GameScene.this.attachChild(countdownFrame1);
		GameScene.this.attachChild(countdownFrame2);
		GameScene.this.attachChild(countdownFrame3);
		GameScene.this.attachChild(countdownFrame4);
	}
		
	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -3), false);
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
	private void addScore(int score) {
		this.score += score;
		engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				registerEntityModifier(new DelayModifier(0.05f, new IEntityModifierListener() {
						
					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
						scoreChanged = true;										
					}
						
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
						scoreChanged = false;
							
					}
				}));
			}
		});
	}
	
	private void displayPauseWindow() {
		availablePause = false;	
		GameScene.this.setIgnoreUpdate(true);		
		
		pauseWindow.setPosition(camera.getCenterX(), camera.getCenterY());
		
		soundDisabled = new Sprite(1500, 1500, resourcesManager.game_sound_button_disabled_region, vbom);
		currentScore = new TiledSprite[6];
		for (int i = 0; i < currentScore.length; i++) {
			currentScore[i] = new TiledSprite(140 + i * 60, 260, resourcesManager.game_score_tiled_region.deepCopy(), vbom);
			currentScore[i].setCurrentTileIndex(gameScoreTiles[i].getCurrentTileIndex());
		}
		
		//If soundEnabled = 0, enabled..if 1 disabled
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		int soundEnabled = sharedPreferences.getInt("soundEnabled", 0);
		if (soundEnabled == 1) {
			activity.enableSound(false);
			soundDisabled.setPosition(51, 51);
		} else if (soundEnabled == 0) {
			activity.enableSound(true);
			soundDisabled.setPosition(1500, 1500);
		}
		
		soundButton = new Sprite(-275, 550, resourcesManager.game_sound_button_region, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					Log.i("protect", "soundButton touched");
					//If soundEnabled = 0, enabled..if 1 disabled
					SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
					int soundEnabled = sharedPreferences.getInt("soundEnabled", 0);
					Log.i("protect", "soundEnabled " + soundEnabled);
					Editor editor = sharedPreferences.edit();
					if (soundEnabled == 1) {
						soundEnabled = 0;
						soundDisabled.setPosition(1500, 1500);
						activity.enableSound(true);
					} else if (soundEnabled == 0) {
						soundEnabled = 1;
						soundDisabled.setPosition(51, 51);
						activity.enableSound(false);
					}
					editor.putInt("soundEnabled", soundEnabled);
					editor.commit();	
					
					setTouchAreaBindingOnActionDownEnabled(false);
					setTouchAreaBindingOnActionMoveEnabled(false);
				}
				return true;
			}
		};
		
	    retryButton = new Sprite(270, 25, resourcesManager.game_retry_button_region, vbom){
	    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
	    		if (pSceneTouchEvent.isActionDown()) {
	    			gameHud.dispose();
					gameHud.setVisible(false);
					detachChild(gameHud);
					myGarbageCollection();
					SceneManager.getInstance().loadGameScene(engine, GameScene.this);
				}
	    		return true;
	    	};
	    };
	    quitButton = new Sprite(0, 25, resourcesManager.game_quit_button_region, vbom){
	    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
	    		if (pSceneTouchEvent.isActionDown()) {
	    			gameHud.dispose();
					gameHud.setVisible(false);
					detachChild(gameHud);
					myGarbageCollection();
					SceneManager.getInstance().loadMenuScene(engine, GameScene.this);
	    		}
	    		return true;
	    	};
	    };
	    resumeButton = new Sprite(550, 25, resourcesManager.game_resume_button_region, vbom){
	    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
	    		if (pSceneTouchEvent.isActionDown()) {
	    			availablePause = true;
					gameHud.setVisible(true);
					GameScene.this.detachChild(fade);
					GameScene.this.detachChild(pauseWindow);
	    			GameScene.this.setIgnoreUpdate(false);
	    			GameScene.this.unregisterTouchArea(this);
	    			for (int i = 0; i < currentScore.length; i++) {
	    				pauseWindow.detachChild(currentScore[i]);
	    			}
	    			GameScene.this.unregisterTouchArea(resumeButton);
	    		    GameScene.this.unregisterTouchArea(retryButton);
	    		    GameScene.this.unregisterTouchArea(quitButton);
	    		    GameScene.this.unregisterTouchArea(soundButton);
	    		    GameScene.this.registerTouchArea(pauseButton);
	    		    
	    		    setTouchAreaBindingOnActionDownEnabled(false);
					setTouchAreaBindingOnActionMoveEnabled(false);
	    		}
	    		return true;
	    	};
	    };
	    
		for (int i = 0; i < currentScore.length; i++) {
			pauseWindow.attachChild(currentScore[i]);
		}
	    pauseWindow.attachChild(resumeButton);
	    pauseWindow.attachChild(retryButton);	    
	    pauseWindow.attachChild(quitButton);
	    pauseWindow.attachChild(soundButton);
	    
	    GameScene.this.registerTouchArea(resumeButton);
	    GameScene.this.registerTouchArea(retryButton);
	    GameScene.this.registerTouchArea(quitButton);
	    GameScene.this.registerTouchArea(soundButton);
		GameScene.this.unregisterTouchArea(pauseButton);
	    
		soundButton.attachChild(soundDisabled);
		
		GameScene.this.attachChild(fade);
		GameScene.this.attachChild(pauseWindow);

		gameHud.setVisible(false);
	}
	
	private void scoreSubmittedToast() {
		GameScene.this.activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(activity, "Score submitted", Toast.LENGTH_SHORT).show();	
			}
		});
	}
	
	private void gameOver() {
		final IEaseFunction[] easeFunction = EASEFUNCTIONS[0];

		Rectangle fade = new Rectangle(screenWidth/2, screenHeight/2, screenWidth, screenHeight, vbom);
		
		newRecord = new Sprite(600, 325, resourcesManager.game_new_record_region, vbom);
		finalScore = new TiledSprite[6];
		for (int i = 0; i < finalScore.length; i++) {
			finalScore[i] = new TiledSprite(140 + i * 60, 280, resourcesManager.game_score_tiled_region.deepCopy(), vbom);
			finalScore[i].setCurrentTileIndex(gameScoreTiles[i].getCurrentTileIndex());
		}
		
		loadHighScore();
		saveHighScore("highScore", score);
		
		if (score > previousHighScore) {
			if (!activity.getGoogleApiClient().isConnected()) {
				activity.getGoogleApiClient().connect();
			}
			if (activity.getGoogleApiClient() != null && activity.getGoogleApiClient().isConnected()) {
				Games.Leaderboards.submitScore(activity.getGoogleApiClient(), activity.getHighestScoreLeaderboardID() , score);
    			scoreSubmittedToast();
			} else {
				activity.getGoogleApiClient().connect();
			}
		}
		
		if (score <= previousHighScore) {
			newRecord.setVisible(false);
		}
		
		fade.setColor(Color.BLACK);
		fade.setAlpha(0.35f);
		
		availablePause = false;
		gameOver = true;

		gameOverWindow.registerEntityModifier(new MoveModifier(HELP_WINDOW_MOVE_MODIFIER_DURATION_MILISECONDS, screenWidth/2, 
				1200, screenWidth/2, screenHeight/2, easeFunction[0]));
		
		retryButton = new Sprite(425, 25, resourcesManager.game_retry_button_region, vbom){
	    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
	    		if (pSceneTouchEvent.isActionDown()) {
	    			setTouchAreaBindingOnActionDownEnabled(false);
					setTouchAreaBindingOnActionMoveEnabled(false);
					
	    			gameHud.dispose();
					gameHud.setVisible(false);
					detachChild(gameHud);
					myGarbageCollection();
					SceneManager.getInstance().loadGameScene(engine, GameScene.this);
				}
	    		return true;
	    	};
	    };
	    quitButton = new Sprite(150, 25, resourcesManager.game_quit_button_region, vbom){
	    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
	    		if (pSceneTouchEvent.isActionDown()) {
	    			setTouchAreaBindingOnActionDownEnabled(false);
					setTouchAreaBindingOnActionMoveEnabled(false);
					
	    			gameHud.dispose();
					gameHud.setVisible(false);
					detachChild(gameHud);
					myGarbageCollection();
					SceneManager.getInstance().loadMenuScene(engine, GameScene.this);
	    		}
	    		return true;
	    	};
	    };
	    twitterButton = new Sprite(450, 190, resourcesManager.game_twitter_button_region, vbom) {
	    	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
	    		if (pSceneTouchEvent.isActionDown()) {
	    			setTouchAreaBindingOnActionDownEnabled(false);
					setTouchAreaBindingOnActionMoveEnabled(false);
					
	    			Intent shareIntent = new Intent();
	    		    shareIntent.setAction(Intent.ACTION_SEND);
	    		    shareIntent.setType("text/plain");
	    		    shareIntent.setPackage("com.twitter.android");
	    		    shareIntent.putExtra(Intent.EXTRA_TEXT, "My score in #ProtectTheTown is " + score + " points. And yours?");
	    		    activity.tweetScore(shareIntent);
	    		}
	    		return true;
	    	}
	    };
		
		gameHud.setVisible(false);
		GameScene.this.attachChild(fade);
		GameScene.this.attachChild(gameOverWindow);
		GameScene.this.registerTouchArea(retryButton);
	    GameScene.this.registerTouchArea(quitButton);
	    GameScene.this.registerTouchArea(twitterButton);
	    for (int i = 0; i < finalScore.length; i++) {
			gameOverWindow.attachChild(finalScore[i]);
		}
		gameOverWindow.attachChild(retryButton);
		gameOverWindow.attachChild(quitButton);
		gameOverWindow.attachChild(newRecord);
		gameOverWindow.attachChild(twitterButton);
		
		showAds();
	}
	
	private void showAds() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		
		int playedInSameSession = sharedPreferences.getInt("playedInSameSession", 0);
		playedInSameSession++;
		
		editor.putInt("playedInSameSession", playedInSameSession);
		editor.commit();
		
		if (playedInSameSession % 5 == 0 && activity.isAvailableUnityAds()) {
			activity.showUnityAds();
		} else if (Chartboost.hasInterstitial(CBLocation.LOCATION_GAMEOVER)) {
			Chartboost.showInterstitial(CBLocation.LOCATION_GAMEOVER);
		} else {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (activity.getAdmobInterstitialAd().isLoaded()) {
						activity.getAdmobInterstitialAd().show();
					}
				}
				
			});
			
		}
	}
	
	private void createExplosion(float x, float y) {
		resourcesManager.explosion.play();
		explosion = explosionPool.obtainPoolItem();
		explosion.setPosition(x, y);
		final long[] EXPLOSION_ANIMATE = new long[] {75, 75, 75, 75, 75, 150};
		explosion.animate(EXPLOSION_ANIMATE, 0, 5, false);
		explosion.registerUpdateHandler(new IUpdateHandler() {
			final AnimatedSprite expRef = explosion;
			
			@Override
			public void reset() {
				
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				final IUpdateHandler upd = this;
				engine.runOnUpdateThread(new Runnable() {
					
					@Override
					public void run() {
						if (!expRef.isAnimationRunning()) {
							explosionPool.recyclePoolItem(expRef);
							expRef.unregisterUpdateHandler(upd);
							expRef.setIgnoreUpdate(true);
						}						
					}
				});
				
			}
		});
		
		explosion.setCullingEnabled(true);
	}
	
	private void createSmallExplosion(float x, float y) {
		resourcesManager.explosion.play();
		small_explosion = smallExplosionPool.obtainPoolItem();
		small_explosion.setPosition(x, y);
		final long[] EXPLOSION_ANIMATE = new long[] {75, 75, 75, 75, 75, 150};
		small_explosion.animate(EXPLOSION_ANIMATE, 0, 5, false);
		small_explosion.registerUpdateHandler(new IUpdateHandler() {
			final AnimatedSprite smallExpRef = small_explosion;
			
			@Override
			public void reset() {
				
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				final IUpdateHandler upd = this;
				engine.runOnUpdateThread(new Runnable() {
					
					@Override
					public void run() {
						if (!smallExpRef.isAnimationRunning()) {
							smallExplosionPool.recyclePoolItem(smallExpRef);
							smallExpRef.unregisterUpdateHandler(upd);
							smallExpRef.setIgnoreUpdate(true);
						}						
					}
				});
				
			}
		});
		small_explosion.setCullingEnabled(true);
	}
	
	private void createBombBox() {
		Random rand = new Random();
		int positionIndex = rand.nextInt(4);
		int[] box_position = {100, 400, 800, 1200};
		
		bombBox = new Bomb(box_position[positionIndex], BOX_INITIAL_Y, vbom, camera, physicsWorld) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final Bomb ref = this;
					engine.runOnUpdateThread(new Runnable() {
						@Override
						public void run() {
							if (ref.getBombBody().isActive() && availablePause && !gameOver) {
								regenerateBoxes(ref.getBombBody());
								ref.getBombBody().setActive(false);
								registerEntityModifier(new DelayModifier(0.1f, new IEntityModifierListener() {
									
									@Override
									public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
										destroyAllEnemies = true;										
									}
									
									@Override
									public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
										destroyAllEnemies = false;
										
									}
								}));
							}
						}
					});
					
					setTouchAreaBindingOnActionDownEnabled(false);
					setTouchAreaBindingOnActionMoveEnabled(false);
				}
				
				return true;
			}
		};
		bombBox.getBombBody().setActive(false);
		bombBox.setCullingEnabled(true);
		GameScene.this.attachChild(bombBox);
		GameScene.this.registerTouchArea(bombBox);
	}
	
	private void createRepairBox() {
		Random rand = new Random();
		int positionIndex = rand.nextInt(4);
		int[] box_position = {100, 400, 800, 1200};
		
		repairBox = new Repair(box_position[positionIndex], BOX_INITIAL_Y, vbom, camera, physicsWorld) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final Repair ref = this;
					engine.runOnUpdateThread(new Runnable() {
						@Override
						public void run() {
							if (ref.getRepairBody().isActive() && availablePause && !gameOver) {
								regenerateBoxes(ref.getRepairBody());
								ref.getRepairBody().setActive(false);
								if (!smallHouse.isSmallHouseDestroyed()) {
									smallHouse.repairCompleteSmallHouse();
								}
								if (!house.isHouseDestroyed()) {
									house.repairCompleteHouse();
								}
								if (!largeHouse.isLargeHouseDestroyed()) {
									largeHouse.repairCompleteLargeHouse();
								}
							}
						}
					});
					
					setTouchAreaBindingOnActionDownEnabled(false);
					setTouchAreaBindingOnActionMoveEnabled(false);
				}
				
				return true;
			}
		};
		repairBox.getRepairBody().setActive(false);
		repairBox.setCullingEnabled(true);
		GameScene.this.attachChild(repairBox);
		GameScene.this.registerTouchArea(repairBox);
	}
	
	private void createShieldBox() {
		Random rand = new Random();
		int positionIndex = rand.nextInt(4);
		int[] box_position = {100, 400, 800, 1200};
		
		shieldBox = new Shield(box_position[positionIndex], BOX_INITIAL_Y, vbom, camera, physicsWorld) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionDown()) {
					final Shield ref = this;
					engine.runOnUpdateThread(new Runnable() {
						@Override
						public void run() {
							if (ref.getShieldBody().isActive() && availablePause && !gameOver) {
								resourcesManager.shield.play();
								resourcesManager.shield.setLooping(true);
								regenerateBoxes(ref.getShieldBody());
								ref.getShieldBody().setActive(false);
								registerEntityModifier(new DelayModifier(SHIELD_DURATION, new IEntityModifierListener() {
									
									@Override
									public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
										dome.setPosition(screenWidth/2, 175);
										shieldBar.setWidth(185);
										shieldBar.setVisible(true);
										shieldBarFrame.setVisible(true);
										shieldBarLogo.setVisible(true);
										domeActivated = true;
									}
									
									@Override
									public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
										resourcesManager.shield.stop();
										dome.setPosition(-1500, -1500);
										shieldBar.setVisible(false);
										shieldBarFrame.setVisible(false);
										shieldBarLogo.setVisible(false);
										domeActivated = true;
									}
								}));
							}
						}
					});
					
					setTouchAreaBindingOnActionDownEnabled(false);
					setTouchAreaBindingOnActionMoveEnabled(false);
				}
				
				return true;
			}
		};
		shieldBox.getShieldBody().setActive(false);
		shieldBox.setCullingEnabled(true);
		GameScene.this.attachChild(shieldBox);
		GameScene.this.registerTouchArea(shieldBox);
	}
	
	private void createDome() {
		float shieldBarWidth = 185;
		float shieldBarHeight = 20;
		
		shieldBar = new Rectangle(screenWidth/2 + screenWidth/4 - 100, 670, shieldBarWidth, shieldBarHeight, vbom);
		shieldBarFrame = new Sprite(screenWidth/2 + screenWidth/4 - 100, 670, resourcesManager.game_shield_bar_frame_region, vbom);
		shieldBarLogo = new Sprite(screenWidth/2 + 150 - 70, 670, resourcesManager.game_shield_bar_logo_region, vbom);
		
		dome = new Sprite(-1500, 1500, resourcesManager.game_dome_region, vbom);
		
		shieldBar.setColor(0.314f, 0.157f, 0f);
		
		shieldBar.setVisible(false);
		shieldBarFrame.setVisible(false);
		shieldBarLogo.setVisible(false);

		gameHud.attachChild(shieldBarFrame);
		gameHud.attachChild(shieldBar);
		gameHud.attachChild(shieldBarLogo);
		GameScene.this.attachChild(dome);
	}
	
	private ContactListener contactListener() {
		ContactListener contactListener = new ContactListener() {
			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
	
			}
			
			@Override
			public void endContact(Contact contact) {
				
			}
			
			@Override
			public void beginContact(Contact contact) {
				final Fixture x1 = contact.getFixtureA();
				final Fixture x2 = contact.getFixtureB();
				
				if (x1.getBody().getUserData().equals("large_rock") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("large_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("rock") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_rock") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("small_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_rock") && x2.getBody().getUserData().equals("house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.destroyHouse();
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}							
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("house") && x2.getBody().getUserData().equals("large_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.destroyHouse();
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}							
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_rock") && x2.getBody().getUserData().equals("small_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.destroySmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_house") && x2.getBody().getUserData().equals("large_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.destroySmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_rock") && x2.getBody().getUserData().equals("large_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							largeHouse.damageLargeHouse();
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_house") && x2.getBody().getUserData().equals("large_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							largeHouse.damageLargeHouse();
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("rock") && x2.getBody().getUserData().equals("house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							house.damageHouse();
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("house") && x2.getBody().getUserData().equals("rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							house.damageHouse();
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("rock") && x2.getBody().getUserData().equals("small_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							smallHouse.damageSmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_house") && x2.getBody().getUserData().equals("rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							largeHouse.damageLargeHouse();
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("rock") && x2.getBody().getUserData().equals("large_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							largeHouse.damageLargeHouse();
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_house") && x2.getBody().getUserData().equals("rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							smallHouse.damageSmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_rock") && x2.getBody().getUserData().equals("house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("house") && x2.getBody().getUserData().equals("small_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_rock") && x2.getBody().getUserData().equals("small_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_house") && x2.getBody().getUserData().equals("small_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_rock") && x2.getBody().getUserData().equals("large_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
							regenerateRocks(x1.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_house") && x2.getBody().getUserData().equals("small_rock")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
							regenerateRocks(x2.getBody());
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("shot") && x2.getBody().getUserData().equals("large_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
							x1.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x1.getBody().getAngle());
							x1.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_house") && x2.getBody().getUserData().equals("shot")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
							x2.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x2.getBody().getAngle());
							x2.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("shot") && x2.getBody().getUserData().equals("house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}
							x1.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x1.getBody().getAngle());
							x1.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("house") && x2.getBody().getUserData().equals("shot")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}
							x2.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x2.getBody().getAngle());
							x2.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("shot") && x2.getBody().getUserData().equals("small_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							x1.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x1.getBody().getAngle());
							x1.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_house") && x2.getBody().getUserData().equals("shot")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							x2.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x2.getBody().getAngle());
							x2.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("shot") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x1.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x1.getBody().getAngle());
							x1.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("shot")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x2.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x2.getBody().getAngle());
							x2.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("satelite")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x2.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x2.getBody().getAngle());
							x2.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("satelite") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x1.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x1.getBody().getAngle());
							x1.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_house") && x2.getBody().getUserData().equals("satelite")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							x2.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x2.getBody().getAngle());
							x2.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("satelite") && x2.getBody().getUserData().equals("small_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.damageSmallHouse();
							if (!smallHouse.isSmallHouseDestroyed()) {
								createSmallExplosion(smallHouse.getX(), smallHouse.getY());
							}
							x1.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x1.getBody().getAngle());
							x1.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("house") && x2.getBody().getUserData().equals("satelite")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}
							x2.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x2.getBody().getAngle());
							x2.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("satelite") && x2.getBody().getUserData().equals("house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.damageHouse();
							if (!house.isHouseDestroyed()) {
								createSmallExplosion(house.getX(), house.getY());
							}
							x1.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x1.getBody().getAngle());
							x1.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_house") && x2.getBody().getUserData().equals("satelite")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
							x2.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x2.getBody().getAngle());
							x2.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("satelite") && x2.getBody().getUserData().equals("large_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.damageLargeHouse();
							if (!largeHouse.isLargeHouseDestroyed()) {
								createSmallExplosion(largeHouse.getX(), largeHouse.getY());
							}
							x1.getBody().setTransform(2000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 1000 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, x1.getBody().getAngle());
							x1.getBody().setActive(false);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("house") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.getHouseBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							house.getHouseBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("small_house") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.getSmallHouseBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("small_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							smallHouse.getSmallHouseBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("large_house") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.getLargeHouseBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("large_house")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							largeHouse.getLargeHouseBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("base_floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x1.getBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("base_floor") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x2.getBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("earth")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x1.getBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("earth") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x2.getBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("floor") && x2.getBody().getUserData().equals("tree")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x2.getBody().setType(BodyType.StaticBody);
						}
					});
				}
				
				if (x1.getBody().getUserData().equals("tree") && x2.getBody().getUserData().equals("floor")) {
					engine.runOnUpdateThread(new Runnable() {
						
						@Override
						public void run() {
							x1.getBody().setType(BodyType.StaticBody);
						}
					});
				}
				
			}
		};
		return contactListener;
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		
	}
	
	private void myGarbageCollection() {
		Iterator<Body> allMyBodies = physicsWorld.getBodies();
        while(allMyBodies.hasNext()) {
        	try {
        		final Body myCurrentBody = allMyBodies.next();
                	physicsWorld.destroyBody(myCurrentBody);                
            } catch (Exception e) {
            	Debug.e(e);
            }
        }
               
        this.clearChildScene();
        this.detachChildren();
        this.reset();
        this.detachSelf();
        physicsWorld.clearForces();
        physicsWorld.clearPhysicsConnectors();
        physicsWorld.reset();
 
        System.gc();
	}
	
	@Override
	public void handleOnPause() {
		if (availablePause) {
			displayPauseWindow();
		}
	}
	
	@Override
	public void onBackKeyPressed() {
		engine.runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
				if (availablePause) {
					displayPauseWindow();
				} else if (!gameOver){
					availablePause = true;
					gameHud.setVisible(true);
					GameScene.this.detachChild(fade);
					GameScene.this.detachChild(pauseWindow);
	    			GameScene.this.setIgnoreUpdate(false);
	    			GameScene.this.unregisterTouchArea(resumeButton);
	    		    GameScene.this.unregisterTouchArea(retryButton);
	    		    GameScene.this.unregisterTouchArea(quitButton);
	    		    GameScene.this.registerTouchArea(pauseButton);
				} else {
					gameHud.dispose();
					gameHud.setVisible(false);
					detachChild(gameHud);
					myGarbageCollection();
					SceneManager.getInstance().loadMenuScene(engine, GameScene.this);
				}
			}
		});
	}
	
	private void saveHighScore(String key, int localScore) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		Editor editor = sharedPreferences.edit();
		if (sharedPreferences.getInt("highScore", 0) < localScore) {
			editor.putInt("highScore", localScore);
		}		
		editor.commit();
	}
	
	private void loadHighScore() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		previousHighScore = sharedPreferences.getInt("highScore", 0);
	}
}
