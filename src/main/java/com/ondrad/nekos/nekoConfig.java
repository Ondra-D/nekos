package com.ondrad.nekos;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("nekoconfig")
public interface nekoConfig extends Config
{

	enum ImageType {
		NEKOS,
		CATS,
		KITSUNE
	}
		@ConfigItem(
				position = 1,
				keyName = "type",
				name = "Type:",
				description = "Choose the type of image to display"
		)
		default ImageType type() {
			return ImageType.NEKOS;
		}


	@ConfigItem(
			position = 5,
			keyName = "xpos",
			name = "X Position",
			description = "X position of the image"
	)
	default int xpos() { return 9; }

	@ConfigItem(
			position = 6,
			keyName = "ypos",
			name = "Y position",
			description = "Y position of the image"
	)
	default int ypos()
	{
		return 147;
	}

	@ConfigItem(
			position = 2,
			keyName = "delaySeconds",
			name = "Delay in seconds",
			description = "The delay between images in seconds"
	)
	default int delaySeconds()
	{
		return 10;
	}

	@ConfigItem(
			position = 3,
			keyName = "Dimensions",
			name = "Dimensions",
			description = "witdth and height of the image"
	)
	default Dimension dimension()
	{
		return new Dimension(200, 300);
	}

	@ConfigItem(
			position = 4,
			keyName = "opacity",
			name = "Opacity",
			description = "Opacity of the image in %"
	)
	default int opacity()
	{
 		return 100;
	}

}
