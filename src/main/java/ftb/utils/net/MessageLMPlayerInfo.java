package ftb.utils.net;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.item.LMInvUtils;
import ftb.lib.api.net.LMNetworkWrapper;
import ftb.utils.world.*;
import latmod.lib.*;
import net.minecraft.nbt.NBTTagCompound;
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
		
		ArrayList<IChatComponent> info = new ArrayList<>();
		p.getInfo(owner, info);
		
		int s = Math.min(255, info.size());
		io.writeByte(s);
		
		for(int i = 0; i < s; i++)
			io.writeUTF(IChatComponent.Serializer.func_150696_a(info.get(i)));
		
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
			info.add(IChatComponent.Serializer.func_150699_a(io.readUTF()));
		p.receiveInfo(info);
		
		LMInvUtils.readItemsFromNBT(p.lastArmor, readTag(), "A");
		
		p.friends.clear();
		p.friends.addAll(io.readIntArray(ByteCount.SHORT));
		
		FTBLibClient.onGuiClientAction();
		return null;
	}
}