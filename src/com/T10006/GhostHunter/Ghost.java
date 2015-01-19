package com.T10006.GhostHunter;

import com.T10006.GhostHunter.LevelOneActivity.GameView;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

@SuppressLint("DrawAllocation")
public class Ghost {

	int x, y, xv, yv, h, w;
	Bitmap s;
	GameView gv;
	int cF = 0;
	int d = 10;
	boolean touch = false;
	Rect dst = new Rect(0, 0, 0, 0);
	boolean collide = false;
	boolean flee = false;
	public Ghost(GameView gameView, Bitmap skeleton, int xp, int yp, int xvp, int yvp) {
		// TODO Auto-generated constructor stub
		s = skeleton;
		gv = gameView;
		h = (s.getHeight() / 21) + 1; // number of rows 21
		w = (s.getWidth() / 13) + 1; // number of columns 13
		x = (int)(Math.random() * (gv.getWidth())); 
		y = (int)(Math.random() * (gv.getHeight()));
		xv = 5;
		yv = 0;
	}

	public void onDraw(Canvas canvas) {
		update();

		int srcY = d * h;
		int srcX = cF * w;
		Rect src = new Rect(srcX, srcY, srcX + w, srcY + h);
		dst = new Rect(x, y, x + w, y + h);
		canvas.drawBitmap(s, src, dst, null);
	}

	private void update() {
		if(Math.random() > .98) {
			double i = Math.random();
			if (i < .25) this.moveUp();
			else if(i <.5) this.moveLeft();
			else if (i < .75) this.moveRight();
			else this.moveDown();
		}
		
		if((x + w) > gv.getWidth() - 153) {
			double i = Math.random();
			if (i < .33) this.moveUp();
			else if(i <.66) this.moveLeft();
			else this.moveDown();
		}
		if((y + h) > gv.getHeight() - 153) {
			double i = Math.random();
			if (i < .33) this.moveUp();
			else if(i <.66) this.moveLeft();
			else this.moveRight();
		}
		if(x < 0) {
			double i = Math.random();
			if (i < .33) this.moveUp();
			else if(i <.66) this.moveDown();
			else this.moveRight();
		}
		if(y < 0) {
			double i = Math.random();
			if (i < .33) this.moveRight();
			else if(i <.66) this.moveLeft();
			else this.moveDown();
		}
		
		if(flee) {
			if (x == 5) moveLeft();
			else if (x == -5) moveRight();
			else if (y == 5) moveUp();
			else if (y == -5) moveDown();
			flee = false;
 		}


		cF = ++cF % 13;

		if (collide && cF == 6)
			cF = 5;

		else if (cF > 8)
			cF = 0;
		x += xv;
		y += yv;
	}

	public void setXY(float i, float j) {
		x = (int) i;
		y = (int) j;
	}

	public void moveRight() {
		yv = 0;
		xv = 5;
		d = 11;
	}

	public void moveUp() {
		xv = 0;
		yv = -5;
		d = 8;
	}

	public void moveLeft() {
		yv = 0;
		xv = -5;
		d = 9;
	}

	public void moveDown() {
		xv = 0;
		yv = 5;
		d = 10;
	}

	public void moveStop() {
		xv = 0;
		yv = 0;
		cF = 9;
	}

	public void moveFlee() {
		flee = true;
	}
	
	public Rect getDST() {
		return dst;
	}

	public void setBitmap(Bitmap b) {
		s = b;
	}
}