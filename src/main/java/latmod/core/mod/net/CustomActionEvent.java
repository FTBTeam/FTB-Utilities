package latmod.core.mod.net;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.relauncher.Side;

@Cancelable
public class CustomActionEvent extends Event
{
	public final EntityPlayer player;
	public final String channel;
	public final String action;
	public final NBTTagCompound extraData;
	public final Side side;
	
	public CustomActionEvent(EntityPlayer ep, String c, String a, NBTTagCompound d, Side s)
	{
		player = ep;
		channel = c;
		action = a;
		extraData = d;
		side = s;
	}
}