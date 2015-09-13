package latmod.ftbu.mod.client.minimap;

import com.google.gson.*;

import cpw.mods.fml.relauncher.*;
import latmod.ftbu.core.gui.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.FTBU;

@SideOnly(Side.CLIENT)
public class Waypoint
{
	public static enum Type
	{
		BEACON("beacon", GuiIcons.beacon),
		MARKER("marker", GuiIcons.marker);
		
		public final String ID;
		public final TextureCoords icon;
		
		Type(String s, TextureCoords t)
		{
			ID = s;
			icon = t;
		}

		public boolean isMarker()
		{ return this == MARKER; }
		
		public boolean isBeacon()
		{ return this == BEACON; }
		
		public Type next()
		{ return values()[(ordinal() + 1) % values().length]; }

		public String getIDS()
		{ return FTBU.mod.translateClient("waypoint.type." + ID); }
	};
	
	public String name;
	//public String customIcon;
	public boolean enabled = true;
	public Type type = Type.BEACON;
	public int posX, posY, posZ, dim, colR, colG, colB;
	public int listID = -1;
	
	public void setPos(double x, double y, double z)
	{
		posX = MathHelperLM.floor(x);
		posY = MathHelperLM.floor(y);
		posZ = MathHelperLM.floor(z);
	}
	
	public void setColor(int r, int g, int b)
	{ colR = r; colG = g; colB = b; }
	
	public int getColorRGB()
	{ return LMColorUtils.getRGBA(colR, colG, colB, 255); }
	
	public void setColor(int col)
	{ setColor(LMColorUtils.getRed(col), LMColorUtils.getGreen(col), LMColorUtils.getBlue(col)); }
	
	public String toString()
	{ return LMJsonUtils.toJson(this); }
	
	public int hashCode()
	{ return listID; }
	
	public static class Serializer implements JsonSerializer<Waypoint>, JsonDeserializer<Waypoint>
	{
		public JsonElement serialize(Waypoint src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context)
		{
			if(src == null) return null;
			JsonObject o = new JsonObject();
			o.add("Name", new JsonPrimitive(src.name));
			o.add("On", new JsonPrimitive(src.enabled ? 1 : 0));
			o.add("Type", new JsonPrimitive(src.type.ordinal()));
			o.add("X", new JsonPrimitive(src.posX));
			o.add("Y", new JsonPrimitive(src.posY));
			o.add("Z", new JsonPrimitive(src.posZ));
			o.add("Dim", new JsonPrimitive(src.dim));
			o.add("Col", new JsonPrimitive(LMColorUtils.getHex(src.getColorRGB())));
			return o;
		}
		
		public Waypoint deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			if(json.isJsonNull()) return null;
			JsonObject o = json.getAsJsonObject();
			Waypoint w = new Waypoint();
			w.name = o.get("Name").getAsString();
			w.enabled = o.get("On").getAsInt() == 1;
			w.type = Type.values()[o.get("Type").getAsInt()];
			w.posX = o.get("X").getAsInt();
			w.posY = o.get("Y").getAsInt();
			w.posZ = o.get("Z").getAsInt();
			w.dim = o.get("Dim").getAsInt();
			w.setColor(Integer.decode(o.get("Col").getAsString()));
			return w;
		}
	}
}