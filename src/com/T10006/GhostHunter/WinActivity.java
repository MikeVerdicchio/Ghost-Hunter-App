package com.T10006.GhostHunter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class WinActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_win);
	}

	public void startGame(View view) {
		this.startActivity(new Intent(WinActivity.this, MainActivity.class));
		finish();
	}
	
}
