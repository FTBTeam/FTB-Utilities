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
	private final Map<String, IGuideTitlePage> map;
	private final IResourceManager resourceManager;
	private final Function<String, IGuideTitlePage> modGuideProvider;

	public ClientGuideEvent(Map<String, IGuideTitlePage> m, IResourceManager r, Function<String, IGuideTitlePage> f)
	{
		map = m;
		resourceManager = r;
		modGuideProvider = f;
	}

	public void add(IGuideTitlePage page)
	{
		map.put(page.getName(), page);
	}

	public IGuideTitlePage getModGuide(String modid)
	{
		return map.computeIfAbsent(modid, modGuideProvider);
	}

	public IResourceManager getResourceManager()
	{
		return resourceManager;
	}
}