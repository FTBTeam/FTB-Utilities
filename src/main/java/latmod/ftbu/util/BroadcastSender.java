package latmod.ftbu.util;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class BroadcastSender implements ICommandSender
{
	public static final BroadcastSender inst = new BroadcastSender();
	private static final ChunkCoordinates nullPos = new ChunkCoordinates(0, 66, 0);
	
	public String getCommandSenderName()
	{ return "[Server]"; }
	
	public IChatComponent func_145748_c_()
	{
		ChatComponentText c = new ChatComponentText(getCommandSenderName());
		c.getChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE); return c;
	}
	
	public void addChatMessage(IChatComponent ics)
	{ LatCoreMC.getServer().getConfigurationManager().sendChatMsgImpl(ics, true); }
	
	public boolean canCommandSenderUseCommand(int i, String s)
	{ return true; }
	
	public ChunkCoordinates getPlayerCoordinates()
	{ return nullPos; }
	
	public World getEntityWorld()
	{ return LatCoreMC.getServerWorld(); }
}