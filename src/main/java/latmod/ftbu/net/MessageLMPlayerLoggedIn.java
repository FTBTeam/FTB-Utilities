package latmod.ftbu.net;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.*;
import latmod.ftbu.api.EventLMPlayerClient;
import latmod.ftbu.world.*;
import net.minecraft.nbt.NBTTagCompound;

public class MessageLMPlayerLoggedIn extends MessageFTBU
{
	public MessageLMPlayerLoggedIn() { super(DATA_LONG); }
	
	public MessageLMPlayerLoggedIn(LMPlayerServer p, boolean first, boolean self)
	{
		this();
		
		io.writeInt(p.playerID);
		io.writeUUID(p.getUUID());
		io.writeString(p.getName());
		NBTTagCompound data = new NBTTagCompound();
		p.writeToNet(data, self);
		writeTag(data);
		io.writeBoolean(first);
	}
	
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(MessageContext ctx)
	{
		if(LMWorldClient.inst == null) return null;
		
		int playerID = io.readInt();
		UUID uuid = io.readUUID();
		String username = io.readString();
		NBTTagCompound data = readTag();
		boolean firstTime = io.readBoolean();
		
		LMPlayerClient p = LMWorldClient.inst.getPlayer(playerID);
		boolean add = p == null;
		if(add) p = new LMPlayerClient(LMWorldClient.inst, playerID, new GameProfile(uuid, username));
		p.readFromNet(data, p.playerID == LMWorldClient.inst.clientPlayerID);
		if(add) LMWorldClient.inst.players.add(p);
		new EventLMPlayerClient.DataLoaded(p).post();
		new EventLMPlayerClient.LoggedIn(p, firstTime).post();
		return null;
	}
}