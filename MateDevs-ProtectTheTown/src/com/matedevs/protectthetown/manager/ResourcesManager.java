package com.matedevs.protectthetown.manager;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import com.matedevs.protectthetown.GameActivity;

public class ResourcesManager {

	private static final ResourcesManager INSTANCE = new ResourcesManager();
	
	public Engine engine;
	public BoundCamera camera;
	public GameActivity activity;
	public VertexBufferObjectManager vbom;
	
	//Splash items
	public ITextureRegion splash_region;
	private BitmapTextureAtlas splashTextureAtlas;
	
	//Menu fonts
	public Font highScoreFont;
	
	//Menu audio
	public Music menuMusic;
	
	//Menu items
	public ITextureRegion loading_background_region;
	public ITextureRegion menu_background_region;
	public ITextureRegion menu_play_button_region;
	public ITextureRegion menu_rateus_button_region;
	public ITextureRegion menu_global_scores_button_region;
	public ITextureRegion menu_quit_button_region;
	public ITextureRegion menu_sound_button_region;
	public ITextureRegion menu_sound_disabled_button_region;
	public ITextureRegion menu_music_button_region;
	public ITextureRegion menu_music_disabled_button_region;
	
	private BuildableBitmapTextureAtlas loadingBackgroundTextureAtlas;
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	private BuildableBitmapTextureAtlas menuBackgroundTextureAtlas;
	
	
	//Game audio
	public Music gameMusic;
	public Sound explosion;
	public Sound shield;
	public Sound shotSound;
	public Sound ufoSound;
	
	//Game fonts
	public Font scoreFont;
	public Font countdownFont;
	public Font gameOverFont;
	public Font currentScoreFont;
	public Font finalScoreFont;
	public Font pauseFont;
	
	//Game HUD
	
	//Objects
	public ITextureRegion game_small_rock_region;
	public ITextureRegion game_rock_region;
	public ITextureRegion game_large_rock_region;
	
	public ITextureRegion game_satelite_region;
	
	public ITextureRegion game_cloud_1_region;
	public ITextureRegion game_cloud_2_region;
	public ITextureRegion game_far_cloud_1_region;
	public ITextureRegion game_far_cloud_2_region;
	
	public ITextureRegion game_trees_1_region;
	public ITextureRegion game_trees_2_region;
	public ITextureRegion game_trees_3_region;
	public ITextureRegion game_trees_4_region;
	public ITextureRegion game_trees_5_region;
	public ITextureRegion game_trees_6_region;
	
	public ITextureRegion game_ufo_1_region;
	public ITextureRegion game_ufo_2_region;
	public ITextureRegion game_ufo_3_region;
	
	public ITextureRegion game_ufo_shot_region;
	
	public ITextureRegion game_bomb_region;
	public ITextureRegion game_repair_region;
	public ITextureRegion game_shield_region;
	
	public ITextureRegion game_dome_region;
		
	public ITextureRegion game_small_house_region;
	
	public ITextureRegion game_house_1_region;
	public ITextureRegion game_house_2_region;
	public ITextureRegion game_house_3_region;
	public ITextureRegion game_house_4_region;
	
	public ITextureRegion game_large_house_1_region;
	public ITextureRegion game_large_house_2_region;
	
	public ITextureRegion game_health_bar_frame_region;
	public ITextureRegion game_shield_bar_frame_region;
	public ITextureRegion game_shield_bar_logo_region;
	
	//Platforms
	public ITextureRegion game_floor_region;
	public ITextureRegion game_earth_region;
	public ITextureRegion game_base_floor_region;
	
	//Backgrounds
	public ITextureRegion game_background_region;
	
	//Animated
	public ITiledTextureRegion game_explosion_region;
	public ITiledTextureRegion game_small_explosion_region;
	
	//Countdown
	public ITextureRegion game_countdown_frame_1_region;
	public ITextureRegion game_countdown_frame_2_region;
	public ITextureRegion game_countdown_frame_3_region;
	public ITextureRegion game_countdown_frame_4_region;
	
	//Windows
	public ITextureRegion game_help_window_region;
	public ITextureRegion game_over_window_region;
	public ITextureRegion game_pause_window_region;
	public ITextureRegion game_new_record_region;
	public ITextureRegion game_score_region;
	
	//Buttons
	public ITextureRegion game_resume_button_region;
	public ITextureRegion game_retry_button_region;
	public ITextureRegion game_quit_button_region;
	public ITextureRegion game_pause_button_region;
	public ITextureRegion game_submit_button_region;
	public ITextureRegion game_twitter_button_region;
	public ITextureRegion game_sound_button_region;
	public ITextureRegion game_sound_button_disabled_region;
	public ITextureRegion game_music_button_region;
	public ITextureRegion game_music_button_disabled_region;
	
	//Score tiles
	public ITiledTextureRegion game_score_tiled_region;
	
	//Game Textures
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	private BuildableBitmapTextureAtlas gameWindowsTextureAtlas;
	private BuildableBitmapTextureAtlas gameHelpWindowTextureAtlas;
	private BuildableBitmapTextureAtlas gameAnimatedTextureAtlas;
	private BuildableBitmapTextureAtlas gameBackgroundTextureAtlas;
	private BuildableBitmapTextureAtlas gameScoreTextureAtlas;
	
