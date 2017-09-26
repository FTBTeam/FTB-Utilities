package com.feed_the_beast.ftbu.api.guide;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public interface IGuideGui
{
	IGuidePage getSelectedPage();

	void setSelectedPage(@Nullable IGuidePage page);
}