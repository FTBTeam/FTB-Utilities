package latmod.ftbu.net;
import cpw.mods.fml.common.network.simpleimpl.*;
import latmod.ftbu.api.tile.IClientActionTile;
import latmod.ftbu.tile.TileLM;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class MessageClientTileAction extends MessageFTBU
{
	public MessageClientTileAction() { super(DATA_LONG); }
	
	public MessageClientTileAction(TileLM t, String s, NBTTagCompound tag)
	{
		this();
		io.writeInt(t.xCoord);
		io.writeInt(t.yCoord);
		io.writeInt(t.zCoord);
		io.writeString(s);
		writeTag(tag);
	}
	
	public IMessage onMessage(MessageContext ctx)
	{
		int x = io.readInt();
		int y = io.readInt();
		int z = io.readInt();
		String action = io.readString();
		NBTTagCompound data = readTag();
		
		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		TileEntity te = ep.worldObj.getTileEntity(x, y, z);
		
		if(te instanceof IClientActionTile)
			((IClientActionTile)te).onClientAction(ep, action, data);
		
		return null;
	}
}