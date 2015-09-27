package latmod.ftbu.inv;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.*;

import net.minecraft.item.*;

public class ItemStackTypeAdapter extends TypeAdapter<ItemStack>
{
	public void write(JsonWriter out, ItemStack value) throws IOException
	{
		if(value == null) out.nullValue();
		else out.value(toString(value));
	}
	
	public ItemStack read(JsonReader in) throws IOException
	{
		if(in.peek() == JsonToken.NULL) { in.nextNull(); return null; }
		return parseItem(in.nextString());
	}
	
	// Static //
	
	private static String getParseRegex(String s)
	{
		if(s.indexOf(' ') != -1) return " ";
		else if(s.indexOf(';') != -1) return ";";
		else if(s.indexOf('@') != -1) return "@";
		else return " x ";
	}
	
	public static ItemStack parseItem(String s)
	{
		if(s == null) return null;
		s = s.trim();
		if(s.isEmpty()) return null;
		
		try
		{
			String[] s1 = s.split(getParseRegex(s));
			if(s1.length <= 0) return null;
			
			Item item = LMInvUtils.getItemFromRegName(s1[0]);
			if(item == null) return null;
			int dmg = 0;
			int size = 1;
			
			if(s1.length == 2)
				dmg = Integer.parseInt(s1[1]);
			else if(s1.length >= 3)
			{
				size = Integer.parseInt(s1[1]);
				dmg = Integer.parseInt(s1[2]);
			}
			/*else if(s1.length >= 4)
			{
				String tagS = LMStringUtils.unsplitSpaceUntilEnd(3, s1);
				NBTTagCompound tag = (NBTTagCompound)JsonToNBT.func_150315_a(tagS);
				
				if(tag != null)
				{
					ItemStack is = new ItemStack(item, size, dmg);
					is.setTagCompound(tag);
					return is;
				}
			}*/
			
			return new ItemStack(item, size, dmg);
		}
		catch(Exception e) { }
		return null;
	}
	
	public static String toString(ItemStack is)
	{
		if(is == null) return null;
		StringBuilder sb = new StringBuilder();
		sb.append(LMInvUtils.getRegName(is));
		sb.append(' ');
		sb.append(is.stackSize);
		sb.append(' ');
		sb.append(is.getItemDamage());
		return sb.toString();
	}
}