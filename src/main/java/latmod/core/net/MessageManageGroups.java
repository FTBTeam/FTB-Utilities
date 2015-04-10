package latmod.core.net;
import static net.minecraft.util.EnumChatFormatting.AQUA;
import latmod.core.*;
import latmod.core.mod.cmd.CmdLMFriends;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageManageGroups extends MessageLM<MessageManageGroups>
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
	
	public void onMessage(MessageContext ctx)
	{
		LMPlayer owner = LMPlayer.getPlayer(data.getInteger("O"));
		EntityPlayerMP ep = owner.getPlayerMP();
		
		if(owner != null)
		{
			int code = data.getByte("C");
			
			int user0 = data.getInteger("U");
			LMPlayer p = (user0 > 0) ? LMPlayer.getPlayer(user0) : null;
			
			if(code > 0)
			{
				String args[] = null;
				
				if(code == C_ADD_FRIEND)
					args = new String[] { "add", p.username };
				else if(code == C_REM_FRIEND)
					args = new String[] { "rem", p.username };
				else
				{
					LMPlayer.Group g = owner.getGroup(data.getInteger("G"));
					
					if(code == C_ADD_GROUP)
						args = new String[] { "addgroup", "Unnamed" };
					else if(code == C_REM_GROUP)
						args = new String[] { "remgroup", g.name };
					else if(code == C_REN_GROUP)
						args = new String[] { "rengroup", g.name, data.getString("GN") };
					else if(code == C_ADD_TO_GROUP)
						args = new String[] { "addto", g.name, p.username };
					else if(code == C_REM_FROM_GROUP)
						args = new String[] { "remfrom", g.name, p.username };
				}
				
				if(args != null)
				{
					String s = CmdLMFriends.onStaticCommand(ep, owner, args);
					LatCoreMC.notifyPlayer(ep, new Notification(s, null, new ItemStack(Items.skull, 1, 3), 800L));
				}
			}
			else
			{
				LatCoreMC.notifyPlayer(ctx.getServerHandler().playerEntity, new Notification(AQUA + "Players saved", "", new ItemStack(Items.diamond), 2000L));
				owner.sendUpdate(LMPlayer.ACTION_GROUPS_CHANGED, true);
			}
		}
	}
}