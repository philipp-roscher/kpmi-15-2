package org.sausagepan.prototyp.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.sausagepan.prototyp.KPMIPrototype;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Prototyp";
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new KPMIPrototype(), config);
	}
}
