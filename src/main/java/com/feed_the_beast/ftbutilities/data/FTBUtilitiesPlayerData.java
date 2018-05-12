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

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class FTBUtilitiesPlayerData implements INBTSerializable<NBTTagCompound>
{
	private final ConfigBoolean renderBadge = new ConfigBoolean(true);
	private final ConfigBoolean disableGlobalBadge = new ConfigBoolean(false);
	private final ConfigBoolean enablePVP = new ConfigBoolean(true);

	public final ForgePlayer player;
	public ForgeTeam lastChunkTeam;
	public final Map<ForgePlayer, Long> tpaRequestsFrom;

	private BlockDimPos lastDeath, lastSafePos;
	private long lastHome, lastWarp, lastTPA;
	public final BlockDimPosStorage homes;
	private boolean fly;

	public FTBUtilitiesPlayerData(ForgePlayer p)
	{
		player = p;
		homes = new BlockDimPosStorage();
		tpaRequestsFrom = new HashMap<>();
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
		nbt.setTag("Homes", homes.serializeNBT());
		nbt.setBoolean("AllowFlying", fly);

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
		lastDeath = BlockDimPos.fromIntArray(nbt.getIntArray("LastDeath"));
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

	public void setFly(boolean v)
	{
		fly = v;
		player.markDirty();
	}

	public boolean getFly()
	{
		return fly;
	}

	public void setLastDeath(@Nullable BlockDimPos pos)
	{
		lastDeath = pos;
		player.markDirty();
	}

	@Nullable
	public BlockDimPos getLastDeath()
	{
		return lastDeath;
	}

	public void setLastSafePos(@Nullable BlockDimPos pos)
	{
		lastSafePos = pos;
		player.markDirty();
	}

	@Nullable
	public BlockDimPos getLastSafePos()
	{
		return lastSafePos;
	}

	public void updateLastHome()
	{
		lastHome = player.team.universe.world.getTotalWorldTime();
	}

	public long getHomeCooldown()
	{
		return lastHome + player.getRankConfig(FTBUtilitiesPermissions.HOMES_COOLDOWN).getInt() - player.team.universe.world.getTotalWorldTime();
	}

	public void updateLastWarp()
	{
		lastWarp = player.team.universe.world.getTotalWorldTime();
	}

	public long getWarpCooldown()
	{
		return lastWarp + player.getRankConfig(FTBUtilitiesPermissions.WARPS_COOLDOWN).getInt() - player.team.universe.world.getTotalWorldTime();
	}

	public void updateLastTPA()
	{
		lastTPA = player.team.universe.world.getTotalWorldTime();
	}

	public long getTPACooldown()
	{
		return lastTPA + player.getRankConfig(FTBUtilitiesPermissions.TPA_COOLDOWN).getInt() - player.team.universe.world.getTotalWorldTime();
	}
}