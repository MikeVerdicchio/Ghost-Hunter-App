package com.T10006.GhostHunter;

import com.T10006.GhostHunter.LevelOneActivity.GameView;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

@SuppressLint("DrawAllocation")
public class MainChar {

	int x, y, xv, yv, h, w;
	Bitmap s;
	GameView gv;
	int cF = 0;
	int d = 10;
	boolean touch = false;
	Rect dst = new Rect(0, 0, 0, 0);
	boolean collide = false;
	int state = 0;
	boolean dead = false;
	Rect bomb = new Rect(0, 0, 0, 0);
	boolean bombt = false;
	boolean stunt = false;
	boolean repelt = false;
	boolean boom = false;
	boolean freeze = false;
	boolean flee = false;
	
	public MainChar(GameView gameView, Bitmap skeleton, int xp, int yp, int xvp, int yvp) {
		s = skeleton;
		gv = gameView;
		h = (s.getHeight()/ 21) + 1; // number of rows 21
		w = (s.getWidth() / 13) + 1; // number of columns 13
		x = xp; 
		y = yp;
		xv = xvp;
		yv = yvp;
	}
	

	public void onDraw(Canvas canvas) {
		update(); 
		int srcY = d * h;
		int srcX = cF * w;
		Rect src = new Rect( srcX, srcY, srcX + w, srcY + h);
		dst = new Rect(x, y, x+w, y+h);
		bomb = new Rect(x-100, y-100, x+w+100, y+h+100);
		canvas.drawBitmap(s, src, dst, null);
	}

	private void update() {
		
		switch(state){
		case 1:	state = 1;
			moveRight();
			break;
		case 2:	state = 2;
			moveUp();
			break;
		case 3:	state = 3;
			moveLeft();
			break;
		case 4:	state = 4;
			moveDown();
			break;
		case 5: state = 5;
			moveStop();
			d = 20;
			break;
		case 6: state = 6;
			if(touch) break;
			moveStop();
		}
		
		if((x + w) > gv.getWidth() - 153 && state == 1) moveStop(); 
		if((y + h) > gv.getHeight() - 153 && state == 4) moveStop();
		if(x < 0 && state == 3) moveStop();
		if(y < 0 && state == 2) moveStop();
		
		if(collide) {
			this.moveStop();
			d = 20;
		}
		
		cF =  ++cF % 13;

		if (collide && cF == 6) cF = 5;
		
		
		if(dead) {
			xv = 0;
			yv = 0;
			d= 20; 
			if (cF > 5) cF = 5;
			x+=xv;
			y+=yv;
		}
		else if(touch) {
			xv = 0;
			yv = 0;
			if (d == 8) d= 12;
			if (d == 9) d= 13;
			if (d == 10) d= 14;
			if (d == 11) d= 15; 
			if (cF > 5) cF = 0;
			x+=xv;
			y+=yv;
		}
		else if (cF > 8) cF = 0;
		x+=xv;
		y+=yv;
		
		
	}
	
	public void relive() {
		dead = false;
	}
	
	public void isTouch(){
		touch = true;
	}
	public void notTouch(){
		touch = false;
	}
	
	public void setXY(float i, float j) {
		x = (int) i;
		y = (int) j;
	}
	
	public void moveRight() {
		yv = 0;
		xv = 10;
		d = 11;	
	}
	public void moveUp() {
		xv = 0;
		yv = -10;
		d = 8;		
	}
	public void moveLeft() {
		yv = 0;
		xv = -10;
		d = 9;	
	}
	public void moveDown() {
		xv = 0;
		yv = 10;
		d = 10;	
	}
	public void moveStop() {
		xv = 0;
		yv = 0;
		cF = 9;
	}
	public Rect getDST() {
		return dst;
	}
	
	public void detectCollision() {
		collide = true;
	}
	
	public void setState(int i) {
		state = i;
	}
	
	public void setBitmap(Bitmap b) {
		s = b;
	}
	
	public void die() {
		dead = true;
	}
	
	public void clickBomb() {
		bombt = true;
	}

	public void clickStun() {
		stunt = true;
	}
	
	public void clickRepel() {
		repelt = true;
	}
	
	public void offBomb() {
		bombt = false;
		boom = true;
	}

	public void offStun() {
		stunt = false;
		freeze =true;
	}
	
	public void offRepel() {
		repelt = false;
		flee = true;
	}
	
	public void boomDone() {
		boom = false;
	}

	public void fleeDone() {
		flee = false;
	}

	public void freezeDone() {
		freeze = false;
	}
}