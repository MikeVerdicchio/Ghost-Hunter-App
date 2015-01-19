package com.T10006.GhostHunter;

import com.T10006.GhostHunter.LevelOneActivity.GameView;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Token {
	
	int posistionX, posistionY;
	int height, width;
	Bitmap t;
	GameView gv;
	int rotationAngle = 60;
	int counter=0;
	boolean collision = false;
	Rect dst = new Rect(0, 0, 0, 0);
	private int i = 0;

	
	public Token(GameView gameView, Bitmap tokenBit) {
		t = tokenBit;
		gv = gameView;
		height = tokenBit.getHeight();
		width = tokenBit.getWidth();
		posistionX = 600; 
		posistionY = 350;

	}

	@SuppressLint("DrawAllocation")
	public void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		update();
		Rect src =new Rect(0, 0, width, height);
		dst = new Rect (posistionX, posistionY, posistionX+width, posistionY+height );
		canvas.drawBitmap(t, src, dst, null);
		canvas.save();
		canvas.rotate(rotationAngle, posistionX + (width / 2), posistionY + (height / 2));
		canvas.drawBitmap(t, posistionX, posistionY, null);
		canvas.restore();
		
	}

	private void update() {
		++i;
		if(i == 6000) {
			posistionX = (int)(Math.random() * (gv.getWidth()-153)); 
			posistionY = (int)(Math.random() * (gv.getHeight()-153));
			i = 0;
		}
		if(collision) {
			posistionX = (int)(Math.random() * (gv.getWidth()-153)); 
			posistionY = (int)(Math.random() * (gv.getHeight()-153));
			i = 0;
			collision = false;
		}
	}

	public void collide() {
		collision = true;
	}
	
}