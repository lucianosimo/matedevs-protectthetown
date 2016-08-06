package com.matedevs.protectthetown.scene;

import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.Sprite;

import com.matedevs.protectthetown.base.BaseScene;
import com.matedevs.protectthetown.manager.SceneManager.SceneType;

public class LoadingScene extends BaseScene{

	private float screenWidth;
	private float screenHeight;
	
	@Override
	public void createScene() {
		screenWidth = resourcesManager.camera.getWidth();
		screenHeight = resourcesManager.camera.getHeight();
		AutoParallaxBackground background = new AutoParallaxBackground(0, 0, 0, 12);
		background.attachParallaxEntity(new ParallaxEntity(0, new Sprite(screenWidth/2, screenHeight/2, resourcesManager.loading_background_region, vbom)));
		setBackground(background);
	}

	@Override
	public void onBackKeyPressed() {
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LOADING;
	}

	@Override
	public void disposeScene() {
	}

	@Override
	public void handleOnPause() {
		// TODO Auto-generated method stub
		
	}

}
