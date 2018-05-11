package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.events.player.ForgePlayerConfigEvent;
import com.feed_the_beast.ftblib.lib.config.ConfigBoolean;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * @author LatvianModder
 */
public class FTBUtilitiesPlayerData implements INBTSerializable<NBTTagCompound>
{
	private final ConfigBoolean renderBadge = new ConfigBoolean(true);
	private final ConfigBoolean disableGlobalBadge = new ConfigBoolean(false);
	private final ConfigBoolean enablePVP = new ConfigBoolean(true);

	public final ForgePlayer player;
	public BlockDimPos lastDeath, lastSafePos;
	private long lastGoHome;
	public ForgeTeam lastChunkTeam;
	public final BlockDimPosStorage homes;
	public boolean fly;

	public FTBUtilitiesPlayerData(ForgePlayer p)
	{
		player = p;
		homes = new BlockDimPosStorage();
	}

	public static FTBUtilitiesPlayerData get(ForgePlayer player)
	{
		return player.getData().get(FTBUtilities.MOD_ID);
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

		nbt.setLong("LastGoHome", lastGoHome);

		return nbt;
	}

	private long getTotalWorldTime()
	{
		return player.team.universe.server.getWorld(0).getTotalWorldTime();
	}

	public long getGoHomeCooldown()
	{
		return lastGoHome + player.getRankConfig(FTBUtilitiesPermissions.HOMES_COOLDOWN).getInt() - getTotalWorldTime();
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
		lastGoHome = nbt.getLong("LastGoHome");
	}

	public void addConfig(ForgePlayerConfigEvent event)
	{
		event.getConfig().setGroupName(FTBUtilities.MOD_ID, new TextComponentString(FTBUtilities.MOD_NAME));
		event.getConfig().add(FTBUtilities.MOD_ID, "render_badge", renderBadge);
		event.getConfig().add(FTBUtilities.MOD_ID, "disable_global_badge", disableGlobalBadge);
		event.getConfig().add(FTBUtilities.MOD_ID, "enable_pvp", enablePVP);
	}

	public boolean renderBadge()
	{
		return renderBadge.getBoolean();
	}

	public boolean disableGlobalBadge()
	{
		return disableGlobalBadge.getBoolean();
	}

	public boolean enablePVP()
	{
		return enablePVP.getBoolean();
	}

	public void setLastGoHome (long tick)
	{
		lastGoHome = tick;
		player.markDirty();
	}

	public long getLastGoHome()
	{
		return lastGoHome;
	}
}