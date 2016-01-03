package latmod.ftbu.mod.client.gui.claims;

import cpw.mods.fml.relauncher.*;
import ftb.lib.api.config.IConfigProvider;
import latmod.ftbu.util.LMSecurityLevel;
import latmod.ftbu.world.*;
import latmod.lib.config.*;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public class ClaimsConfig implements IConfigProvider
{
	public final PersonalSettings settings;
	public final ConfigGroup group;
	
	public ClaimsConfig()
	{
		settings = LMWorldClient.inst.clientPlayer.getSettings();
		group = new ConfigGroup("claims_config");
		
		group.add(new ConfigEntryBool("explosions", settings.explosions)
		{
			public boolean get()
			{ return settings.explosions; }
			
			public void set(boolean v)
			{ settings.explosions = v; }
		}, false);
		
		group.add(new ConfigEntryEnum<LMSecurityLevel>("security_level", LMSecurityLevel.class, LMSecurityLevel.VALUES_3, settings.blocks, false)
		{
			public LMSecurityLevel get()
			{ return settings.blocks; }
			
			public void set(Object v)
			{ settings.blocks = (LMSecurityLevel)v; save(); }
		}, false);
		
		group.add(new ConfigEntryBool("fake_players", settings.fakePlayers)
		{
			public boolean get()
			{ return settings.fakePlayers; }
			
			public void set(boolean v)
			{ settings.fakePlayers = v; save(); }
		}, false);
	}
	
	public String getGroupTitle(ConfigGroup g)
	{ return I18n.format(g.getFullID()); }
	
	public String getEntryTitle(ConfigEntry e)
	{ return I18n.format(e.getFullID()); }
	
	public ConfigGroup getGroup()
	{ return group; }
	
	public void save()
	{ settings.update(); }
}