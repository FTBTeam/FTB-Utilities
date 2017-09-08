package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.events.team.ForgeTeamSettingsEvent;
import com.feed_the_beast.ftbl.lib.EnumTeamPrivacyLevel;
import com.feed_the_beast.ftbl.lib.config.ConfigBoolean;
import com.feed_the_beast.ftbl.lib.config.ConfigEnum;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.handlers.FTBLibIntegration;
import com.google.common.base.Preconditions;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class FTBUTeamData implements INBTSerializable<NBTTagCompound>
{
	public final ConfigEnum<EnumTeamPrivacyLevel> editBlocks = new ConfigEnum<>(EnumTeamPrivacyLevel.NAME_MAP.withDefault(EnumTeamPrivacyLevel.ALLIES));
	public final ConfigEnum<EnumTeamPrivacyLevel> interactWithBlocks = new ConfigEnum<>(EnumTeamPrivacyLevel.NAME_MAP.withDefault(EnumTeamPrivacyLevel.ALLIES));
	public final ConfigBoolean explosions = new ConfigBoolean(false);
	public final ConfigBoolean fakePlayers = new ConfigBoolean(true);

	public static FTBUTeamData get(@Nullable IForgeTeam t)
	{
		FTBUTeamData data = t == null ? null : (FTBUTeamData) t.getData(FTBLibIntegration.FTBU_DATA);
		Preconditions.checkNotNull(data);
		return data;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("Explosions", explosions.getBoolean());
		nbt.setBoolean("FakePlayers", fakePlayers.getBoolean());
		nbt.setString("EditBlocks", editBlocks.getString());
		nbt.setString("InteractWithBlocks", interactWithBlocks.getString());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (nbt == null)
		{
			return;
		}

		explosions.setBoolean(nbt.getBoolean("Explosions"));
		fakePlayers.setBoolean(nbt.getBoolean("FakePlayers"));

		if (nbt.hasKey("EditBlocks"))
		{
			editBlocks.setValueFromString(nbt.getString("EditBlocks"), false);
		}

		if (nbt.hasKey("InteractWithBlocks"))
		{
			interactWithBlocks.setValueFromString(nbt.getString("InteractWithBlocks"), false);
		}
	}

	public void addConfig(ForgeTeamSettingsEvent event)
	{
		String group = FTBUFinals.MOD_ID;
		event.add(group, "explosions", explosions);
		event.add(group, "fake_players", fakePlayers);
		event.add(group, "blocks_edit", editBlocks);
		event.add(group, "blocks_interact", interactWithBlocks);
	}
}