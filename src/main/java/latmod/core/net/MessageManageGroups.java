package latmod.core.net;
import latmod.core.*;
import latmod.core.mod.LC;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageManageGroups extends MessageLM implements IMessageHandler<MessageManageGroups, IMessage>
{
	public static final String C_RESET = "R";
	public static final String C_ADD_FRIEND = "AF";
	public static final String C_REM_FRIEND = "RF";
	public static final String C_ADD_GROUP = "AG";
	public static final String C_REM_GROUP = "RG";
	public static final String C_ADD_TO_GROUP = "A2G";
	public static final String C_REM_FROM_GROUP = "R2G";
	
	public MessageManageGroups() { }
	
	public MessageManageGroups(LMPlayer p, String c, String d)
	{
		data = new NBTTagCompound();
		data.setString("U", p.username);
		if(c != null) data.setString("C", c);
		if(d != null) data.setString("D", d);
	}
	
	public IMessage onMessage(MessageManageGroups m, MessageContext ctx)
	{
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		
		if(ep.getCommandSenderName().equals(m.data.getString("U")))
		{
			LMPlayer owner = LMPlayer.getPlayer(ep);
			
			if(owner != null)
			{
				String c = m.data.getString("C");
				String d = m.data.getString("D");
				boolean changed = false;
				
				if(!c.isEmpty())
				{
					if(c.equals(C_RESET))
					{
						if(!owner.friends.isEmpty())
						{
							owner.friends.clear();
							changed = true;
							LatCoreMC.sendMessage(ep, EnumChatFormatting.DARK_RED + "Groups reset", "", new ItemStack(Items.iron_sword), 2000L);
						}
					}
					else if(c.equals(C_ADD_FRIEND))
					{
						LMPlayer p = LMPlayer.getPlayer(d);
						
						if(p != null && !owner.friends.contains(p))
						{
							owner.friends.add(p);
							changed = true;
							LC.proxy.displayMessage(EnumChatFormatting.GREEN + "+ " + p.getDisplayName(), "Friends", new ItemStack(Blocks.emerald_block), 1500L);
						}
					}
					else if(c.equals(C_REM_FRIEND))
					{
						LMPlayer p = LMPlayer.getPlayer(d);
						
						if(p != null && owner.friends.contains(p))
						{
							owner.friends.remove(p);
							changed = true;
							LC.proxy.displayMessage(EnumChatFormatting.RED + "- " + p.getDisplayName(), "Friends", new ItemStack(Blocks.redstone_block), 1500L);
						}
						else if(p != null && p.friends.contains(owner))
						{
							p.friends.remove(owner);
							changed = true;
							p.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED);
							LC.proxy.displayMessage(EnumChatFormatting.RED + "- " + p.getDisplayName(), "Friends", new ItemStack(Blocks.redstone_block), 1500L);
						}
					}
				}
				else
				{
					LatCoreMC.sendMessage(ep, EnumChatFormatting.AQUA + "Players saved", "", new ItemStack(Items.diamond), 2000L);
					changed = true;
				}
				
				if(changed) owner.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED);
			}
		}
		return null;
	}
}