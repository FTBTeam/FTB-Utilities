package latmod.ftbu.core.inv;

import java.lang.reflect.Type;

import latmod.ftbu.core.util.LMStringUtils;
import net.minecraft.item.*;
import net.minecraft.nbt.*;

import com.google.gson.*;

public class ItemStackSerializer implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack>
{
	public JsonElement serialize(ItemStack is, Type typeOfSrc, JsonSerializationContext context)
	{ return new JsonPrimitive(toString(is)); }
	
	public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		if(json.isJsonNull()) return null;
		return parseItem(json.getAsString());
	}
	
	// Static //
	
	private static String getParseRegex(String s)
	{
		if(s.indexOf(';') != -1) return ";";
		if(s.indexOf('@') != -1) return "@";
		if(s.indexOf(" x ") != -1) return " x ";
		return " ";
	}
	
	public static ItemStack parseItem(String s)
	{
		if(s == null || s.isEmpty()) return null;
		
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
			else if(s1.length == 3)
			{
				size = Integer.parseInt(s1[1]);
				dmg = Integer.parseInt(s1[2]);
			}
			else if(s1.length >= 4)
			{
				String tagS = LMStringUtils.unsplitSpaceUntilEnd(3, s1);
				NBTTagCompound tag = (NBTTagCompound)JsonToNBT.func_150315_a(tagS);
				
				if(tag != null)
				{
					ItemStack is = new ItemStack(item, size, dmg);
					is.setTagCompound(tag);
					return is;
				}
			}
			
			return new ItemStack(item, size, dmg);
		}
		catch(Exception e) { }
		return null;
	}
	
	public static String toString(ItemStack is)
	{
		if(is == null) return null;
		Item i = is.getItem();
		
		StringBuilder sb = new StringBuilder();
		sb.append(LMInvUtils.getRegName(is));
		
		if(is.stackSize > 1)
		{
			sb.append(' ');
			sb.append(is.stackSize);
		}
		
		int dmg = is.getItemDamage();
		if(dmg != 0 || i.getHasSubtypes())
		{
			sb.append(' ');
			sb.append(dmg);
		}
		
		return sb.toString();
	}
}