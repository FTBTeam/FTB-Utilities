package latmod.ftbu.core;

import java.io.*;

import latmod.ftbu.core.util.*;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.Constants;

@SuppressWarnings("all")
public class NBTHelper // NBTBase
{
	public static final int END = Constants.NBT.TAG_END;
	public static final int BYTE = Constants.NBT.TAG_BYTE;
	public static final int SHORT = Constants.NBT.TAG_SHORT;
	public static final int INT = Constants.NBT.TAG_INT;
	public static final int LONG = Constants.NBT.TAG_LONG;
	public static final int FLOAT = Constants.NBT.TAG_FLOAT;
	public static final int DOUBLE = Constants.NBT.TAG_DOUBLE;
	public static final int BYTE_ARRAY = Constants.NBT.TAG_BYTE_ARRAY;
	public static final int STRING = Constants.NBT.TAG_STRING;
	public static final int LIST = Constants.NBT.TAG_LIST;
	public static final int MAP = Constants.NBT.TAG_COMPOUND;
	public static final int INT_ARRAY = Constants.NBT.TAG_INT_ARRAY;
	
	public static FastList<String> getMapKeys(NBTTagCompound tag)
	{
		FastList<String> list = new FastList<String>();
		list.addAll(tag.func_150296_c()); return list;
	}
	
	public static FastMap<String, NBTBase> toFastMap(NBTTagCompound tag)
	{
		FastMap<String, NBTBase> map = new FastMap<String, NBTBase>();
		FastList<String> keys = getMapKeys(tag);
		for(int i = 0; i < keys.size(); i++)
		{ String s = keys.get(i); map.put(s, tag.getTag(s)); }
		return map;
	}
	
	public static <E extends NBTBase> FastMap<String, E> toFastMapWithType(NBTTagCompound tag)
	{
		FastMap<String, E> map = new FastMap<String, E>();
		FastList<String> keys = getMapKeys(tag);
		
		for(int i = 0; i < keys.size(); i++)
		{
			String s = keys.get(i);
			map.put(s, (E)tag.getTag(s));
		}
		
		return map;
	}
	
	public static void writeMap(OutputStream os, NBTTagCompound tag) throws Exception
	{
		byte[] b = CompressedStreamTools.compress(tag);
		os.write(b); os.flush(); os.close();
	}
	
	public static void writeMap(File f, NBTTagCompound tag)
	{
		try { writeMap(new FileOutputStream(LMFileUtils.newFile(f)), tag); }
		catch(Exception e) { e.printStackTrace(); }
	}
	
	public static NBTTagCompound readMap(InputStream is) throws Exception
	{
		byte[] b = new byte[is.available()]; is.read(b); is.close();
		return CompressedStreamTools.func_152457_a(b, NBTSizeTracker.field_152451_a);
	}
	
	public static NBTTagCompound readMap(File f)
	{
		if(f == null || !f.exists()) return null;
		try { return readMap(new FileInputStream(f)); }
		catch(Exception e) { e.printStackTrace(); }
		return null;
	}
	
	public static boolean areTagsEqual(NBTTagCompound tag1, NBTTagCompound tag2)
	{
		if(tag1 == null && tag2 == null) return true;
		if(tag1 != null && tag2 == null) return false;
		if(tag1 == null && tag2 != null) return false;
		return tag1.equals(tag2);
	}
	
	public static void toStringList(FastList<String> l, NBTTagList tag)
	{
		l.clear();
		for(int i = 0; i < tag.tagCount(); i++)
			l.add(tag.getStringTagAt(i));
	}
	
	public static FastList<String> toStringList(NBTTagList tag)
	{ FastList<String> l = new FastList<String>(); toStringList(l, tag); return l; }
	
	public static NBTTagList fromStringList(FastList<String> l)
	{
		NBTTagList tag = new NBTTagList();
		for(int i = 0; i < l.size(); i++)
			tag.appendTag(new NBTTagString(l.get(i)));
		return tag;
	}
	
	public static Vertex getVertex(NBTTagCompound tag, String s)
	{
		NBTTagList list = tag.getTagList(s, DOUBLE);
		if(list.tagCount() != 3) return null;
		return new Vertex(list.func_150309_d(0), list.func_150309_d(1), list.func_150309_d(2));
	}
	
	public static void setVertex(NBTTagCompound tag, String s, Vertex v)
	{
		if(v == null) return;
		NBTTagList list = new NBTTagList();
		list.appendTag(new NBTTagDouble(v.x));
		list.appendTag(new NBTTagDouble(v.y));
		list.appendTag(new NBTTagDouble(v.z));
		tag.setTag(s, list);
	}
}