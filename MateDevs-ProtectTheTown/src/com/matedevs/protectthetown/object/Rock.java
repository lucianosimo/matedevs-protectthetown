package com.matedevs.protectthetown.object;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
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

public class Rock extends Sprite{

	private Body body;
	private FixtureDef fixture;
	private float savedRotation = 0;
	private float omega;
	
	public Rock(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().game_rock_region.deepCopy(), vbom);
		createPhysics(camera, physicsWorld);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		//n = rand.nextInt(max - min + 1) + min;
		Random rand = new Random();
		omega = rand.nextInt(2) + 3;
		
		final float width = 128 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final float height = 128 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;		
		final Vector2[] v = {
			new Vector2(-0.49219f*width, -0.07812f*height),
			new Vector2(-0.38281f*width, -0.42969f*height),
			new Vector2(+0.08594f*width, -0.44531f*height),
			new Vector2(+0.52344f*width, -0.13281f*height),
			new Vector2(+0.28906f*width, +0.40625f*height),
			new Vector2(-0.17188f*width, +0.46875f*height),
			new Vector2(-0.40625f*width, +0.24219f*height),
		};		
		this.setUserData("rock");
		fixture = PhysicsFactory.createFixtureDef(0, 0, 0);
		fixture.filter.groupIndex = -1;
		body = PhysicsFactory.createPolygonBody(physicsWorld, this, v, BodyType.DynamicBody, fixture);
		body.setUserData("rock");
		body.setFixedRotation(true);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				body.setAngularVelocity(omega);
			}
		});
		rotateRock();
	}
	
	private void rotateRock() {
		this.registerEntityModifier(new RotationModifier(5, savedRotation, savedRotation + omega * 180) {
			@Override
			protected void onModifierFinished(IEntity pItem) {
				super.onModifierFinished(pItem);
				savedRotation = Rock.this.getRotation();
				rotateRock();
			}
		});
	}
	
	public void setRockDirection(float x, float y) {
		body.setLinearVelocity(x, y);
	}
	
	public Body getRockBody() {
		return body;
	}
	
	public float getRockXVel() {
		return body.getLinearVelocity().x;
	}
}
