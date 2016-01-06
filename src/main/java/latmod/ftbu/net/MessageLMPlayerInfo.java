package latmod.ftbu.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.LMNBTUtils;
import ftb.lib.api.LMNetworkWrapper;
import ftb.lib.item.LMInvUtils;
import latmod.ftbu.util.client.LatCoreMCClient;
import latmod.ftbu.world.*;
import latmod.lib.ByteCount;
import net.minecraft.nbt.*;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;

public class MessageLMPlayerInfo extends MessageFTBU
{
	public MessageLMPlayerInfo() { super(ByteCount.INT); }
	
	public MessageLMPlayerInfo(LMPlayerServer owner, int playerID)
	{
		this();
		LMPlayerServer p = LMWorldServer.inst.getPlayer(playerID);
		io.writeInt(p == null ? 0 : p.playerID);
		if(p == null) return;
		
		NBTTagCompound tag = new NBTTagCompound();

		ArrayList<IChatComponent> info = new ArrayList<>();
		p.getInfo(owner, info);
		
		NBTTagList listInfo = new NBTTagList();
		
		for(int i = 0; i < info.size(); i++)
			listInfo.appendTag(new NBTTagString(IChatComponent.Serializer.func_150696_a(info.get(i))));
		
		tag.setTag("I", listInfo);
		
		LMInvUtils.writeItemsToNBT(p.lastArmor, tag, "LI");
		
		writeTag(tag);
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		if(LMWorldClient.inst == null) return null;
		LMPlayerClient p = LMWorldClient.inst.getPlayer(io.readInt());
		if(p == null) return null;
		
		NBTTagCompound tag = readTag();
		NBTTagList listInfo = tag.getTagList("I", LMNBTUtils.STRING);

		ArrayList<IChatComponent> info = new ArrayList<>();
		for(int i = 0; i < listInfo.tagCount(); i++)
			info.add(IChatComponent.Serializer.func_150699_a(listInfo.getStringTagAt(i)));
		p.receiveInfo(info);
		
		LMInvUtils.readItemsFromNBT(p.lastArmor, tag, "LI");
		
		LatCoreMCClient.onGuiClientAction();
		return null;
	}
}