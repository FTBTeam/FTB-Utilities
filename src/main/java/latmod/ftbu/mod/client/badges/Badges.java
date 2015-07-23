package latmod.ftbu.mod.client.badges;

import java.util.Map;

import com.google.gson.annotations.Expose;

import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class Badges
{
	@Expose public Map<String, String> players;
	@Expose public Map<String, String> badges;
}