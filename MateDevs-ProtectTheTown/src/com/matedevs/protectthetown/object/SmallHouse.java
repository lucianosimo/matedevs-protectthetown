package com.matedevs.protectthetown.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.matedevs.protectthetown.manager.ResourcesManager;

public abstract class SmallHouse extends Sprite{

	private Body body;
	private int energy;
	
	private final static int MAX_ENERGY = 2;
	
	public abstract void onDie();
	
	public SmallHouse(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().game_small_house_region.deepCopy(), vbom);
		createPhysics(camera, physicsWorld);
		energy = MAX_ENERGY;
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		final float width = 115 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final float height = 90 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] v1 = {
			new Vector2(-0.32840f*width, -0.39006f*height),
			new Vector2(+0.31975f*width, -0.34116f*height),
			new Vector2(+0.43549f*width, +0.11854f*height),
			new Vector2(+0.33519f*width, +0.41197f*height),
			new Vector2(-0.28981f*width, +0.53912f*height),
			new Vector2(-0.41327f*width, +0.09898f*height),
		};
		body = PhysicsFactory.createPolygonBody(physicsWorld, this, v1, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		body.setUserData("small_house");
		body.setFixedRotation(true);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				body.setLinearVelocity(body.getLinearVelocity().x, -11);
				if (isSmallHouseDestroyed()) {
					onDie();
				}
			}
		});
	}
	
	public Body getSmallHouseBody() {
		return body;
	}
	
	public void repairCompleteSmallHouse() {
		energy = MAX_ENERGY;
	}
	
	public int getMaxEnergy() {
		return MAX_ENERGY;
	}
	
	public void repairPartialSmallHouse() {
		if (energy < MAX_ENERGY) {
			energy++;
		}		
	}
	
	public void destroySmallHouse() {
		energy = 0;
	}
	
	public void damageSmallHouse() {
		energy--;
	}
	
	public int getSmallHouseEnergy() {
		return energy;
	}
	
	public boolean isSmallHouseDestroyed() {
		if (energy > 0) {
			return false;
		} else {
			return true;
		}
	}
	
}
