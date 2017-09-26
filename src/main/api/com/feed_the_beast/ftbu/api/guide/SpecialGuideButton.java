package com.feed_the_beast.ftbu.api.guide;

import com.feed_the_beast.ftbl.lib.icon.Icon;
import com.feed_the_beast.ftbl.lib.util.JsonUtils;
import com.google.gson.JsonObject;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;

/**
 * @author LatvianModder
 */
public class SpecialGuideButton
{
	public final ITextComponent title;
	public final Icon icon;
	public final ClickEvent clickEvent;

	public SpecialGuideButton(ITextComponent t, Icon icn, ClickEvent c)
	{
		title = t;
		icon = icn;
		clickEvent = c;
	}

	public SpecialGuideButton(JsonObject o)
	{
		title = o.has("title") ? JsonUtils.deserializeTextComponent(o.get("title")) : new TextComponentString("");
		icon = o.has("icon") ? Icon.getIcon(o.get("icon")) : Icon.EMPTY;
		clickEvent = o.has("click") ? JsonUtils.deserializeClickEvent(o.get("click")) : null;
	}
}