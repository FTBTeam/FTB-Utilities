package latmod.core.net;
import java.util.UUID;

import latmod.core.LMPlayer;
import latmod.core.mod.LC;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageLMPlayerLoggedIn extends MessageLM<MessageLMPlayerLoggedIn>
{
	public MessageLMPlayerLoggedIn() { }
	
	public MessageLMPlayerLoggedIn(LMPlayer p)
	{
		data = new NBTTagCompound();
		data.setInteger("P", p.playerID);
		data.setLong("M", p.uuid.getMostSignificantBits());
		data.setLong("L", p.uuid.getLeastSignificantBits());
		data.setString("U", p.username);
		
		NBTTagCompound data1 = new NBTTagCompound();
		p.writeToNBT(data1, false);
		data.setTag("D", data1);
	}
	
	public void onMessage(MessageContext ctx)
	{
		if(LC.proxy.getClientWorld() == null) return;
		
		int playerID = data.getInteger("P");
		
		LMPlayer p = new LMPlayer(playerID, new UUID(data.getLong("M"), data.getLong("L")), data.getString("U"));
		LMPlayer.map.put(p.playerID, p);
		
		p.readFromNBT(data.getCompoundTag("D"), false);
		LC.proxy.playerLMLoggedIn(p);
	}
}