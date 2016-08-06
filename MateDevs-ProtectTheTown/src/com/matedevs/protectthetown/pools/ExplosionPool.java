package com.matedevs.protectthetown.pools;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.GenericPool;

import com.matedevs.protectthetown.scene.GameScene;

public class ExplosionPool extends GenericPool<AnimatedSprite>{

	private ITiledTextureRegion iTextureRegion;
	private VertexBufferObjectManager vbom;
	private GameScene game;
	 
    public ExplosionPool(ITiledTextureRegion pTextureRegion, VertexBufferObjectManager vbom, GameScene gameLocal) {
        iTextureRegion = pTextureRegion;
        this.vbom = vbom;
        game = gameLocal;
    }
 
    @Override
    protected AnimatedSprite onAllocatePoolItem() {
    	AnimatedSprite exp = new AnimatedSprite(0, 0, iTextureRegion.deepCopy(), vbom);
        return exp;
        
    }
 
    protected void onHandleRecycleItem(final AnimatedSprite explosion) {
    	explosion.clearEntityModifiers();
    	explosion.clearUpdateHandlers();
    	explosion.setVisible(false);
    	explosion.detachSelf();
    	explosion.reset();
    }
    
    @Override
    protected void onHandleObtainItem(AnimatedSprite explosion) {
    	explosion.reset();
    	explosion.setVisible(true);
    	if (!explosion.hasParent()) {
    		game.attachChild(explosion);
    	}
    	
    }
}
