package latmod.ftbu.net;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.ftbu.world.*;
import latmod.lib.ByteCount;

import java.util.UUID;

public class MessageLMPlayerLoggedIn extends MessageFTBU
{
	public MessageLMPlayerLoggedIn() { super(ByteCount.INT); }
	
	public MessageLMPlayerLoggedIn(LMPlayerServer p, boolean first, boolean self)
	{
		this();
		
		io.writeInt(p.playerID);
		io.writeUUID(p.getUUID());
		io.writeUTF(p.getName());
		io.writeBoolean(first);
		p.writeToNet(io, self);
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		if(LMWorldClient.inst == null) return null;
		
		int playerID = io.readInt();
		UUID uuid = io.readUUID();
		String username = io.readUTF();
		boolean firstTime = io.readBoolean();
		
		LMPlayerClient p = LMWorldClient.inst.getPlayer(playerID);
		boolean add = p == null;
		if(add) p = new LMPlayerClient(LMWorldClient.inst, playerID, new GameProfile(uuid, username));
		p.readFromNet(io, p.playerID == LMWorldClient.inst.clientPlayerID);
		LMWorldClient.inst.playerMap.put(p.playerID, p);
		new EventLMPlayerClient.LoggedIn(p, firstTime).post();
		new EventLMPlayerClient.DataLoaded(p).post();
		return null;
	}
}