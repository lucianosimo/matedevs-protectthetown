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

public class LargeRock extends Sprite{

	private Body body;
	private FixtureDef fixture;
	private float savedRotation = 0;
	
	public LargeRock(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().game_large_rock_region.deepCopy(), vbom);
		createPhysics(camera, physicsWorld);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		final int random = rand.nextInt(2) + 1;
		final float omega = random;
		final float width = 160 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final float height = 166 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final Vector2[] v = {
			new Vector2(-0.53125f*width, -0.10241f*height),
			new Vector2(-0.31875f*width, -0.43976f*height),
			new Vector2(+0.01875f*width, -0.52410f*height),
			new Vector2(+0.51875f*width, -0.01807f*height),
			new Vector2(+0.26250f*width, +0.45783f*height),
			new Vector2(-0.18125f*width, +0.52410f*height),
			new Vector2(-0.46875f*width, +0.22892f*height),
		};
		
		this.setUserData("large_rock");
		fixture = PhysicsFactory.createFixtureDef(0, 0, 0);
		fixture.filter.groupIndex = -1;
		body = PhysicsFactory.createPolygonBody(physicsWorld, this, v, BodyType.DynamicBody, fixture);
		body.setUserData("large_rock");
		body.setFixedRotation(true);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				body.setAngularVelocity(omega);
			}
		});
		this.registerEntityModifier(new LoopEntityModifier(new RotationModifier(5, savedRotation, savedRotation + omega * 180)) {
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				savedRotation = LargeRock.this.getRotation();
			}
		});
	}
	
	public void setLargeRockDirection(float x, float y) {
		body.setLinearVelocity(x, y);
	}
	
	public Body getLargeRockBody() {
		return body;
	}
	
	public float getLargeRockXVel() {
		return body.getLinearVelocity().x;
	}

}
