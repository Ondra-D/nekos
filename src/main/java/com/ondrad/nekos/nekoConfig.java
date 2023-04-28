package com.ondrad.nekos;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("nekoconfig")
public interface nekoConfig extends Config
{
	@ConfigItem(
			keyName = "xpos",
			name = "X Position",
			description = "X position of the image"
	)
	default int xpos()
	{
		return 9;
	}

	@ConfigItem(
			keyName = "ypos",
			name = "Y position",
			description = "Y position of the image"
	)
	default int ypos()
	{
		return 147;
	}

	@ConfigItem(
			keyName = "delaySeconds",
			name = "Delay in seconds",
			description = "The delay between images in seconds"
	)
	default int delaySeconds()
	{
		return 10;
	}

	@ConfigItem(
			keyName = "Dimensions",
			name = "Dimensions",
			description = "witdth and height of the image"
	)
	default Dimension dimension()
	{
		return new Dimension(350, 479);
	}

	@ConfigItem(
			keyName = "opacity",
			name = "Opacity",
			description = "Opacity of the image in %"
	)
	default int opacity()
	{
 		return 100;
	}

}
