package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.events.player.ForgePlayerConfigEvent;
import com.feed_the_beast.ftblib.lib.config.ConfigBoolean;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftbutilities.FTBUFinals;
import com.feed_the_beast.ftbutilities.integration.FTBLibIntegration;
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

	public final ForgePlayer player;
	public BlockDimPos lastDeath, lastSafePos;
	public ForgeTeam lastChunkTeam;
	public final BlockDimPosStorage homes;
	public boolean fly;

	public FTBUPlayerData(ForgePlayer p)
	{
		player = p;
		homes = new BlockDimPosStorage();
	}

	public static FTBUPlayerData get(ForgePlayer player)
	{
		return player.getData().get(FTBLibIntegration.FTBU_DATA);
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("RenderBadge", renderBadge.getBoolean());
		nbt.setBoolean("DisableGlobalBadges", disableGlobalBadge.getBoolean());
		nbt.setBoolean("EnablePVP", enablePVP.getBoolean());

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