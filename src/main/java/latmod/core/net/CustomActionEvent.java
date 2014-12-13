package latmod.core.net;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;

public class CustomActionEvent extends Event
{
	public final EntityPlayer player;
	public final String action;
	public final NBTTagCompound extraData;
	public final Side side;
	
	public CustomActionEvent(EntityPlayer ep, String a, NBTTagCompound d, Side s)
	{
		player = ep;
		action = a;
		extraData = d;
		side = s;
	}
	
	public void post()
	{ MinecraftForge.EVENT_BUS.post(this); }
}