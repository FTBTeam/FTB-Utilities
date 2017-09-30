package com.feed_the_beast.ftbu.api.guide;

import java.util.List;

/**
 * @author LatvianModder
 */
public interface IGuideTitlePage extends IGuidePage
{
	GuideType getType();

	List<String> getAuthors();

	List<String> getGuideAuthors();
}