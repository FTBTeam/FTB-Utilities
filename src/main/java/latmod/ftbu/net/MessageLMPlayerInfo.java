package latmod.ftbu.net;

import ftb.lib.api.LMNetworkWrapper;
import ftb.lib.client.FTBLibClient;
import ftb.lib.item.LMInvUtils;
import latmod.ftbu.world.*;
import latmod.lib.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

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
		
		ArrayList<IChatComponent> info = new ArrayList<>();
		p.getInfo(owner, info);
		
		int s = Math.min(255, info.size());
		io.writeByte(s);
		
		for(int i = 0; i < s; i++)
			io.writeUTF(IChatComponent.Serializer.componentToJson(info.get(i)));
		
		NBTTagCompound tag = new NBTTagCompound();
		LMInvUtils.writeItemsToNBT(p.lastArmor, tag, "A");
		writeTag(tag);
		
		io.writeIntArray(LMListUtils.toHashCodeArray(p.getFriends()), ByteCount.SHORT);
	}
	
	public LMNetworkWrapper getWrapper()
	{ return FTBUNetHandler.NET_INFO; }
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		if(LMWorldClient.inst == null) return null;
		LMPlayerClient p = LMWorldClient.inst.getPlayer(io.readInt());
		if(p == null) return null;
		
		int s = io.readUnsignedByte();
		ArrayList<IChatComponent> info = new ArrayList<>();
		for(int i = 0; i < s; i++)
			info.add(IChatComponent.Serializer.jsonToComponent(io.readUTF()));
		p.receiveInfo(info);
		
		LMInvUtils.readItemsFromNBT(p.lastArmor, readTag(), "A");
		
		p.friends.clear();
		p.friends.addAll(io.readIntArray(ByteCount.SHORT));
		
		FTBLibClient.onGuiClientAction();
		return null;
	}
}