package com.feed_the_beast.ftbutilities.gui;

import com.feed_the_beast.ftblib.lib.EnumTeamColor;
import com.feed_the_beast.ftblib.lib.io.Bits;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

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
			data.write(team.color, EnumTeamColor.NAME_MAP);
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
		public static final DataOut.Serializer<ChunkData> SERIALIZER = (data, d) -> data.writeInt(d.flags);
		public static final DataIn.Deserializer<ChunkData> DESERIALIZER = data -> new ChunkData(Team.currentTeam, data.readInt());

		public static final int LOADED = 1;

		public final Team team;
		public final int flags;

		public ChunkData(Team t, int f)
		{
			team = t;
			flags = f;
		}

		public int hashCode()
		{
			return Objects.hash(team, flags);
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
				return team.equals(d.team) && flags == d.flags;
			}
			return false;
		}

		public boolean isLoaded()
		{
			return Bits.getFlag(flags, LOADED);
		}
	}
}