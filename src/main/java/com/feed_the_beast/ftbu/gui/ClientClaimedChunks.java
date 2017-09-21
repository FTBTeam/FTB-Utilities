package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.api.EnumTeamColor;
import com.feed_the_beast.ftbl.lib.io.DataIn;
import com.feed_the_beast.ftbl.lib.io.DataOut;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ClientClaimedChunks
{
	public static class Team
	{
		public static final DataOut.Serializer<Team> SERIALIZER = (data, team) ->
		{
			data.writeUUID(team.ownerId);
			data.writeString(team.formattedName);
			data.write(EnumTeamColor.NAME_MAP, team.color);
			data.writeBoolean(team.isAlly);
		};

		public static final DataIn.Deserializer<Team> DESERIALIZER = data ->
		{
			Team team = new Team();
			team.ownerId = data.readUUID();
			team.formattedName = data.readString();
			team.color = data.read(EnumTeamColor.NAME_MAP);
			team.isAlly = data.readBoolean();
			return team;
		};

		public UUID ownerId;
		public EnumTeamColor color;
		public String formattedName;
		public boolean isAlly;
	}

	public static class Data
	{
		public static final DataOut.Serializer<IChunkUpgrade> UPGRADE_NAME_SERIALIZER = (data, upgrade) -> data.writeString(upgrade.getName());
		public static final DataIn.Deserializer<IChunkUpgrade> UPGRADE_NAME_DESERIALIZER = data -> FTBUUniverseData.ALL_CHUNK_UPGRADES.get(data.readString());
		public static final DataOut.Serializer<IChunkUpgrade> UPGRADE_ID_SERIALIZER = (data, upgrade) -> data.writeInt(FTBUUniverseData.getUpgradeId(upgrade));
		public static final DataIn.Deserializer<IChunkUpgrade> UPGRADE_ID_DESERIALIZER = data -> FTBUUniverseData.getUpgradeFromId(data.readInt());

		public final Collection<IChunkUpgrade> upgrades = new HashSet<>();
		public Team team;

		public boolean hasUpgrade(IChunkUpgrade upgrade)
		{
			return upgrades.contains(upgrade);
		}

		public void setHasUpgrade(IChunkUpgrade upgrade, boolean val)
		{
			if (val)
			{
				upgrades.add(upgrade);
			}
			else
			{
				upgrades.remove(upgrade);
			}
		}
	}
}