package com.feed_the_beast.ftbu.api.guide;

import com.feed_the_beast.ftbl.api.FTBLibEvent;
import net.minecraft.client.resources.IResourceManager;

import java.util.Map;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class ClientGuideEvent extends FTBLibEvent
{
	private final Map<String, GuideTitlePage> map;
	private final IResourceManager resourceManager;
	private final Function<String, GuideTitlePage> modGuideProvider;

	public ClientGuideEvent(Map<String, GuideTitlePage> m, IResourceManager r, Function<String, GuideTitlePage> f)
	{
		map = m;
		resourceManager = r;
		modGuideProvider = f;
	}

	public void add(GuideTitlePage page)
	{
		map.put(page.page.getName(), page);
	}

	public GuideTitlePage getModGuide(String modid)
	{
		return map.computeIfAbsent(modid, modGuideProvider);
	}

	public IResourceManager getResourceManager()
	{
		return resourceManager;
	}
}