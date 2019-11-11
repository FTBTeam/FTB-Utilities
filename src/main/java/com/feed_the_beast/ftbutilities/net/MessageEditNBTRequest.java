package com.feed_the_beast.ftbutilities.net;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;
import net.minecraft.client.Minecraft;
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
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		editNBT();
	}

	@SideOnly(Side.CLIENT)
	public static void editNBT()
	{
		RayTraceResult ray = Minecraft.getMinecraft().objectMouseOver;

		if (ray != null)
		{
			if (ray.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				ClientUtils.execClientCommand(StringJoiner.with(' ').joinObjects("/nbtedit block", ray.getBlockPos().getX(), ray.getBlockPos().getY(), ray.getBlockPos().getZ()));
			}
			else if (ray.typeOfHit == RayTraceResult.Type.ENTITY && ray.entityHit != null)
			{
				ClientUtils.execClientCommand("/nbtedit entity " + ray.entityHit.getEntityId());
			}
		}
	}
}