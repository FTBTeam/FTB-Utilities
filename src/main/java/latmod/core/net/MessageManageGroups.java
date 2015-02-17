package latmod.core.net;
import static net.minecraft.util.EnumChatFormatting.AQUA;
import latmod.core.*;
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
	
	public MessageManageGroups(LMPlayer p, int code, int user, int group, String groupName)
	{
		data = new NBTTagCompound();
		data.setInteger("O", p.playerID);
		if(code > 0) data.setByte("C", (byte)code);
		if(user > 0) data.setInteger("U", user);
		if(group > 0) data.setInteger("G", group);
		if(groupName != null && !groupName.isEmpty()) data.setString("GN", groupName);
	}
	
	public IMessage onMessage(MessageManageGroups m, MessageContext ctx)
	{
		LMPlayer owner = LMPlayer.getPlayer(m.data.getInteger("O"));
		
		if(owner != null)
		{
			int code = m.data.getByte("C");
			
			int user0 = m.data.getInteger("U");
			LMPlayer p = (user0 > 0) ? LMPlayer.getPlayer(user0) : null;
			
			boolean changed = false;
			
			if(code > 0)
			{
				if(code == C_ADD_FRIEND)
				{
					if(p != null && !owner.friends.contains(p))
					{
						owner.friends.add(p);
						changed = true;
					}
				}
				else if(code == C_REM_FRIEND)
				{
					if(p != null && owner.friends.contains(p))
					{
						owner.friends.remove(p);
						changed = true;
					}
				}
				else
				{
					int groupID = m.data.getInteger("G");
					
					if(code == C_ADD_GROUP)
					{
						if(owner.groups.size() < 8)
						{
							LMPlayer.Group g = new LMPlayer.Group(owner, ++owner.lastGroupID, "Unnamed");
							owner.groups.put(g.groupID, g);
							changed = true;
						}
					}
					else if(code == C_REM_GROUP)
					{
						if(groupID > 0 && owner.groups.hasKeys())
						{
							if(owner.groups.remove(groupID))
								changed = true;
						}
					}
					else if(code == C_REN_GROUP)
					{
						String groupName = m.data.getString("GN");
						
						if(groupID > 0 && owner.groups.hasKeys() && !groupName.isEmpty())
						{
							LMPlayer.Group g = owner.groups.get(groupID);
							if(g != null && !g.name.equals(groupName))
							{
								g.name = groupName;
								changed = true;
							}
						}
					}
					else if(code == C_ADD_TO_GROUP)
					{
						if(p != null && owner.groups.hasKeys() && owner.groups.keys.contains(groupID))
						{
							LMPlayer.Group g = owner.groups.get(groupID);
							
							if(!g.members.contains(p))
								g.members.add(p);
							
							changed = true;
						}
					}
					else if(code == C_REM_FROM_GROUP)
					{
						if(p != null && owner.groups.hasKeys() && owner.groups.keys.contains(groupID))
						{
							LMPlayer.Group g = owner.groups.get(groupID);
							
							if(g.members.contains(p))
								g.members.remove(p);
							
							changed = true;
						}
					}
				}
			}
			else
			{
				LatCoreMC.notifyPlayer(ctx.getServerHandler().playerEntity, new Notification(AQUA + "Players saved", "", new ItemStack(Items.diamond), 2000L));
				changed = true;
			}
			
			if(changed)
			{
				owner.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED);
				
				if(p != null)
					p.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED);
			}
		}
		
		return null;
	}
}