package com.matedevs.protectthetown.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public abstract class LargeHouse extends Sprite{

	private Body body;
	private int energy;
	private int whichHouse;
	
	private final static int MAX_ENERGY = 6;
	
	public abstract void onDie();
	
	public LargeHouse(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld, ITextureRegion region, int house) {
		super(pX, pY, region.deepCopy(), vbom);
		whichHouse = house;
		createPhysics(camera, physicsWorld);
		energy = MAX_ENERGY;
		
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		switch (whichHouse) {
		case 1:
			final float width = 150 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			final float height = 175 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			final Vector2[] v1 = {
				new Vector2(-0.37361f*width, -0.45899f*height),
				new Vector2(+0.37292f*width, -0.45394f*height),
				new Vector2(+0.47708f*width, -0.14081f*height),
				new Vector2(-0.00903f*width, +0.51071f*height),
				new Vector2(-0.46620f*width, -0.15596f*height),
			};
			body = PhysicsFactory.createPolygonBody(physicsWorld, this, v1, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
			break;
		case 2:
			final float width2 = 150 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			final float height2 = 175 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			final Vector2[] v2 = {
				new Vector2(-0.41991f*width2, -0.45069f*height2),
				new Vector2(+0.44815f*width2, -0.42176f*height2),
				new Vector2(+0.47708f*width2, -0.16134f*height2),
				new Vector2(-0.07269f*width2, +0.52153f*height2),
				new Vector2(-0.51250f*width2, -0.01088f*height2),
				};
			body = PhysicsFactory.createPolygonBody(physicsWorld, this, v2, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
			break;
		default:
			break;
		}
		body.setUserData("large_house");
		body.setFixedRotation(true);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				body.setLinearVelocity(body.getLinearVelocity().x, -11);
				if (isLargeHouseDestroyed()) {
					onDie();
				}
			}
		});
	}
	
	public Body getLargeHouseBody() {
		return body;
	}
	
	public int getMaxEnergy() {
		return MAX_ENERGY;
	}
	
	public void repairCompleteLargeHouse() {
		energy = MAX_ENERGY;
	}
	
	public void repairPartialLargeHouse() {
		if (energy < MAX_ENERGY) {
			energy++;
		}		
	}
	
	public void destroyLargeHouse() {
		energy = 0;
	}
	
	public void damageLargeHouse() {
		energy--;
	}
	
	public int getLargeHouseEnergy() {
		return energy;
	}
	
	public boolean isLargeHouseDestroyed() {
		if (energy > 0) {
			return false;
		} else {
			return true;
		}
	}
	
}
