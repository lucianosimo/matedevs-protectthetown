package com.matedevs.protectthetown.scene;

import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import com.matedevs.protectthetown.base.BaseScene;
import com.matedevs.protectthetown.manager.SceneManager;
import com.matedevs.protectthetown.manager.SceneManager.SceneType;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener{
	
	private MenuScene menuChildScene;
	private float screenWidth;
	private float screenHeight;
	
	//private Text highScoreText;
	//private int highScore;
	
	private Sprite soundDisabled;
	//private Sprite musicDisabled;
	
	private final int MENU_PLAY = 0;
	private final int MENU_RATEUS = 1;
	private final int MENU_QUIT = 2;
	private final int MENU_GLOBAL_SCORES = 3;

	@Override
	public void createScene() {
		screenWidth = resourcesManager.camera.getWidth();
		screenHeight = resourcesManager.camera.getHeight();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		int played = sharedPreferences.getInt("played", 0);
		//Rated: 0 = no, 1 = yes, 2 = no and don't want to rate
		int rated = sharedPreferences.getInt("rated", 0);
		if (rated == 0) {
			if (played == 5 || played == 20 || played == 50) {
				displayRateUsWindow();
			}
		}
		createBackground();
		createMenuChildScene();
	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
	}
	
	public void createBackground() {
		AutoParallaxBackground background = new AutoParallaxBackground(0, 0, 0, 12);
		background.attachParallaxEntity(new ParallaxEntity(0, new Sprite(screenWidth/2, screenHeight/2, resourcesManager.menu_background_region, vbom)));
		this.setBackground(background);
	}
	
	private void createMenuChildScene() {
		soundDisabled = new Sprite(51, 51, resourcesManager.menu_sound_disabled_button_region, vbom);
		
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
		
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(screenWidth/2, screenHeight/2);
		
		Sprite soundButton = new Sprite(-570, 290, resourcesManager.menu_sound_button_region, vbom) {
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
				}
				return true;
			}
		};

		menuChildScene.attachChild(soundButton);
		soundButton.attachChild(soundDisabled);

		menuChildScene.registerTouchArea(soundButton);

		final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.menu_play_button_region, vbom), 1.2f, 1);
		final IMenuItem rateusMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_RATEUS, resourcesManager.menu_rateus_button_region, vbom), 1.2f, 1);
		final IMenuItem globalScoresMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_GLOBAL_SCORES, resourcesManager.menu_global_scores_button_region, vbom), 1.2f, 1);
		final IMenuItem quitMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_QUIT, resourcesManager.menu_quit_button_region, vbom), 1.2f, 1);

		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.addMenuItem(rateusMenuItem);
		menuChildScene.addMenuItem(globalScoresMenuItem);
		menuChildScene.addMenuItem(quitMenuItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		playMenuItem.setPosition(0, -225);
		rateusMenuItem.setPosition(-375, -250);
		globalScoresMenuItem.setPosition(375, -250);
		quitMenuItem.setPosition(570, 290);
		
		menuChildScene.setOnMenuItemClickListener(this);
		setChildScene(menuChildScene);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,	float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
			case MENU_PLAY:
				SceneManager.getInstance().loadGameScene(engine, this);
				return true;
			case MENU_RATEUS:
				activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.matedevs.protectthetown")));
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
				int rated = sharedPreferences.getInt("rated", 0);
				Editor editor = sharedPreferences.edit();
				rated = 1;
				editor.putInt("rated", rated);
				editor.commit();
				return true;
			case MENU_GLOBAL_SCORES:
				if (activity.getGoogleApiClient() != null && activity.getGoogleApiClient().isConnected()) {
					activity.displayLeaderboard();
				} else {
					activity.getGoogleApiClient().connect();
				}
				return true;
			case MENU_QUIT:
				System.exit(0);
				return true;
			default:
				return false;
		}
	}

	@Override
	public void handleOnPause() {
		// TODO Auto-generated method stub
		
	}
	
	private void displayRateUsWindow() {
		MainMenuScene.this.activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new AlertDialog.Builder(MainMenuScene.this.activity)
				.setMessage("Do you want to rate us")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				    public void onClick(DialogInterface dialog, int whichButton) {
				    	activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.lucianosimo.protectthetown")));
				    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
						int rated = sharedPreferences.getInt("rated", 0);
						Editor editor = sharedPreferences.edit();
						rated = 1;
						editor.putInt("rated", rated);
						editor.commit();
				    }})
				.setNegativeButton("Never", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
						int rated = sharedPreferences.getInt("rated", 0);
						Editor editor = sharedPreferences.edit();
						rated = 2;
						editor.putInt("rated", rated);
						editor.commit();						
					}
				})
				.setNeutralButton("Maybe later", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
						int played = sharedPreferences.getInt("played", 0);
						Editor editor = sharedPreferences.edit();
						played++;
						editor.putInt("played", played);
						editor.commit();						
					}
				})
				.show();
			}
		});
	}

}
