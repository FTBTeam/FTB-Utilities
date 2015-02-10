package latmod.core.net;
import static net.minecraft.util.EnumChatFormatting.*;
import latmod.core.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageManageGroups extends MessageLM implements IMessageHandler<MessageManageGroups, IMessage>
{
	public static final int C_ADD_FRIEND = 1;
	public static final int C_REM_FRIEND = 2;
	public static final int C_ADD_GROUP = 3;
	public static final int C_REM_GROUP = 4;
	public static final int C_ADD_TO_GROUP = 5;
	public static final int C_REM_FROM_GROUP = 6;
	public static final int C_RESET = 7;
	
	public MessageManageGroups() { }
	
	public MessageManageGroups(LMPlayer p, int c, String d)
	{
		data = new NBTTagCompound();
		data.setInteger("ID", p.playerID);
		if(c != -1) data.setByte("C", (byte)c);
		if(d != null) data.setString("D", d);
	}
	
	public IMessage onMessage(MessageManageGroups m, MessageContext ctx)
	{
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		
		LMPlayer owner = LMPlayer.getPlayer(ep);
		
		if(owner != null && owner.playerID == m.data.getInteger("ID"))
		{
			if(owner != null)
			{
				int c = m.data.getByte("C");
				String d = m.data.getString("D");
				boolean changed = false;
				
				if(c != 0)
				{
					if(c == C_RESET)
					{
						if(!owner.friends.isEmpty())
						{
							owner.friends.clear();
							changed = true;
							LatCoreMC.notifyPlayer(ep, new Notification(DARK_RED + "Groups reset", "", new ItemStack(Items.iron_sword), 2000L));
						}
					}
					else if(c == C_ADD_FRIEND)
					{
						LMPlayer p = LMPlayer.getPlayer(d);
						
						if(p != null && !owner.friends.contains(p))
						{
							owner.friends.add(p);
							changed = true;
							LatCoreMC.notifyPlayer(ep, new Notification(GREEN + "+ " + p.getDisplayName(), "Friends", new ItemStack(Blocks.emerald_block), 1500L));
						}
					}
					else if(c == C_REM_FRIEND)
					{
						LMPlayer p = LMPlayer.getPlayer(d);
						
						if(p != null && owner.friends.contains(p))
						{
							owner.friends.remove(p);
							changed = true;
							LatCoreMC.notifyPlayer(ep, new Notification(RED + "- " + p.getDisplayName(), "Friends", new ItemStack(Blocks.redstone_block), 1500L));
						}
						else if(p != null && p.friends.contains(owner))
						{
							p.friends.remove(owner);
							changed = true;
							p.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED);
							LatCoreMC.notifyPlayer(ep, new Notification(RED + "- " + p.getDisplayName(), "Friends", new ItemStack(Blocks.redstone_block), 1500L));
						}
					}
				}
				else
				{
					LatCoreMC.notifyPlayer(ep, new Notification(AQUA + "Players saved", "", new ItemStack(Items.diamond), 2000L));
					changed = true;
				}
				
				if(changed) owner.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED);
			}
		}
		return null;
	}
}