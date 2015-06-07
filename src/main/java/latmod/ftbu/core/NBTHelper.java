package latmod.ftbu.core;

import java.io.*;

import latmod.ftbu.core.util.*;
import net.minecraft.nbt.*;

@SuppressWarnings("all")
public class NBTHelper // NBTBase
{
	public static final int END = 0;
	public static final int BYTE = 1;
	public static final int SHORT = 2;
	public static final int INT = 3;
	public static final int LONG = 4;
	public static final int FLOAT = 5;
	public static final int DOUBLE = 6;
	public static final int BYTE_ARRAY = 7;
	public static final int STRING = 8;
	public static final int LIST = 9;
	public static final int MAP = 10;
	public static final int INT_ARRAY = 11;
	
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
		try { writeMap(new FileOutputStream(LatCore.newFile(f)), tag); }
		catch(Exception e) { e.printStackTrace(); }
	}
	
	public static NBTTagCompound readMap(InputStream is) throws Exception
	{
		byte[] b = new byte[is.available()]; is.read(b); is.close();
		return CompressedStreamTools.func_152457_a(b, new NBTSizeTracker(Long.MAX_VALUE));
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
}