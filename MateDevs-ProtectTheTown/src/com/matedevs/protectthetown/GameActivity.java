package com.matedevs.protectthetown;

import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import com.chartboost.sdk.Chartboost;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.matedevs.protectthetown.manager.ResourcesManager;
import com.matedevs.protectthetown.manager.SceneManager;
import com.unity3d.ads.android.IUnityAdsListener;
import com.unity3d.ads.android.UnityAds;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;

public class GameActivity extends BaseGameActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private BoundCamera camera;
	public static float mGravityX = 0;
	
	private View decorView;
	private int flags;

	private GoogleApiClient mGoogleApiClient;
	private InterstitialAd mInterstitialAd;
	
	private static int RC_SIGN_IN = 9001;
	private static int RC_LEADERBOARD = 9002;
	private final static String SIGN_IN_OTHER_ERROR = "There was an issue with sign in. Please try again later.";
	
	private final static String HIGHEST_SCORE_LEADERBOARD_ID = "CgkI4r-I2bEREAIQAQ";
	
	private final static String CHARTBOOST_APP_ID = "57b60d6df6cd4543a0574eb6";
	private final static String CHARTBOOST_APP_SIGNATURE = "3d704573a17ec274ab47b1e0de2b5f0a9cdae052";
	
	//private final static String ADMOB_APP_ID = "ca-app-pub-7393689937893463~9946254636";
	private final static String ADMOB_AD_UNIT_ID = "ca-app-pub-7393689937893463/2422987831";
	
	private final static String UNITY_ADS_GAME_ID = "1118513";

	private boolean mResolvingConnectionFailure = false;
	private boolean mAutoStartSignInFlow = true;
	
	private final static float SPLASH_DURATION = 5f;
	
	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
	            .addConnectionCallbacks(this)
	            .addOnConnectionFailedListener(this)
	            .addApi(Games.API).addScope(Games.SCOPE_GAMES)
	            .build();
		
		initializeAdsServices();
        
        decorView = getWindow().getDecorView();
        flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        
        decorView.setSystemUiVisibility(flags);
	}
	
	private void initializeAdsServices() {
		Chartboost.startWithAppId(this, CHARTBOOST_APP_ID, CHARTBOOST_APP_SIGNATURE);
	    Chartboost.onCreate(this);
	    
	    mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(ADMOB_AD_UNIT_ID);
        
        UnityAds.init((Activity)this, UNITY_ADS_GAME_ID, new IUnityAdsListener() {
			
			@Override
			public void onVideoStarted() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onVideoCompleted(String arg0, boolean arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onShow() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onHide() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFetchFailed() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFetchCompleted() {
				// TODO Auto-generated method stub
				
			}
		});
        UnityAds.changeActivity((Activity)this);
	}
	
	public boolean isAvailableUnityAds() {
		if (UnityAds.canShow()) {
		    return true;
		} else {
			return false;
		}
	}
	
	public void showUnityAds() {
		UnityAds.show();
	}
	
	@Override
	public synchronized void onWindowFocusChanged(boolean pHasWindowFocus) {
		super.onWindowFocusChanged(pHasWindowFocus);
		if (pHasWindowFocus) {
			decorView.setSystemUiVisibility(flags);
		}
		
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new BoundCamera(0, 0, 1280, 720);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), this.camera);
		engineOptions.getAudioOptions().setNeedsSound(true);
		engineOptions.getRenderOptions().setDithering(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		return engineOptions;
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		Chartboost.onPause(this);
		SceneManager.getInstance().getCurrentScene().handleOnPause();
		mEngine.getSoundManager().setMasterVolume(0);
	}
	
	@Override
	protected synchronized void onResume() {
		super.onResume();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		int soundEnabled = sharedPreferences.getInt("soundEnabled", 0);
		
		Chartboost.onResume(this);
		UnityAds.changeActivity(this);
		
		if (soundEnabled == 1) {
			enableSound(false);
		} else if (soundEnabled == 0) {
			enableSound(true);
		}
	}
	
	public void enableSound(boolean enable) {
		if (enable) {
			mEngine.getSoundManager().setMasterVolume(1);
		} else {
			mEngine.getSoundManager().setMasterVolume(0);
		}
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback)	throws IOException {
		ResourcesManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)	throws IOException {
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);		
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {
		mEngine.registerUpdateHandler(new TimerHandler(SPLASH_DURATION, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				SceneManager.getInstance().createMenuScene();
			}
		}));
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		return new LimitedFPSEngine(pEngineOptions, 60);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Chartboost.onDestroy(this);
		System.exit(0);
	}
	
	public void tweetScore(Intent intent) {
		startActivity(Intent.createChooser(intent, "Protect the town"));
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (Chartboost.onBackPressed()) {
				 return false;
			 } else {
				 SceneManager.getInstance().getCurrentScene().onBackKeyPressed(); 
			 }
		}
		return false;
	}

    @Override
	protected void onStart() {
		super.onStart();
		Chartboost.onStart(this);
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Chartboost.onStop(this);
		mGoogleApiClient.disconnect();
	}

	public String getHighestScoreLeaderboardID() {
		return HIGHEST_SCORE_LEADERBOARD_ID;
	}
	
	public GoogleApiClient getGoogleApiClient() {
		return mGoogleApiClient;
	}
	
	public InterstitialAd getAdmobInterstitialAd() {
		return mInterstitialAd;
	}
	
	public String getAdmobAdUnitId() {
		return ADMOB_AD_UNIT_ID;
	}
	
	public void displayLeaderboard() {
		if (!mGoogleApiClient.isConnected()) {
			mGoogleApiClient.connect();
		}
		startActivityForResult(Games.Leaderboards.getLeaderboardIntent(this.getGoogleApiClient(),
		        this.getHighestScoreLeaderboardID()), RC_LEADERBOARD);
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (mResolvingConnectionFailure) {
	        // Already resolving
	        return;
	    }
		
		if (mAutoStartSignInFlow) {
	        mAutoStartSignInFlow = false;
	        mResolvingConnectionFailure = true;

	        // Attempt to resolve the connection failure using BaseGameUtils.
	        // The R.string.signin_other_error value should reference a generic
	        // error string in your strings.xml file, such as "There was
	        // an issue with sign in, please try again later."
	        if (!BaseGameUtils.resolveConnectionFailure(this,
	                mGoogleApiClient, connectionResult,
	                RC_SIGN_IN, SIGN_IN_OTHER_ERROR)) {
	            mResolvingConnectionFailure = false;
	        }
	    }

	}

	@Override
	public void onConnected(Bundle arg0) {
		// The player is signed in. Hide the sign-in button and allow the
	    // player to proceed.
	}

	@Override
	public void onConnectionSuspended(int i) {
	    // Attempt to reconnect
	    mGoogleApiClient.connect();
	}
}
