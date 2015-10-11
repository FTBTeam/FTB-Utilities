package latmod.ftbu.api.client;

import cpw.mods.fml.relauncher.*;
import latmod.lib.FastMap;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public final class ClientConfig
{
	public final String id;
	public final FastMap<String, ClientConfigProperty> map;
	public boolean isHidden = false;
	
	public ClientConfig(String s)
	{
		id = s;
		map = new FastMap<String, ClientConfigProperty>();
	}
	
	public void add(ClientConfigProperty p)
	{ map.put(p.id, p); }
	
	public String getIDS()
	{ return I18n.format("config.group." + id); }
	
	public String toString()
	{ return getIDS() + ": " + map; }
	
	public ClientConfig setHidden()
	{ isHidden = true; return this; }
}