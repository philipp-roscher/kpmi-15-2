package org.sausagepan.prototyp.network;

import org.mockito.Mockito;
import org.sausagepan.prototyp.model.ServerSettings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.backends.headless.HeadlessNativesLoader;
import com.badlogic.gdx.backends.headless.HeadlessNet;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGraphics;
import com.badlogic.gdx.graphics.GL20;

public class ServerLauncher {
	public static void main (String[] arg) {
		// Load gdx stuff
		HeadlessNativesLoader.load();
		MockGraphics mockGraphics = new MockGraphics();
		Gdx.graphics = mockGraphics;
		HeadlessNet headlessNet = new HeadlessNet();
		Gdx.net = headlessNet;
		HeadlessFiles headlessFiles = new HeadlessFiles();
		Gdx.files = headlessFiles;
		Gdx.gl = Mockito.mock(GL20.class);
		
		// Create new configuration
		HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
		// Set tickrate
		config.renderInterval = 1f / ServerSettings.TICKRATE;
		
		// Create server
		new HeadlessApplication(new GameServer(), config);
	}
}