	//Splash Methods
	public void loadSplashScreen() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
		splashTextureAtlas.load();
	}
	
	public void unloadSplashScreen() {
		splashTextureAtlas.unload();
		splash_region = null;
	}
	
	//Menu methods
	public void loadMenuResources() {
		//loadLoadingGraphics();
		loadMenuGraphics();
		loadMenuFonts();
		loadMenuAudio();		
	}
	
	public void unloadMenuResources() {
		unloadMenuTextures();
		unloadMenuFonts();
		unloadMenuAudio();
	}
	
	private void loadMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1000, 1000, TextureOptions.BILINEAR);
		menuBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1280, 720, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		loadingBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		
		loading_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(loadingBackgroundTextureAtlas, activity, "loading_background.png");
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuBackgroundTextureAtlas, activity, "menu_background.png");
		menu_play_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_play_button.png");
		//menu_play_button_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_play_button_background.png");
		menu_rateus_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_rateus_button.png");
		//menu_rateus_button_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_rateus_button_background.png");
		menu_global_scores_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_global_scores_button.png");
		menu_quit_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_quit_button.png");
		menu_sound_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_sound_button.png");
		menu_sound_disabled_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_sound_button_off.png");;
		menu_music_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_music_button.png");
		menu_music_disabled_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_music_button_off.png");;
		
		try {
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuBackgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.loadingBackgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
			this.loadingBackgroundTextureAtlas.load();
			this.menuBackgroundTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			org.andengine.util.debug.Debug.e(e);
		}
	}
	
	private void loadMenuFonts() {
		FontFactory.setAssetBasePath("fonts/menu/");
		final ITexture highScoreTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		highScoreFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), highScoreTexture, activity.getAssets(), "font.ttf", 40, true, Color.WHITE_ARGB_PACKED_INT, 1f, Color.WHITE_ARGB_PACKED_INT);
		highScoreFont.load();
	}
	
	private void loadMenuAudio() {
		try {
			MusicFactory.setAssetBasePath("music/menu/");
			menuMusic = MusicFactory.createMusicFromAsset(activity.getMusicManager(), activity, "menuMusic.mp3");
			menuMusic.setLooping(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void unloadMenuTextures() {
		this.menuTextureAtlas.unload();
		this.menuBackgroundTextureAtlas.unload();
	}
	
	private void unloadMenuFonts() {
		
	}
	
	private void unloadMenuAudio() {
		menuMusic.stop();
		activity.getMusicManager().remove(menuMusic);
		System.gc();
	}
	
	//Game Methods
	public void loadGameResources() {
		loadGameGraphics();
		loadGameAudio();
		loadGameFonts();
	}
	
	public void unloadGameResources() {
		unloadGameTextures();
		unloadGameFonts();	
		unloadGameAudio();
	}
	
	private void loadGameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1500, 1500, TextureOptions.BILINEAR);
		gameWindowsTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1250, 1250, TextureOptions.BILINEAR);
		gameHelpWindowTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1250, 1250, TextureOptions.BILINEAR);
		gameAnimatedTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 900, 500, TextureOptions.BILINEAR);
		gameBackgroundTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1280, 720, TextureOptions.BILINEAR);
		gameScoreTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 600, 72, TextureOptions.BILINEAR);
		
		game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTextureAtlas, activity, "game_background.png");
		
		game_small_rock_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_rock_small.png");
		game_rock_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_rock_medium.png");
		game_large_rock_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_rock_large.png");
		
		game_satelite_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_satelite.png");
		
		game_far_cloud_1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_far_cloud_1.png");
		game_far_cloud_2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_far_cloud_2.png");		
		game_cloud_1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_cloud_1.png");
		game_cloud_2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_cloud_2.png");
		
		game_trees_1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_trees_1.png");
		game_trees_2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_trees_2.png");
		game_trees_3_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_trees_3.png");
		game_trees_4_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_trees_4.png");
		game_trees_5_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_trees_5.png");
		game_trees_6_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_trees_6.png");
		
		game_ufo_1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_ufo_1.png");
		game_ufo_2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_ufo_2.png");
		game_ufo_3_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_ufo_3.png");
		
		game_ufo_shot_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_ufo_shot.png");
		
		game_small_house_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_house_small.png");
		
		game_house_1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_house_medium_1.png");
		game_house_2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_house_medium_2.png");
		game_house_3_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_house_medium_3.png");
		game_house_4_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_house_medium_4.png");
		
		game_large_house_1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_house_large_1.png");
		game_large_house_2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_house_large_2.png");
		
		game_health_bar_frame_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_health_bar_frame.png");
		game_shield_bar_frame_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_shield_bar_frame.png");
		game_shield_bar_logo_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_shield_bar_logo.png");
		
		game_bomb_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_bomb.png");
		game_repair_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_repair.png");
		game_shield_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_shield.png");
		
		game_dome_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_dome.png");
		
		game_base_floor_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_base_floor.png");
		game_earth_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_earth.png");
		game_floor_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "game_floor.png");
		
		game_help_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameHelpWindowTextureAtlas, activity, "game_help_window.png");
		game_over_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_over_window.png");
		game_pause_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_pause_window.png");
		game_new_record_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_new_record.png");
		game_score_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_score.png");
		
		game_resume_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_resume_button.png");
		game_retry_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_retry_button.png");
		game_quit_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_quit_button.png");
		game_pause_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_pause_button.png");
		game_submit_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_submit_button.png");
		game_twitter_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_twitter_button.png");
		game_sound_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_sound_button.png");
		game_sound_button_disabled_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_sound_button_off.png");
		game_music_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_music_button.png");
		game_music_button_disabled_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_music_button_off.png");		
		
		game_countdown_frame_1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_countdown_frame_1.png");
		game_countdown_frame_2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_countdown_frame_2.png");
		game_countdown_frame_3_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_countdown_frame_3.png");
		game_countdown_frame_4_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameWindowsTextureAtlas, activity, "game_countdown_frame_4.png");
		
		game_score_tiled_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameAnimatedTextureAtlas, activity, "game_score_tiles.png", 10, 1);
		
		game_explosion_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameAnimatedTextureAtlas, activity, "game_explosion.png", 6, 1);
		game_small_explosion_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameAnimatedTextureAtlas, activity, "game_small_explosion.png", 6, 1);
		
		try {
			this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameHelpWindowTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameWindowsTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameAnimatedTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameBackgroundTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameScoreTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameTextureAtlas.load();
			this.gameHelpWindowTextureAtlas.load();
			this.gameWindowsTextureAtlas.load();
			this.gameAnimatedTextureAtlas.load();
			this.gameBackgroundTextureAtlas.load();
			this.gameScoreTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	private void loadGameAudio() {
		MusicFactory.setAssetBasePath("music/game/");
		SoundFactory.setAssetBasePath("sound/game/");
		try {
			gameMusic = MusicFactory.createMusicFromAsset(activity.getMusicManager(), activity, "gameMusic.mp3");
			gameMusic.setLooping(true);
			explosion = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "explosion.mp3");
			shield = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "shield.mp3");
			shotSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "shotSound.mp3");
			ufoSound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), activity, "ufoSound.mp3");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void unloadGameAudio() {
		gameMusic.stop();
		
		explosion.stop();
		shield.stop();
		shotSound.stop();
		ufoSound.stop();
		
		activity.getSoundManager().remove(explosion);
		activity.getSoundManager().remove(shield);
		activity.getSoundManager().remove(shotSound);
		activity.getSoundManager().remove(ufoSound);
		
		activity.getMusicManager().remove(gameMusic);
		System.gc();
	}
	
	private void loadGameFonts() {
		FontFactory.setAssetBasePath("fonts/game/");
		final ITexture scoreTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture countdownTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture gameOverTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture finalScoreTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture currentScoreTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture pauseTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		scoreFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), scoreTexture, activity.getAssets(), "font.ttf", 35, true, Color.WHITE_ARGB_PACKED_INT, 1f, Color.WHITE_ARGB_PACKED_INT);
		countdownFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), countdownTexture, activity.getAssets(), "font.ttf", 70, true, Color.RED_ARGB_PACKED_INT, 2f, Color.RED_ARGB_PACKED_INT);
		gameOverFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), gameOverTexture, activity.getAssets(), "font.ttf", 45, true, Color.RED_ARGB_PACKED_INT, 2f, Color.RED_ARGB_PACKED_INT);
		finalScoreFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), finalScoreTexture, activity.getAssets(), "font.ttf", 40, true, Color.RED_ARGB_PACKED_INT, 2f, Color.RED_ARGB_PACKED_INT);
		currentScoreFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), currentScoreTexture, activity.getAssets(), "font.ttf", 40, true, Color.RED_ARGB_PACKED_INT, 2f, Color.RED_ARGB_PACKED_INT);
		pauseFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), pauseTexture, activity.getAssets(), "font.ttf", 45, true, Color.RED_ARGB_PACKED_INT, 2f, Color.RED_ARGB_PACKED_INT);
		scoreFont.load();
		countdownFont.load();
		gameOverFont.load();
		finalScoreFont.load();
		currentScoreFont.load();
		pauseFont.load();
	}
	
	private void unloadGameTextures() {
		this.gameTextureAtlas.unload();
		this.gameHelpWindowTextureAtlas.unload();
		this.gameWindowsTextureAtlas.unload();
		this.gameAnimatedTextureAtlas.unload();
		this.gameBackgroundTextureAtlas.unload();
		this.gameScoreTextureAtlas.unload();
	}
	
	private void unloadGameFonts() {
		scoreFont.unload();
		countdownFont.unload();
		gameOverFont.unload();
		finalScoreFont.unload();
		currentScoreFont.unload();
		pauseFont.unload();
	}
	
	
	//Manager Methods
	public static void prepareManager(Engine engine, GameActivity activity, BoundCamera camera, VertexBufferObjectManager vbom) {
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;		
	}
	
	public static ResourcesManager getInstance() {
		return INSTANCE;
	}

}


