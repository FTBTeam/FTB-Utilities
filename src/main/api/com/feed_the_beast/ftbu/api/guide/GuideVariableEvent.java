package com.feed_the_beast.ftbu.api.guide;

import com.feed_the_beast.ftbl.api.FTBLibEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Cancelable
public class GuideVariableEvent extends FTBLibEvent
{
	private final Side side;
	private final IGuidePage page;
	private final ResourceLocation variable;
	private String value;

	public GuideVariableEvent(Side s, IGuidePage p, ResourceLocation id)
	{
		side = s;
		page = p;
		variable = id;
		value = "default";
	}

	public Side getSide()
	{
		return side;
	}

	public IGuidePage getPage()
	{
		return page;
	}

	public ResourceLocation getVariable()
	{
		return variable;
	}

	public void setValue(String val)
	{
		value = val;
	}

	public String getValue()
	{
		return value;
	}
}