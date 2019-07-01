package com.feed_the_beast.ftbutilities.gui;

import com.feed_the_beast.ftblib.lib.EnumTeamColor;
import com.feed_the_beast.ftblib.lib.io.Bits;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.text.ITextComponent;

import java.util.Map;
import java.util.Objects;

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
			data.writeShort(team.uid);
			data.writeTextComponent(team.nameComponent);
			EnumTeamColor.NAME_MAP.write(data, team.color);
			data.writeBoolean(team.isAlly);
			data.writeMap(team.chunks, DataOut.INT, ChunkData.SERIALIZER);
		};

		public static final DataIn.Deserializer<Team> DESERIALIZER = data ->
		{
			Team team = new Team(data.readShort());
			team.nameComponent = data.readTextComponent();
			team.color = EnumTeamColor.NAME_MAP.read(data);
			team.isAlly = data.readBoolean();
			currentTeam = team;
			data.readMap(team.chunks, DataIn.INT, ChunkData.DESERIALIZER);
			return team;
		};

		public final short uid;
		public EnumTeamColor color;
		public ITextComponent nameComponent;
		public boolean isAlly;
		public final Map<Integer, ChunkData> chunks = new Int2ObjectOpenHashMap<>();
		public Object shapeProperties;

		public Team(short id)
		{
			uid = id;
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