package com.T10006.GhostHunter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.T10006.GhostHunter.R;

public class MainActivity extends Activity {

	private AnimationDrawable ghost;
	private MediaPlayer menum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		menum=MediaPlayer.create(MainActivity.this, R.raw.menu_music);
		ImageView ghostImage = (ImageView) findViewById(R.id.menughost);
		ghostImage.setBackgroundResource(R.drawable.menuanimation);;
		ghost = (AnimationDrawable) ghostImage.getBackground();
		ghost.start();
		menum.start();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	public void charChoose(View view) {
		ghost.stop();
		menum.stop();
		startActivity(new Intent(MainActivity.this, LevelOneActivity.class));	
	}
	
	@Override
	public void onPause(){
		super.onPause();
		menum.pause();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		menum.start();
	}
	
	public void goSplash(View view) {
		startActivity(new Intent(MainActivity.this, SplashScreen.class));
		finish();
	}
	
	public void toInstructions(View view) {
		setContentView(R.layout.instruct);
	}
	
	public void toMenu(View view) {
		setContentView(R.layout.activity_main);
	}
}