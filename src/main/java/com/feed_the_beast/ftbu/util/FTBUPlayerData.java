package com.feed_the_beast.ftbu.util;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.player.ForgePlayerConfigEvent;
import com.feed_the_beast.ftbl.lib.config.ConfigBoolean;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.handlers.FTBLibIntegration;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author LatvianModder
 */
public class FTBUPlayerData implements INBTSerializable<NBTTagCompound>
{
	public final ConfigBoolean renderBadge = new ConfigBoolean(true);
	public final ConfigBoolean disableGlobalBadge = new ConfigBoolean(false);
	public final ConfigBoolean enablePVP = new ConfigBoolean(true);

	public final IForgePlayer player;
	public BlockDimPos lastDeath, lastSafePos;
	public IForgeTeam lastChunkTeam;
	public final BlockDimPosStorage homes;
	public boolean fly;

	public FTBUPlayerData(IForgePlayer p)
	{
		player = p;
		homes = new BlockDimPosStorage();
	}

	public static FTBUPlayerData get(IForgePlayer player)
	{
		return player.getData().get(FTBLibIntegration.FTBU_DATA);
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();

		if (!renderBadge.getBoolean())
		{
			nbt.setBoolean("RenderBadge", false);
		}

		if (disableGlobalBadge.getBoolean())
		{
			nbt.setBoolean("DisableGlobalBadges", true);
		}

		if (!enablePVP.getBoolean())
		{
			nbt.setBoolean("EnablePVP", false);
		}

		NBTTagCompound homesTag = homes.serializeNBT();

		if (!homesTag.hasNoTags())
		{
			nbt.setTag("Homes", homesTag);
		}

		if (fly)
		{
			nbt.setBoolean("AllowFlying", true);
		}

		if (lastDeath != null)
		{
			nbt.setIntArray("LastDeath", lastDeath.toIntArray());
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (nbt == null)
		{
			return;
		}

		renderBadge.setBoolean(!nbt.hasKey("RenderBadge") || nbt.getBoolean("RenderBadge"));
		disableGlobalBadge.setBoolean(nbt.getBoolean("DisableGlobalBadges"));
		enablePVP.setBoolean(!nbt.hasKey("EnablePVP") || nbt.getBoolean("EnablePVP"));
		homes.deserializeNBT(nbt.getCompoundTag("Homes"));
		fly = nbt.getBoolean("AllowFlying");

		lastDeath = null;
		if (nbt.hasKey("LastDeath"))
		{
			int[] ai = nbt.getIntArray("LastDeath");
			lastDeath = (ai.length == 4) ? new BlockDimPos(ai) : null;
		}
	}

	public void addConfig(ForgePlayerConfigEvent event)
	{
		String group = FTBUFinals.MOD_ID;
		event.getConfig().setGroupName(group, new TextComponentString(FTBUFinals.MOD_NAME));
		event.getConfig().add(group, "render_badge", renderBadge);
		event.getConfig().add(group, "disable_global_badge", disableGlobalBadge);
		event.getConfig().add(group, "enable_pvp", enablePVP);
	}
}