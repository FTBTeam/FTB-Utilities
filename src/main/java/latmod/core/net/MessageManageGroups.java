package latmod.core.net;
import static net.minecraft.util.EnumChatFormatting.AQUA;
import latmod.core.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.*;

public class MessageManageGroups extends MessageLM implements IMessageHandler<MessageManageGroups, IMessage>
{
	public static final int C_ADD_FRIEND = 1;
	public static final int C_REM_FRIEND = 2;
	public static final int C_ADD_GROUP = 3;
	public static final int C_REM_GROUP = 4;
	public static final int C_REN_GROUP = 5;
	public static final int C_ADD_TO_GROUP = 6;
	public static final int C_REM_FROM_GROUP = 7;
	
	public MessageManageGroups() { }
	
	public MessageManageGroups(LMPlayer p, int code, int user, String group)
	{
		data = new NBTTagCompound();
		data.setInteger("O", p.playerID);
		if(code > 0) data.setByte("C", (byte)code);
		if(user > 0) data.setInteger("U", user);
		if(group != null) data.setString("G", group);
	}
	
	public IMessage onMessage(MessageManageGroups m, MessageContext ctx)
	{
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		
		LMPlayer owner = LMPlayer.getPlayer(ep);
		
		if(owner != null && owner.playerID == m.data.getInteger("O"))
		{
			if(owner != null)
			{
				int code = m.data.getByte("C");
				int user = m.data.getInteger("U");
				String group = m.data.getString("G");
				
				boolean changed = false;
				
				if(code > 0)
				{
					if(code == C_ADD_FRIEND)
					{
						LMPlayer p = LMPlayer.getPlayer(user);
						
						if(p != null && !owner.friends.contains(p))
						{
							owner.friends.add(p);
							changed = true;
						}
					}
					else if(code == C_REM_FRIEND)
					{
						LMPlayer p = LMPlayer.getPlayer(user);
						
						if(p != null && owner.friends.contains(p))
						{
							owner.friends.remove(p);
							changed = true;
						}
					}
					else if(code == C_ADD_GROUP)
					{
						if(!group.isEmpty() && owner.groups.size() < 8 && !owner.groups.keys.contains(group))
						{
							LMPlayer.Group g = new LMPlayer.Group(owner, group);
							owner.groups.put(g.name, g);
							changed = true;
						}
					}
					else if(code == C_REM_GROUP)
					{
						if(!group.isEmpty() && owner.groups.size() > 0 && owner.groups.keys.contains(group))
						{
							owner.groups.remove(group);
							changed = true;
						}
					}
					else if(code == C_REN_GROUP)
					{
						if(!group.isEmpty() && owner.groups.size() > 0)
						{
							String[] s = group.split("=", 2);
							
							if(s != null && s.length == 2 && owner.groups.keys.contains(s[0]))
							{
								LMPlayer.Group g = owner.groups.get(s[0]).copy(s[1]);
								owner.groups.put(g.name, g);
								changed = true;
							}
						}
					}
					else if(code == C_ADD_TO_GROUP)
					{
						LMPlayer p = LMPlayer.getPlayer(user);
						
						if(p != null && owner.groups.size() > 0 && owner.groups.keys.contains(group))
						{
							LMPlayer.Group g = owner.groups.get(group);
							
							if(!g.members.contains(p))
								g.members.add(p);
							
							changed = true;
						}
					}
					else if(code == C_REM_FROM_GROUP)
					{
						LMPlayer p = LMPlayer.getPlayer(user);
						
						if(p != null && owner.groups.size() > 0 && owner.groups.keys.contains(group))
						{
							LMPlayer.Group g = owner.groups.get(group);
							
							if(g.members.contains(p))
								g.members.remove(p);
							
							changed = true;
						}
					}
				}
				else
				{
					LatCoreMC.notifyPlayer(ep, new Notification(AQUA + "Players saved", "", new ItemStack(Items.diamond), 2000L));
					changed = true;
				}
				
				if(changed)
				{
					owner.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED);
					
					if(user > 0)
					{
						LMPlayer p = LMPlayer.getPlayer(user);
						if(p != null) p.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED);
					}
				}
			}
		}
		return null;
	}
}