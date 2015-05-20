package latmod.core.mod;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import latmod.core.*;
import latmod.core.net.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class LCCommon extends LMProxy // LCClient
{
	public boolean isShiftDown() { return false; }
	public boolean isCtrlDown() { return false; }
	public boolean isTabDown() { return false; }
	public boolean inGameHasFocus() { return true; }
	
	public EntityPlayer getClientPlayer()
	{ return null; }
	
	public World getClientWorld()
	{ return null; }
	
	public double getReachDist(EntityPlayer ep)
	{
		if(ep instanceof EntityPlayerMP)
			return ((EntityPlayerMP)ep).theItemInWorldManager.getBlockReachDistance();
		return 0F;
	}
	
	public void spawnDust(World w, double x, double y, double z, int col) { }
	public void playerLMLoggedIn(LMPlayer p) { }
	public void playerLMLoggedOut(LMPlayer p) { }
	public void playerLMDataChanged(LMPlayer p, String action) { }
	public void openClientGui(EntityPlayer ep, String id, NBTTagCompound data) { }
	public <M extends MessageLM<?>> void handleClientMessage(IClientMessageLM<M> m, MessageContext ctx) { }
}