package com.matedevs.protectthetown.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Ufo extends Sprite{

	private Body body;
	private FixtureDef fixture;
	
	public Ufo(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld, ITextureRegion region) {
		super(pX, pY, region.deepCopy(), vbom);
		createPhysics(camera, physicsWorld);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		this.setUserData("ufo");
		fixture = PhysicsFactory.createFixtureDef(0, 0, 0);
		fixture.filter.groupIndex = -1;
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.KinematicBody, fixture);
		body.setUserData("ufo");
		body.setFixedRotation(true);
		setUfoVelocityY(5);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				body.setLinearVelocity(body.getLinearVelocity().x, body.getLinearVelocity().y);
			}
		});
	}
	
	public Body getUfoBody() {
		return body;
	}
	
	public void setUfoVelocityX(float velX) {
		body.setLinearVelocity(velX, body.getLinearVelocity().y);
	}
	
	public void setUfoVelocityY(float velY) {
		body.setLinearVelocity(body.getLinearVelocity().x, velY);
	}
	
}
