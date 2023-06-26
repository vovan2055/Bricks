package com.vovangames.bricks;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useCompass = false;
		config.useGyroscope = false;
		config.maxSimultaneousSounds = 4;
		config.useAccelerometer = true;
		initialize(new Main(), config);
	}
}
