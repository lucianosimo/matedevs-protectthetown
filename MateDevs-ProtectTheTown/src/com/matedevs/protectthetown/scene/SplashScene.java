package com.matedevs.protectthetown.scene;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.color.Color;

import com.matedevs.protectthetown.base.BaseScene;
import com.matedevs.protectthetown.manager.SceneManager.SceneType;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SplashScene extends BaseScene{

	private Sprite splash;
	private float screenWidth;
	private float screenHeight;	
	
	private static final int UPDATES = 175;
	
	@Override
	public void createScene() {
		screenWidth = resourcesManager.camera.getWidth();
		screenHeight = resourcesManager.camera.getHeight();	
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		int played = sharedPreferences.getInt("played", 0);
		Editor editor = sharedPreferences.edit();
		played++;
		editor.putInt("played", played);
		editor.commit();
		
		setBackground(new Background(Color.WHITE));
			
		final Rectangle fade = new Rectangle(screenWidth/2, screenHeight/2, screenWidth, screenHeight, vbom);
		
		splash = new Sprite(0, 0, resourcesManager.splash_region, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		
		splash.setPosition(screenWidth/2, screenHeight/2);		
		fade.setColor(Color.BLACK);
		fade.setAlpha(1.0f);
		
		attachChild(splash);
		attachChild(fade);
		
		fade.registerEntityModifier(new AlphaModifier(2f, 1.0f, 0.0f));
		fade.setAlpha(0.0f);
		
		engine.registerUpdateHandler(new IUpdateHandler() {
			private int updates = 0;
			
			@Override
			public void reset() {

			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				updates++;
				if (updates > UPDATES) {
					fade.registerEntityModifier(new AlphaModifier(2f, 0.0f, 1.0f));
				}
			}
		});
	}

	@Override
	public void onBackKeyPressed() {
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SPLASH;
	}

	@Override
	public void disposeScene() {
		splash.detachSelf();
		splash.dispose();
		this.detachSelf();
		this.dispose();
	}

	@Override
	public void handleOnPause() {
		
	}

}