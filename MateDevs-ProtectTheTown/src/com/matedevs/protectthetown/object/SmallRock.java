package com.matedevs.protectthetown.object;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.matedevs.protectthetown.manager.ResourcesManager;

public class SmallRock extends Sprite{

	private Body body;
	private FixtureDef fixture;
	private float savedRotation = 0;
	
	public SmallRock(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().game_small_rock_region.deepCopy(), vbom);
		createPhysics(camera, physicsWorld);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		final int random = rand.nextInt(2) + 5;
		final float omega = random;
		final float width = 63 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final float height = 66 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;		
		final Vector2[] v = {
			new Vector2(-0.49206f*width, -0.13636f*height),
			new Vector2(-0.15873f*width, -0.48485f*height),
			new Vector2(+0.17460f*width, -0.45455f*height),
			new Vector2(+0.44444f*width, -0.09091f*height),
			new Vector2(+0.26984f*width, +0.34848f*height),
			new Vector2(-0.11111f*width, +0.48485f*height),
			new Vector2(-0.46032f*width, +0.18182f*height),
		};
		this.setUserData("small_rock");
		fixture = PhysicsFactory.createFixtureDef(0, 0, 0);
		fixture.filter.groupIndex = -1;
		body = PhysicsFactory.createPolygonBody(physicsWorld, this, v, BodyType.DynamicBody, fixture);
		body.setUserData("small_rock");
		body.setFixedRotation(true);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				body.setAngularVelocity(omega);
			}
		});
		this.registerEntityModifier(new LoopEntityModifier(new RotationModifier(5, savedRotation, savedRotation - omega * 180)) {
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				savedRotation = SmallRock.this.getRotation();
			}
		});
	}
	
	public void setSmallRockDirection(float x, float y) {
		body.setLinearVelocity(x, y);
	}
	
	public Body getSmallRockBody() {
		return body;
	}
}
