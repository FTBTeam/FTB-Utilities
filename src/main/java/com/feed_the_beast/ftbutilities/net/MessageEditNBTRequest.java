package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class MessageEditNBTRequest extends MessageToClient
{
	public MessageEditNBTRequest()
	{
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBUtilitiesNetHandler.FILES;
	}

	@Override
	public boolean hasData()
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		RayTraceResult ray = ClientUtils.MC.objectMouseOver;

		if (ray != null)
		{
			if (ray.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				ClientUtils.execClientCommand(StringJoiner.with(' ').joinObjects("/ftb nbtedit tile", ray.getBlockPos().getX(), ray.getBlockPos().getY(), ray.getBlockPos().getZ()));
			}
			else if (ray.typeOfHit == RayTraceResult.Type.ENTITY && ray.entityHit != null)
			{
				ClientUtils.execClientCommand("/ftb nbtedit entity " + ray.entityHit.getEntityId());
			}
		}
	}
}