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
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.matedevs.protectthetown.manager.ResourcesManager;

public class Satelite extends Sprite{

	private Body body;
	private FixtureDef fixture;
	private static final int FALL_VELOCITY = -6;
	
	public Satelite(float pX, float pY, VertexBufferObjectManager vbom, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().game_satelite_region.deepCopy(), vbom);
		createPhysics(camera, physicsWorld);
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		this.setUserData("satelite");
		fixture = PhysicsFactory.createFixtureDef(0, 0, 0);
		fixture.filter.groupIndex = -1;
		final float width = 284 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		final float height = 217 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;		
		final Vector2[] v = {
			new Vector2(-0.51056f*width, -0.29954f*height),
			new Vector2(-0.39085f*width, -0.48387f*height),
			new Vector2(+0.18662f*width, -0.32258f*height),
			new Vector2(+0.50704f*width, +0.32719f*height),
			new Vector2(+0.39789f*width, +0.51613f*height),
			new Vector2(-0.15493f*width, +0.28571f*height),	
		};

		body = PhysicsFactory.createPolygonBody(physicsWorld, this, v, BodyType.DynamicBody, fixture);
		body.setUserData("satelite");
		body.setFixedRotation(true);
		body.setLinearVelocity(0, FALL_VELOCITY);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
			}
		});
	}
	
	public Body getSateliteBody() {
		return body;
	}
	
}
