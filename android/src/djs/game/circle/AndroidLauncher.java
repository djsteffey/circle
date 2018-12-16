package djs.game.circle;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class AndroidLauncher extends AndroidApplication implements IPlatformServices{
	// constants
	private static final String TAG = AndroidLauncher.class.toString();
	private static final int RC_SIGN_IN = 9001;
	private static final int RC_UNUSED = 5001;

	// variables
	private InterstitialAd m_interstitial_ad;
	private IAdListener m_interstitial_ad_listener;
	private GoogleSignInClient m_sign_in_client;
	private LeaderboardsClient m_leaderboards_client;
	private CGame m_game;
	private boolean m_sign_in_for_leaderboards;
	private long m_last_on_resume_signin_time;



	// functions
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		// super me
		super.onCreate(savedInstanceState);

		// init ads
//		MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");	// test
		MobileAds.initialize(this, "ca-app-pub-5673239259906583~1695347591");	// real
		this.m_interstitial_ad = new InterstitialAd(this);
//		this.m_interstitial_ad.setAdUnitId("ca-app-pub-3940256099942544/1033173712");		// test
		this.m_interstitial_ad.setAdUnitId("ca-app-pub-5673239259906583/3746795865");		// real
		this.m_interstitial_ad.loadAd(new AdRequest.Builder().build());
		this.m_interstitial_ad.setAdListener(
				new AdListener(){
					@Override
					public void onAdClosed(){
						// inform it is closed
						if (AndroidLauncher.this.m_interstitial_ad_listener != null) {
							AndroidLauncher.this.m_interstitial_ad_listener.on_ad_complete();
							AndroidLauncher.this.m_interstitial_ad_listener = null;
						}

						// start loading another
						AdRequest.Builder ar = new AdRequest.Builder();
						AndroidLauncher.this.m_interstitial_ad.loadAd(ar.build());
					}
				}
		);
		this.m_interstitial_ad_listener = null;

		// sign in client
		this.m_sign_in_client = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());
		this.m_sign_in_for_leaderboards = false;
		this.m_last_on_resume_signin_time = 0;

		// create the config
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;

		// create the game
		this.m_game = new CGame(this);

		// start it up
		initialize(this.m_game, config);
	}

	@Override
	public void onResume(){
		Log.d(TAG, "onResume()");

		super.onResume();

		// only try to sign in every hour onResume
		long now = System.currentTimeMillis();
		if (now - this.m_last_on_resume_signin_time >= 1000 * 60 * 60){
			// one hour
			this.m_last_on_resume_signin_time = now;
			this.sign_in_silently();
		}
	}

	private boolean is_signed_in(){
		return GoogleSignIn.getLastSignedInAccount(this) != null;
	}

	private void sign_in_silently(){
		Log.d(TAG, "sign_in_silently()");

		this.m_sign_in_client.silentSignIn().addOnCompleteListener(
				this,
				new OnCompleteListener<GoogleSignInAccount>() {
					@Override
					public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
						if (task.isSuccessful()){
							// gtg
							Log.d(TAG, "sign_in_silently(): success");
							AndroidLauncher.this.on_connected(task.getResult());
						}
						else{
							// failed silent sign in
							Log.d(TAG, "sign_in_silently(): failed: " + task.getException());
							AndroidLauncher.this.on_disconnected();

							// normal sign in
							AndroidLauncher.this.start_sign_in_intent();
						}
					}
				}
		);
	}

	private void start_sign_in_intent(){
		Log.d(TAG, "start_sign_in_intent()");
		this.startActivityForResult(this.m_sign_in_client.getSignInIntent(), RC_SIGN_IN);
	}

	@Override
	public void onActivityResult(int request_code, int result_code, Intent intent){
		super.onActivityResult(request_code, result_code, intent);

		if (request_code == RC_SIGN_IN) {
			Log.d(TAG, "onActivityResult(): RC_SIGN_IN");
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
			try {
				GoogleSignInAccount account = task.getResult(ApiException.class);
				AndroidLauncher.this.on_connected(account);
			}
			catch (ApiException api_exception){
				AndroidLauncher.this.on_disconnected();
			}
		}
	}

	private void on_connected(GoogleSignInAccount account) {
		Log.d(TAG, "on_connected()");

		// get leaderboard client
		this.m_leaderboards_client = Games.getLeaderboardsClient(this, account);

		// update leaderboards if needed
		this.push_leaderboards();

		// show leaderboards if it was requested
		if (this.m_sign_in_for_leaderboards){
			this.m_sign_in_for_leaderboards = false;
			this.show_leaderboards();
		}
	}

	private void on_disconnected(){
		Log.d(TAG, "on_disconnected()");

		// clear leaderboard client
		this.m_leaderboards_client = null;
	}

	private void push_leaderboards(){
		Log.d(TAG, "push_leaderboards()");

		// make sure signed in
		if (this.is_signed_in() == false){
			// cant
			Log.d(TAG, "push_leaderboards(): not signed in");
			return;
		}

		// make sure we have a client
		if (this.m_leaderboards_client == null){
			// cant
			Log.d(TAG, "push_leaderboards(): no leaderboard client");
			return;
		}

		// make sure we have the state
		if (this.m_game.get_save_state() == null){
			// cant
			Log.d(TAG, "push_leaderboards(): no save state");
			return;
		}

		// upload them all
		if (this.m_game.get_save_state().get_outbox_total_games_played() != 0){
			this.m_leaderboards_client.submitScore(this.getString(R.string.leaderboard_total_games), this.m_game.get_save_state().get_outbox_total_games_played());
		}
		if (this.m_game.get_save_state().get_outbox_total_revolutions() != 0){
			this.m_leaderboards_client.submitScore(this.getString(R.string.leaderboard_total_revolutions), this.m_game.get_save_state().get_outbox_total_revolutions());
		}
		if (this.m_game.get_save_state().get_outbox_total_spikes_jumped() != 0){
			this.m_leaderboards_client.submitScore(this.getString(R.string.leaderboard_total_jumped_spikes), this.m_game.get_save_state().get_outbox_total_spikes_jumped());
		}
		if (this.m_game.get_save_state().get_outbox_total_scores() != 0){
			this.m_leaderboards_client.submitScore(this.getString(R.string.leaderboard_total_scores), this.m_game.get_save_state().get_outbox_total_scores());
		}
		if (this.m_game.get_save_state().get_outbox_max_revolutions() != 0){
			this.m_leaderboards_client.submitScore(this.getString(R.string.leaderboard_high_revolutions), this.m_game.get_save_state().get_outbox_max_revolutions());
		}
		if (this.m_game.get_save_state().get_outbox_max_spikes_jumped() != 0){
			this.m_leaderboards_client.submitScore(this.getString(R.string.leaderboard_high_jumped_spikes), this.m_game.get_save_state().get_outbox_max_spikes_jumped());
		}
		if (this.m_game.get_save_state().get_outbox_max_score() != 0){
			this.m_leaderboards_client.submitScore(this.getString(R.string.leaderboard_high_score), this.m_game.get_save_state().get_outbox_max_score());
		}

		// clear the outbox
		this.m_game.get_save_state().clear_outbox();
	}

	// iplatformservices
	@Override
	public void open_rate(){
		Log.d(TAG, "open_rate()");

		Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.addFlags(
				Intent.FLAG_ACTIVITY_NO_HISTORY |
						Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		try {
			this.startActivity(intent);
		}
		catch (ActivityNotFoundException e){
			e.printStackTrace();
			this.startActivity(
					new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())
					)
			);
		}
	}
	@Override
	public void show_ad(IAdListener listener){
		Log.d(TAG, "show_ad()");

		// set the listener
		this.m_interstitial_ad_listener = listener;

		// see if we can do it
		AndroidLauncher.this.runOnUiThread(
				new Runnable() {
					@Override
					public void run() {
						// is ad loaded?
						if (AndroidLauncher.this.m_interstitial_ad.isLoaded()){
							// show it
							AndroidLauncher.this.m_interstitial_ad.show();
						}
						else{
							// start loading one
							AdRequest.Builder ar = new AdRequest.Builder();
							AndroidLauncher.this.m_interstitial_ad.loadAd(ar.build());

							// no ad loaded yet...just tell finished right away
							if (AndroidLauncher.this.m_interstitial_ad_listener != null) {
								AndroidLauncher.this.m_interstitial_ad_listener.on_ad_complete();
								AndroidLauncher.this.m_interstitial_ad_listener = null;
							}
						}
					}
				}
		);
	}
	@Override
	public void update_leaderboards(){
		Log.d(TAG, "update_leaderboards()");

		this.push_leaderboards();
	}
	@Override
	public void show_leaderboards(){
		Log.d(TAG, "show_leaderboards()");

		if (this.is_signed_in() == false){
			this.m_sign_in_for_leaderboards = true;
			this.sign_in_silently();
			return;
		}

		if (this.m_leaderboards_client == null){
			this.m_sign_in_for_leaderboards = true;
			this.sign_in_silently();
			return;
		}

		this.m_leaderboards_client.getAllLeaderboardsIntent()
				.addOnSuccessListener(
						new OnSuccessListener<Intent>() {
							@Override
							public void onSuccess(Intent intent) {
								Log.d(TAG, "show_leaderboards(): success");
								AndroidLauncher.this.startActivityForResult(intent, RC_UNUSED);
							}
						}
				)
				.addOnFailureListener(
						new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception e) {
								Log.d(TAG, "show_leaderboards(): failed: " + e.toString());
							}
						}
				);
	}
	// iplatformservices end
}
