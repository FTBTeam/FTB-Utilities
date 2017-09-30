package com.feed_the_beast.ftbu.gui.guide;

import com.feed_the_beast.ftbu.api.guide.GuideType;
import com.feed_the_beast.ftbu.api.guide.IGuideTitlePage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuideTitlePage extends GuidePage implements IGuideTitlePage
{
	private final GuideType type;
	private final List<String> authors;
	private final List<String> guideAuthors;

	public GuideTitlePage(String id, GuideType t)
	{
		super(id, Guides.INFO_PAGE);
		type = t;
		authors = new ArrayList<>();
		guideAuthors = new ArrayList<>();
	}

	@Override
	public GuideType getType()
	{
		return type;
	}

	@Override
	public List<String> getAuthors()
	{
		return authors;
	}

	@Override
	public List<String> getGuideAuthors()
	{
		return guideAuthors;
	}
}