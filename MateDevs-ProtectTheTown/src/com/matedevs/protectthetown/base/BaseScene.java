package com.matedevs.protectthetown.base;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.matedevs.protectthetown.GameActivity;
import com.matedevs.protectthetown.manager.ResourcesManager;
import com.matedevs.protectthetown.manager.SceneManager.SceneType;

public abstract class BaseScene extends Scene{

	public Engine engine;
	public BoundCamera camera;
	public GameActivity activity;
	public VertexBufferObjectManager vbom;
	public ResourcesManager resourcesManager;
	
	public BaseScene() {
		this.resourcesManager = ResourcesManager.getInstance();
		this.engine = resourcesManager.engine;
		this.camera = resourcesManager.camera;
		this.activity = resourcesManager.activity;
		this.vbom = resourcesManager.vbom;
		createScene();
	}
	
	public abstract void createScene();
	public abstract void onBackKeyPressed();
	public abstract SceneType getSceneType();
	public abstract void disposeScene();
	public abstract void handleOnPause();
}