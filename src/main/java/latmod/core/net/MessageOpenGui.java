package latmod.core.net;
import latmod.core.LatCoreMC;
import latmod.core.mod.LC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageOpenGui extends MessageLM<MessageOpenGui>
{
	public MessageOpenGui() { }
	
	public MessageOpenGui(String id, NBTTagCompound tag, int windowID)
	{
		data = new NBTTagCompound();
		data.setString("ID", id);
		data.setInteger("W", windowID);
		if(tag != null) data.setTag("D", tag);
	}
	
	public void onMessage(MessageContext ctx)
	{
		EntityPlayer player = LC.proxy.getClientPlayer();
		LatCoreMC.openGui(player, data.getString("ID"), (NBTTagCompound)data.getTag("D"));
        player.openContainer.windowId = data.getInteger("W");
	}
}