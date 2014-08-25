package latmod.core.mod.net;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

import latmod.core.mod.LC;
import latmod.core.util.FastMap;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class LMNetHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(LC.mod.modID);
	
	public static FastMap<String, ICustomActionHandler> customHandlers = new FastMap<String, ICustomActionHandler>();
	
	public static void init()
	{
		INSTANCE.registerMessage(MessageClientTileAction.class, MessageClientTileAction.class, 1, Side.SERVER);
		INSTANCE.registerMessage(MessageClientItemAction.class, MessageClientItemAction.class, 2, Side.SERVER);
		INSTANCE.registerMessage(MessageCustomClientAction.class, MessageCustomClientAction.class, 3, Side.SERVER);
		INSTANCE.registerMessage(MessageCustomServerAction.class, MessageCustomServerAction.class, 4, Side.CLIENT);
	}
	
	// Helper methods //
	
	public static String readString(ByteBuf data)
	{
		int s = data.readShort();
		if(s == -1) return null;
		String str = "";
		for(int i = 0; i < s; i++)
			str += data.readChar();
		return str;
	}
	
	public static void writeString(ByteBuf data, String s)
	{
		data.writeShort(s == null ? -1 : s.length());
		if(s != null && s.length() > 0)
		for(int i = 0; i < s.length(); i++)
			data.writeChar(s.charAt(i));
	}
	
	public static void writeNBTTagCompound(ByteBuf data, NBTTagCompound tag)
	{
		if (tag == null) data.writeShort(-1);
		else
		{
			try
			{
				byte[] abyte = CompressedStreamTools.compress(tag);
				data.writeShort((short)abyte.length);
				data.writeBytes(abyte);
			}
			catch(Exception e) { data.writeByte(-1); }
		}
	}
	
	public static NBTTagCompound readNBTTagCompound(ByteBuf data)
	{
		short s = data.readShort();
		if (s < 0) return null;
		else
		{
			byte[] abyte = new byte[s];
			data.readBytes(abyte);
			try
			{
				NBTTagCompound tag = CompressedStreamTools.func_152457_a(abyte, new NBTSizeTracker(2097152L));
				return tag;
			}
			catch(Exception e) {}
		}
		
		return null;
	}
	
	public static void writeItemStack(ByteBuf data, ItemStack is)
	{
		if (is == null) data.writeShort(-1);
		else
		{
			data.writeShort(Item.getIdFromItem(is.getItem()));
			data.writeByte(is.stackSize);
			data.writeShort(is.getItemDamage());
			NBTTagCompound tag = null;
			
			if (is.getItem().isDamageable() || is.getItem().getShareTag())
				tag = is.stackTagCompound;
			
			writeNBTTagCompound(data, tag);
		}
	}
	
	public static ItemStack readItemStack(ByteBuf data)
	{
		ItemStack is = null;
		short s = data.readShort();
		
		if (s >= 0)
		{
			byte b = data.readByte();
			short s1 = data.readShort();
			is = new ItemStack(Item.getItemById(s), b, s1);
			is.stackTagCompound = readNBTTagCompound(data);
		}
		
		return is;
	}
	
	public static void writeUUID(ByteBuf data, UUID id)
	{
		data.writeLong(id.getLeastSignificantBits());
		data.writeLong(id.getMostSignificantBits());
	}
	
	public static UUID readUUID(ByteBuf data)
	{
		long least = data.readLong();
		long most = data.readLong();
		return new UUID(most, least);
	}
}