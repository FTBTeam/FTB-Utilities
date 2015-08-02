package latmod.ftbu.core.api;

import latmod.ftbu.core.util.FastList;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.relauncher.Side;

public class FTBUReloadableRegistry
{
	private static final FastList<IFTBUReloadable> list = new FastList<IFTBUReloadable>();
	
	public static void add(IFTBUReloadable i)
	{ if(i != null) list.add(i); }
	
	public static void remove(IFTBUReloadable i)
	{ list.remove(i); }
	
	public static void reload(Side s, ICommandSender sender)
	{
		for(IFTBUReloadable i : list)
		{
			try { i.onReloaded(s, sender); } catch(Exception e)
			{ sender.addChatMessage(new ChatComponentText("Error @ " + e.toString())); }
		}
	}
}