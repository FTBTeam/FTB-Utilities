package com.feed_the_beast.ftbu.api.guide;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuideTitlePage
{
	public final IGuidePage page;
	public final GuideType type;
	public final List<String> authors;
	public final List<String> guideAuthors;

	public GuideTitlePage(IGuidePage _page, GuideType t)
	{
		page = _page;
		type = t;
		authors = new ArrayList<>();
		guideAuthors = new ArrayList<>();
	}
}