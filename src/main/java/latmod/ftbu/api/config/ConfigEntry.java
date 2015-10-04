package latmod.ftbu.api.config;

import latmod.core.util.PrimitiveType;
import net.minecraft.nbt.NBTTagCompound;

public abstract class ConfigEntry
{
	public final String ID;
	public final PrimitiveType type;
	
	public ConfigGroup group = null;
	private boolean syncWithClient = false;
	
	public ConfigEntry(String id, PrimitiveType t)
	{ ID = id; type = t; }
	
	public String toString()
	{ return ID; }
	
	@SuppressWarnings("all")
	public <E extends ConfigEntry> E setSyncWithClient()
	{ syncWithClient = true; return (E)this; }
	
	public boolean syncWithClient()
	{ return syncWithClient; }
	
	public int hashCode()
	{ return toString().hashCode(); }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || o.toString().equals(toString())); }
	
	abstract void setJson(Object o);
	abstract Object getJson();
	final void writeToNBT(NBTTagCompound tag) {}
	final void readFromNBT(NBTTagCompound tag) {}
}