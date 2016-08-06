package com.matedevs.protectthetown.manager;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import com.matedevs.protectthetown.base.BaseScene;
import com.matedevs.protectthetown.scene.GameScene;
import com.matedevs.protectthetown.scene.LoadingScene;
import com.matedevs.protectthetown.scene.MainMenuScene;
import com.matedevs.protectthetown.scene.SplashScene;

public class SceneManager {

	private BaseScene splashScene;
	private BaseScene menuScene;
	private BaseScene loadingScene;
	private BaseScene gameScene;
	
	private static final SceneManager INSTANCE = new SceneManager();
	private SceneType currentSceneType = SceneType.SCENE_SPLASH;
	private BaseScene currentScene;
	private Engine engine = ResourcesManager.getInstance().engine;
	
	public enum SceneType {
		SCENE_SPLASH,
		SCENE_MENU,
		SCENE_LOADING,
		SCENE_GAME,
	}
	
	public void setScene(BaseScene scene) {
		engine.setScene(scene);
		currentScene = scene;
		currentSceneType = scene.getSceneType();
	}
	
	public void setScene(SceneType sceneType) {
		switch(sceneType) {
			case SCENE_SPLASH:
				setScene(splashScene);
				break;
			case SCENE_MENU:
				setScene(menuScene);
				break;
			case SCENE_GAME:
				setScene(gameScene);
				break;
			case SCENE_LOADING:
				setScene(loadingScene);
				break;
			default:
				break;
		}
	}
	
	public static SceneManager getInstance() {
		return INSTANCE;
	}
	
	public SceneType getCurrentSceneType() {
		return currentSceneType;
	}
	
	public BaseScene getCurrentScene() {
		return currentScene;
	}
	
	public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		ResourcesManager.getInstance().loadSplashScreen();
		splashScene = new SplashScene();
		currentScene = splashScene;
		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
	}
	
	private void disposeSplashScene() {
		ResourcesManager.getInstance().unloadSplashScreen();
		splashScene.disposeScene();
		splashScene = null;
	}
	
	public void createMenuScene() {
		ResourcesManager.getInstance().loadMenuResources();
		menuScene = new MainMenuScene();
		loadingScene = new LoadingScene();
		setScene(menuScene);
		disposeSplashScene();
	}
	
	public void loadGameScene(final Engine mEngine, final BaseScene scene) {
		setScene(loadingScene);
		scene.disposeScene();
		switch (scene.getSceneType()) {
			case SCENE_GAME:
				ResourcesManager.getInstance().unloadGameResources();
				break;
			default:
				break;
		}
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadGameResources();
				gameScene = new GameScene();
				setScene(gameScene);
			}
		}));
	}
	
	public void loadMenuScene(final Engine mEngine, final BaseScene scene) {
		setScene(loadingScene);
		scene.disposeScene();
		switch (scene.getSceneType()) {
			case SCENE_GAME:
				ResourcesManager.getInstance().unloadGameResources();
				break;
			default:
				break;
		}
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadMenuResources();
				menuScene = new MainMenuScene();
				setScene(menuScene);
			}
		}));
	}
	
}
