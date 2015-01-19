package com.T10006.GhostHunter;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

@SuppressLint({ "ShowToast", "DrawAllocation" })
public class LevelOneActivity extends Activity implements SensorEventListener, OnTouchListener{

	private GameView g;
	private SensorManager mSensorManager;
	private android.hardware.Sensor mSensor;
	float xv, yv= 0;
	private Bitmap ghosty;
	private final float[] deltaRotationMatrix = new float[16];
	private Bitmap skeleton;
	private Bitmap zombie;
	private Bitmap wizard;
	private Bitmap bomb;
	private Bitmap repel;
	private Bitmap stun;
	private int score = 100;
	private Bitmap tokenbit;
	private Vibrator vib;
	private MediaPlayer music;
	private int character = 0;
	private SoundPool soundPool;
	private int ghostdie;
	private int bang;
	private int scream;
	private int laugh;
	private int coin;
	private int pin;
	boolean plays = false, loaded = false;
	float actVolume, maxVolume, volume;
	AudioManager audioManager;
	int counter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Sets up vibration
		vib = (Vibrator) getSystemService(LevelOneActivity.VIBRATOR_SERVICE);
		
		//Sets up Fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//Sets up mediaplayer
		music= MediaPlayer.create(this, R.raw.haunting);
		music.setLooping(true);
		music.start();

		//AudioManager Settings for adjusting volume
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		volume = actVolume / maxVolume;
		
		//Hardware buttons setting to adjust mediasound
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		//counter for the stream id of the sound
		counter = 0;
		
