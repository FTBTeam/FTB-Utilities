package com.feed_the_beast.ftbutilities.gui;

import com.feed_the_beast.ftblib.lib.EnumTeamColor;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftbutilities.data.ChunkUpgrade;
import com.feed_the_beast.ftbutilities.util.FTBUUniverseData;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ClientClaimedChunks
{
	public static class Team
	{
		private static Team currentTeam;

		public static final DataOut.Serializer<Team> SERIALIZER = (data, team) ->
		{
			data.writeUUID(team.ownerId);
			data.writeString(team.formattedName);
			data.write(EnumTeamColor.NAME_MAP, team.color);
			data.writeBoolean(team.isAlly);
			data.writeMap(team.chunks, DataOut.INT, ChunkData.SERIALIZER);
		};

		public static final DataIn.Deserializer<Team> DESERIALIZER = data ->
		{
			Team team = new Team(data.readUUID());
			team.formattedName = data.readString();
			team.color = data.read(EnumTeamColor.NAME_MAP);
			team.isAlly = data.readBoolean();
			currentTeam = team;
			data.readMap(team.chunks, DataIn.INT, ChunkData.DESERIALIZER);
			return team;
		};

		public final UUID ownerId;
		public EnumTeamColor color;
		public String formattedName;
		public boolean isAlly;
		public final Map<Integer, ChunkData> chunks = new Int2ObjectOpenHashMap<>();
		public Object shapeProperties;

		public Team(UUID id)
		{
			ownerId = id;
		}
	}

	public static class ChunkData
	{
		public static final DataOut.Serializer<ChunkUpgrade> UPGRADE_SERIALIZER = (data, upgrade) -> data.writeString(upgrade.getName());
		public static final DataIn.Deserializer<ChunkUpgrade> UPGRADE_DESERIALIZER = data -> FTBUUniverseData.CHUNK_UPGRADES.get(data.readString());

		public static final DataOut.Serializer<ChunkData> SERIALIZER = (data, d) -> data.writeCollection(d.upgrades, UPGRADE_SERIALIZER);
		public static final DataIn.Deserializer<ChunkData> DESERIALIZER = data ->
		{
			ChunkData d = new ChunkData(Team.currentTeam);
			data.readCollection(d.upgrades, UPGRADE_DESERIALIZER);
			return d;
		};

		public final Team team;
		public final Collection<ChunkUpgrade> upgrades = new HashSet<>();

		public ChunkData(Team t)
		{
			team = t;
		}

		public boolean hasUpgrade(ChunkUpgrade upgrade)
		{
			return upgrades.contains(upgrade);
		}

		public void setHasUpgrade(ChunkUpgrade upgrade, boolean val)
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

		public int hashCode()
		{
			return Objects.hash(team, upgrades);
		}

		public boolean equals(Object o)
		{
			if (o == this)
			{
				return true;
			}
			else if (o != null && o.getClass() == ChunkData.class)
			{
				ChunkData d = (ChunkData) o;
				return team.equals(d.team) && upgrades.equals(d.upgrades);
			}
			return false;
		}
	}
}