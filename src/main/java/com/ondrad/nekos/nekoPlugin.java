package com.ondrad.nekos;

import com.google.inject.Provides;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.swing.Timer;

import java.io.IOException;

@Slf4j
@PluginDescriptor(
		name = "Nekos",
		description = "Displays a cute neko",
		tags = {"anime", "neko", "overlay", "catgirl"}
)
public class nekoPlugin extends Plugin {

	@Inject
	private nekoConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private nekoOverlay overlay;
	private Timer timer;


	@Override
	protected void startUp(){

		overlayManager.add(overlay);

		int delaySeconds = config.delaySeconds();
		int delayMillis = delaySeconds * 1000;

		timer = new Timer(delayMillis, e -> {
			try {
				overlay.GETRequest();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		});
		timer.start();

	}


	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		timer.stop();
	}


	@Provides
	nekoConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(nekoConfig.class);
	}
}
