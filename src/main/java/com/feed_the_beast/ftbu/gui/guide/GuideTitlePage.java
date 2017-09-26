package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbu.api.guide.GuideFormat;
import com.feed_the_beast.ftbu.api.guide.GuideType;
import com.feed_the_beast.ftbu.api.guide.IGuidePage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuideTitlePage
{
	public final IGuidePage page;
	public final GuideType type;
	public final GuideFormat format;
	public final List<String> authors;
	public final List<String> guideAuthors;

	public GuideTitlePage(IGuidePage _page, GuideType t, GuideFormat f)
	{
		page = _page;
		type = t;
		format = f;
		authors = new ArrayList<>();
		guideAuthors = new ArrayList<>();
	}
}