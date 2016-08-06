package com.matedevs.protectthetown.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.matedevs.protectthetown.manager.ResourcesManager;

public class Shield extends Sprite{

	private Body body;
	private FixtureDef fixture;
	
	public Shield(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().game_shield_region.deepCopy(), vbom);
		createPhysics(camera, physicsWorld);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		fixture = PhysicsFactory.createFixtureDef(0, 0, 0);
		fixture.filter.groupIndex = -1;
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, fixture);
		body.setUserData("shield");
		body.setFixedRotation(true);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				body.setLinearVelocity(body.getLinearVelocity().x, -9);
			}
		});
	}
	
	public Body getShieldBody() {
		return body;
	}
}
