package latmod.core.net;
import latmod.core.LMPlayer;
import latmod.core.mod.LC;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageLMPlayerUpdate extends MessageLM<MessageLMPlayerUpdate>
{
	public MessageLMPlayerUpdate() { }
	
	public MessageLMPlayerUpdate(LMPlayer p, String action)
	{
		data = new NBTTagCompound();
		data.setString("A", action);
		data.setInteger("P", p.playerID);
		NBTTagCompound data1 = new NBTTagCompound();
		p.writeToNBT(data1, false);
		data.setTag("D", data1);
	}
	
	public void onMessage(MessageContext ctx)
	{
		int playerID = data.getInteger("P");
		LMPlayer p = LMPlayer.getPlayer(playerID);
		p.readFromNBT(data.getCompoundTag("D"), false);
		LC.proxy.playerLMDataChanged(p, data.getString("A"));
	}
}