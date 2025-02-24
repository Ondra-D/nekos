package com.ondrad.nekos;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import okhttp3.OkHttpClient;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@PluginDescriptor(
		name = "Nekos",
		description = "Displays a cute neko on the screen",
		tags = {"anime", "neko", "overlay", "catgirl, kitsune, cat"}
)
public class nekoPlugin extends Plugin {

	@Inject
	private nekoConfig 	config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private nekoOverlay overlay;
	private ScheduledExecutorService executorService;

	@Override
	protected void startUp() {
		overlayManager.add(overlay);
		executorService = Executors.newSingleThreadScheduledExecutor();
		int delaySeconds = config.delaySeconds();
		executorService.scheduleAtFixedRate(this::fetchNekoImage, 0, delaySeconds, TimeUnit.SECONDS);
	}

	private void fetchNekoImage() {
		String endpoint;
		switch (config.type()) {
			case NEKOS:
				endpoint = "https://nekos.life/api/v2/img/neko";
				break;
			case CATS:
				endpoint = "https://nekos.life/api/v2/img/meow";
				break;
			case KITSUNE:
				endpoint = "https://nekos.life/api/v2/img/fox_girl";
				break;
			default:
				endpoint = "https://nekos.life/api/v2/img/neko";
				break;
		}

		try {
			OkHttpClient client = new OkHttpClient();
			GetRequest getRequest = new GetRequest(client);
			BufferedImage image = getRequest.GETRequest(endpoint);
			if (image != null) {
				overlay.updateImage(image);
			}
		} catch (IOException ex) {
			log.error("Failed to fetch image", ex);
		}
	}

	@Override
	protected void shutDown() {
		overlayManager.remove(overlay);
		if (executorService != null) {
			executorService.shutdown();
		}
	}

	@Provides
	nekoConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(nekoConfig.class);
	}
}