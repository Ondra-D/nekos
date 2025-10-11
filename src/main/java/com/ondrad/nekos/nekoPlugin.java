package com.ondrad.nekos;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@PluginDescriptor(
		name = "Nekos",
		description = "Displays a cute neko on the screen",
		tags = {"anime", "neko", "overlay", "catgirl", "kitsune", "cat"}
)
public class nekoPlugin extends Plugin {

	@Inject
	private nekoConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private GetRequest getRequest;

	@Inject
	private nekoOverlay overlay;

	private ScheduledExecutorService fetcherService;
	private ScheduledExecutorService displayExecutor;
	private ExecutorService fetchPool;
	private static final int BUFFER_SIZE = 5;
	private static final int HISTORY_SIZE = 1;
	private final Deque<BufferedImage> imageBuffer = new ArrayDeque<>(BUFFER_SIZE);
	private final List<BufferedImage> imageHistory = new LinkedList<>();
	private final AtomicBoolean running = new AtomicBoolean(false);
	private BufferedImage lastImage = null;


	@Override
	protected void startUp() {
		overlayManager.add(overlay);
		fetcherService = Executors.newSingleThreadScheduledExecutor();
		fetchPool = Executors.newFixedThreadPool(2);
		running.set(true);

		fetcherService.submit(this::imageFetcherLoop);

		if (!config.everyTick()) {
			startDisplayExecutor(0);
		}
	}

	private List<BufferedImage> fetchNekoImages() {
		try {
			return getRequest.GETRequest(getApiEndpoint());
		} catch (IOException e) {
			return null;
		}
	}

	private String getApiEndpoint() {
		switch (config.type()) {
			case NEKOS:
				return "https://nekos.life/api/v2/img/neko";
			case CATS:
				return "https://nekos.life/api/v2/img/meow";
			case KITSUNE:
				return "https://nekos.life/api/v2/img/fox_girl";
			default:
				return "https://nekos.life/api/v2/img/neko";
		}
	}

	private void imageFetcherLoop() {
		while (running.get()) {
			try {
				int space;
				synchronized (imageBuffer) {
					space = BUFFER_SIZE - imageBuffer.size();
				}
				if (space > 0) {
					int fetchCount = Math.min(space, 2);
					List<Future<List<BufferedImage>>> futures = new ArrayList<>();
					for (int i = 0; i < fetchCount; i++) {
						futures.add(fetchPool.submit(this::fetchNekoImages));
					}
					for (Future<List<BufferedImage>> f : futures) {
						try {
							List<BufferedImage> imgs = f.get(5, TimeUnit.SECONDS);
							if (imgs != null && !imgs.isEmpty()) {
								synchronized (imageBuffer) {
									for (BufferedImage image : imgs) {
										if (imageBuffer.size() >= BUFFER_SIZE) break;
										imageBuffer.addLast(image);
									}
								}
							}
						} catch (Exception e) {
							log.warn("Failed to fetch image batch", e);
						}
					}
				}
				Thread.sleep(300);
			} catch (Exception ex) {
				log.warn("Error in image fetcher loop", ex);
			}
		}
	}

	@Override
	protected void shutDown() {
		overlayManager.remove(overlay);
		running.set(false);
		if (fetcherService != null) {
			fetcherService.shutdownNow();
		}
		if (fetchPool != null) {
			fetchPool.shutdownNow();
		}
		if (displayExecutor != null) {
			displayExecutor.shutdownNow();
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick) {
		if (config.everyTick()) {
			displayNextImage();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (!event.getGroup().equals("nekoconfig")) {
			return;
		}

		boolean typeChanged = "type".equals(event.getKey());
		if (typeChanged) {
			synchronized (imageBuffer) {
				imageBuffer.clear();
			}
		}

		if (displayExecutor != null) {
			displayExecutor.shutdownNow();
			displayExecutor = null;
		}

		if (!config.everyTick()) {
			long delayMillis = typeChanged ? 1000 : 0;
			startDisplayExecutor(delayMillis);
		}
	}

	private void startDisplayExecutor(long initialDelayMillis) {
		displayExecutor = Executors.newSingleThreadScheduledExecutor();
		long delayMillis = Math.max(1L, (long) config.delaySeconds() * 1000);
		displayExecutor.scheduleAtFixedRate(this::displayNextImage, initialDelayMillis, delayMillis, TimeUnit.MILLISECONDS);
	}

	private void displayNextImage() {
		BufferedImage next = null;
		boolean bufferWasEmpty = false;
		synchronized (imageBuffer) {
			if (!imageBuffer.isEmpty()) {
				next = imageBuffer.pollFirst();
				synchronized (imageHistory) {
					if (next != null && (imageHistory.isEmpty() || imageHistory.get(imageHistory.size() - 1) != next)) {
						imageHistory.add(next);
						if (imageHistory.size() > HISTORY_SIZE) {
							imageHistory.remove(0);
						}
					}
				}
			} else {
				bufferWasEmpty = true;
			}
		}
		if (bufferWasEmpty) {
			BufferedImage alt = null;
			synchronized (imageHistory) {
				for (BufferedImage img : imageHistory) {
					if (img != lastImage) {
						alt = img;
						break;
					}
				}
			}
			if (alt != null) {
				overlay.updateImage(alt);
				return;
			}
		}
		if (next != null) {
			lastImage = next;
		}
		if (lastImage != null) {
			overlay.updateImage(lastImage);
		}
	}

	@Provides
	nekoConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(nekoConfig.class);
	}
}
