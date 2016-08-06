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

public abstract class House extends Sprite{

	private Body body;
	private int energy;
	private int whichHouse;
	
	private final static int MAX_ENERGY = 4;
	
	public abstract void onDie();
	
	public House(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld, ITextureRegion region, int house) {
		super(pX, pY, region.deepCopy(), vbom);
		whichHouse = house;
		createPhysics(camera, physicsWorld);
		energy = MAX_ENERGY;
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		switch (whichHouse) {
		case 1:
			final float width = 150 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			final float height = 110 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			final Vector2[] v1 = {
				new Vector2(-0.50413f*width, -0.49412f*height),
				new Vector2(+0.49174f*width, -0.48235f*height),
				new Vector2(+0.50826f*width, +0.00588f*height),
				new Vector2(+0.33884f*width, +0.52353f*height),
				new Vector2(-0.37603f*width, +0.51765f*height),
				new Vector2(-0.53719f*width, +0.05294f*height),
			};
			body = PhysicsFactory.createPolygonBody(physicsWorld, this, v1, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
			break;
		case 2:
			final float width2 = 150 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			final float height2 = 110 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			final Vector2[] v2 = {
				new Vector2(-0.50459f*width2, -0.00643f*height2),
				new Vector2(-0.44420f*width2, -0.40117f*height2),
				new Vector2(+0.45556f*width2, -0.40117f*height2),
				new Vector2(+0.51594f*width2, -0.04298f*height2),
				new Vector2(+0.37101f*width2, +0.49064f*height2),
				new Vector2(-0.36570f*width2, +0.49795f*height2),
				};
			body = PhysicsFactory.createPolygonBody(physicsWorld, this, v2, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
			break;
		case 3:
			final float width3 = 150 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			final float height3 = 110 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			final Vector2[] v3 = {
				new Vector2(-0.42569f*width3, -0.44837f*height3),
				new Vector2(+0.43657f*width3, -0.44837f*height3),
				new Vector2(+0.47708f*width3, -0.01536f*height3),
				new Vector2(+0.25139f*width3, +0.51569f*height3),
				new Vector2(-0.41412f*width3, +0.49118f*height3),
				new Vector2(-0.48356f*width3, -0.00719f*height3),
				};
			body = PhysicsFactory.createPolygonBody(physicsWorld, this, v3, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
			break;
		case 4:
			final float width4 = 150 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			final float height4 = 110 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
			final Vector2[] v4 = {
				new Vector2(-0.44037f*width4, -0.47639f*height4),
				new Vector2(+0.45163f*width4, -0.47639f*height4),
				new Vector2(+0.49353f*width4, -0.01632f*height4),
				new Vector2(+0.28400f*width4, +0.48715f*height4),
				new Vector2(-0.27275f*width4, +0.50451f*height4),
				new Vector2(-0.53017f*width4, -0.01632f*height4),
				};
			body = PhysicsFactory.createPolygonBody(physicsWorld, this, v4, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
			break;
		default:
			break;
		}
		body.setUserData("house");
		body.setFixedRotation(true);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				body.setLinearVelocity(body.getLinearVelocity().x, -11);
				if (isHouseDestroyed()) {
					onDie();
				}
			}
		});
	}
	
	public Body getHouseBody() {
		return body;
	}
	
	public int getMaxEnergy() {
		return MAX_ENERGY;
	}
	
	public void repairCompleteHouse() {
		energy = MAX_ENERGY;
	}
	
	public void repairPartialHouse() {
		if (energy < MAX_ENERGY) {
			energy++;
		}		
	}
	
	public void destroyHouse() {
		energy = 0;
	}
	
	public void damageHouse() {
		energy--;
	}
	
	public int getHouseEnergy() {
		return energy;
	}
	
	public boolean isHouseDestroyed() {
		if (energy > 0) {
			return false;
		} else {
			return true;
		}
	}
	
}