		//load the sounds
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
					loaded = true;
			}
		});
		scream = soundPool.load(this, R.raw.scream, 1);
		ghostdie = soundPool.load(this, R.raw.ghostdie, 1);
		laugh = soundPool.load(this, R.raw.laugh, 1);
		bang = soundPool.load(this, R.raw.bang, 1);
		coin = soundPool.load(this, R.raw.coin, 1);
		pin = soundPool.load(this, R.raw.pin, 1);
		
		//Initializes bitmaps
		skeleton = BitmapFactory.decodeResource(getResources(), R.drawable.skeletonspritesheet);
		zombie = BitmapFactory.decodeResource(getResources(), R.drawable.zombie);
		wizard = BitmapFactory.decodeResource(getResources(), R.drawable.wizardspritesheet);
		ghosty = BitmapFactory.decodeResource(getResources(), R.drawable.ghostspritesheet);
		bomb = BitmapFactory.decodeResource(getResources(), R.drawable.flashbang);
		repel = BitmapFactory.decodeResource(getResources(), R.drawable.repellent);
		stun = BitmapFactory.decodeResource(getResources(), R.drawable.freeze);
		tokenbit = BitmapFactory.decodeResource(getResources(), R.drawable.token);		
		
		//Sets up GameView
		g = new GameView(this);
		g.setOnTouchListener(this);
		setContentView(R.layout.characters);

		//Sets up sensor
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
		
		//Fixed view as landscape
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	public class GameView extends SurfaceView implements Runnable{
		
		Thread t = null;
		SurfaceHolder holder;
		boolean oK = false;
		MainChar sprite;
		boolean spriteLoaded = false;
		List<Ghost> ghosts = new ArrayList<Ghost>();
		private Bitmap[] mainChar = {skeleton, zombie, wizard};
		int timer = 0;
		private int kills = 0;
		Paint p = new Paint();
		Paint w = new Paint();
		private boolean tokenLoaded;
		private Token token;
		boolean gameover = false;
		public GameView(Context context) {
			super(context);
			holder = getHolder();
		}
		@SuppressLint("WrongCall")
		@Override
		public void run() {
			token = new Token(GameView.this, tokenbit);
			sprite = new MainChar(GameView.this, mainChar[character], 500, 500, 0, 0);
			ghosts.add(new Ghost(GameView.this, ghosty, 200, 300, 5, 0));
			ghosts.add(new Ghost(GameView.this, ghosty, 100, 500, 5, 0));
			ghosts.add(new Ghost(GameView.this, ghosty, 300, 100, 5, 0));
			ghosts.add(new Ghost(GameView.this, ghosty, 600, 200, -5, 0));
			ghosts.add(new Ghost(GameView.this, ghosty, 200, 400, 0, -5));
			ghosts.add(new Ghost(GameView.this, ghosty, 600, 600, 0, 5));
			p.setColor(Color.BLACK);
			w.setColor(Color.WHITE);
			w.setTextSize(40);
			while (oK == true) {
				
				//Winning
				if(kills > 9) {
					nextLevel();
				}
				
				//Setting the bitmap for the buttons
				if(score < 100) bomb = BitmapFactory.decodeResource(getResources(), R.drawable.bombf);
				else if(sprite.bombt) bomb = BitmapFactory.decodeResource(getResources(), R.drawable.bombc);
				else bomb = BitmapFactory.decodeResource(getResources(), R.drawable.bomb);

				if(score < 50) repel = BitmapFactory.decodeResource(getResources(), R.drawable.repellentf);
				else if(sprite.bombt) repel = BitmapFactory.decodeResource(getResources(), R.drawable.repellentc);
				else repel = BitmapFactory.decodeResource(getResources(), R.drawable.repellent);
				
				if(score < 10) stun = BitmapFactory.decodeResource(getResources(), R.drawable.freezef);
				else if(sprite.stunt) stun = BitmapFactory.decodeResource(getResources(), R.drawable.freezec);
				else stun = BitmapFactory.decodeResource(getResources(), R.drawable.freeze);
				
				if(score < 1) sprite.notTouch();
				
				if(sprite.touch) score -= 1;
				
				//effect for the bomb
				if(sprite.boom) {
					soundPool.play(bang, volume, volume, 1, 0, 1f);
					for (int i = 0 ; i < ghosts.size(); ++i) {
						if(sprite.bomb.intersect(ghosts.get(i).dst)) {
								ghosts.remove(i);
								score += 100;
								kills++;
						}
					}
					sprite.boomDone();
					vib.vibrate(1000);
				}
				
				//effect for the repellent
				if(sprite.flee) {
					for (int i = 0 ; i < ghosts.size(); ++i) {
						if(sprite.bomb.intersect(ghosts.get(i).dst)) {
								ghosts.get(i).moveFlee();
						}
					}
					sprite.fleeDone();
				}
				
				//effect for the stunner
				if(sprite.freeze) {
					for (int i = 0 ; i < ghosts.size(); ++i) {
						soundPool.play(pin, volume, volume, 1, 0, 1f);
						if(sprite.bomb.intersect(ghosts.get(i).dst)) {
								ghosts.get(i).moveStop();
						}
					}
					sprite.freezeDone();
				}
				if (ghosts.size() < 6 && kills < 30) {
					int i = 0;
					int j = 0;
					double r = Math.random();
					if(r < 25) {
						i = 5;
						j = 0;
					}
					else if(r < 50) {
						i = 0;
						j = -5;
					}
					else if(r < 75) {
						i = 0;
						j = 5;
					}
					else{
						i = -5;
						j = 0;
					}
					ghosts.add(new Ghost(GameView.this, ghosty, (int)(Math.random() * (this.getWidth() - 153)), (int)(Math.random() * (this.getWidth() - 153)), i, j));
				}
				
				//colision for ghosts
				for (int i = 0 ; i < ghosts.size(); ++i) {
					if(sprite.dst.intersect(ghosts.get(i).dst)) {
						if(sprite.touch) {
							ghosts.remove(i);
							score += 100;
							soundPool.play(ghostdie, volume, volume, 1, 0, 1f);
							kills++;
						}
						else {
							sprite.die();
							if (score > 100) {
								soundPool.play(scream, volume, volume, 1, 0, 1f);
								sprite = new MainChar(GameView.this, mainChar[character], 500, 500, 0, 0);
								score -= 100;
							}
							else {
								soundPool.play(laugh, volume, volume, 1, 0, 1f);
								gameOver();
							}
						}
					
					}
				}

				//collision for token
				if(sprite.dst.intersect(token.dst)) {
					token.collide();
					soundPool.play(coin, volume, volume, 1, 0, 1f);
					score += 100;
				}
				
				//sets the state of the sprite
				if ( yv > .2 || yv < -.2 || xv > .1 || xv < -.1) {
					if (yv > .2) sprite.setState(2);
					if (yv < -.2) sprite.setState(4);
					if (xv > .1) sprite.setState(3);
					if (xv < -.1) sprite.setState(1);
				}
				else sprite.setState(6);
				
				if(!holder.getSurface().isValid()) continue;
				if(!spriteLoaded) {
				spriteLoaded = true;
				}
				if(!tokenLoaded){
					tokenLoaded = true;
				}
				
				Canvas c = holder.lockCanvas();
				onDraw(c);
				holder.unlockCanvasAndPost(c);
			}
		}
		
			@SuppressLint("WrongCall")
		protected void onDraw(Canvas canvas) {
			Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.graveyard);
			background = Bitmap.createScaledBitmap(background, this.getWidth(), this.getHeight(), true);
			canvas.drawARGB(255, 255, 255, 255);
			canvas.drawBitmap(background, 0, 0, null);
			sprite.onDraw(canvas);
			for (int i = 0; i < ghosts.size(); ++i) {
				ghosts.get(i).onDraw(canvas);
			}
			canvas.drawRect(this.getWidth() - bomb.getWidth() - 20, 0, this.getWidth(), this.getHeight(), p);
			canvas.drawBitmap(bomb, this.getWidth() - bomb.getWidth() - 10, 10, null);
			canvas.drawBitmap(repel, this.getWidth() - bomb.getWidth() - 10, bomb.getHeight() + 20, null);
			canvas.drawBitmap(stun, this.getWidth() - bomb.getWidth() - 10, repel.getHeight() + bomb.getHeight() + 30, null);
			canvas.drawText(Integer.toString(kills) + " / 10", this.getWidth() - bomb.getWidth(), this.getHeight() - 200, w);
			canvas.drawText(Integer.toString(score), this.getWidth() - bomb.getWidth(), this.getHeight() - 100, w);
			token.onDraw(canvas);
		}
		
		public void pause() {
			oK = false;
			while(true) {
				try{
					t.join();
				}
				catch(InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
			t = null;
		}
		public void resume() {
			oK = true;
			t = new Thread(this);
			t.start();
		}
	}
	
	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(this);
		music.stop();
		super.onStop();
	}

	@Override
	protected void onResume() {
		mSensorManager.registerListener((SensorEventListener) this, mSensor, SensorManager.SENSOR_DELAY_GAME);
		g.resume();
		music.start();
		super.onResume();

	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(this);
		g.pause();
		music.pause();
		super.onPause();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, event.values);
		yv = deltaRotationMatrix[0];
		xv = deltaRotationMatrix[1];				
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			g.sprite.notTouch();
			if(g.sprite.bombt) {
				g.sprite.offBomb();
				bomb = BitmapFactory.decodeResource(getResources(), R.drawable.flashbang);
				score -= 100;
			}
			if(g.sprite.repelt) {
				g.sprite.offRepel();
				repel = BitmapFactory.decodeResource(getResources(), R.drawable.repellent);
				score -= 50;
			}
			if(g.sprite.stunt) {
				g.sprite.offStun();
				stun = BitmapFactory.decodeResource(getResources(), R.drawable.freeze);
				score -= 10;
			}
			return false;
		}
		if(score > 0) {
			float xt = event.getX();
			float yt = event.getY();
			if(xt > g.getWidth() - bomb.getWidth() - 10 && xt < g.getWidth() - 10) {
				if(yt > 10 && yt < bomb.getHeight() + 10 && score > 99) {
					bomb = BitmapFactory.decodeResource(getResources(), R.drawable.bombc);
					g.sprite.clickBomb();
				}
				else if(yt > 20 + bomb.getHeight() && yt < 20 + repel.getHeight() + bomb.getHeight() && score > 49) {
					repel = BitmapFactory.decodeResource(getResources(), R.drawable.repellentc);
					g.sprite.clickRepel();
				}
				else if(yt > 30 + bomb.getHeight() + repel.getHeight() && yt < 20 + stun.getHeight() + repel.getHeight() +bomb.getHeight() && score > 9) {
					stun = BitmapFactory.decodeResource(getResources(), R.drawable.freezec);
					g.sprite.clickStun();
				}
			}
			else g.sprite.isTouch();
		
			
		}
		else g.sprite.notTouch();
		return true;
	}
	public void startGameS(View view) {
		setContentView(g);
		character = 0;
	}
	
	public void startGameZ(View view) {
		setContentView(g);
		character = 1;
	}
	
	public void startGameW(View view) {
		setContentView(g);
		character = 2;
	}
	
	public void gameOver() {
		this.startActivity(new Intent(LevelOneActivity.this, GameOverActivity.class));
		finish();
	}
	
	public void nextLevel() {
		this.startActivity(new Intent(LevelOneActivity.this, WinActivity.class));
		finish();
	}
}

 