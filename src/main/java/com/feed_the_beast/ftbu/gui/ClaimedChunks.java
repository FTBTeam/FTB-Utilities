package com.feed_the_beast.ftbu.gui;

import com.feed_the_beast.ftbl.api.EnumTeamColor;
import com.feed_the_beast.ftbl.lib.io.Bits;
import com.feed_the_beast.ftbl.lib.io.DataIn;
import com.feed_the_beast.ftbl.lib.io.DataOut;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ClaimedChunks
{
	public static class Team
	{
		public static final DataOut.Serializer<Team> SERIALIZER = (data, team) ->
		{
			data.writeUUID(team.ownerId);
			data.writeString(team.formattedName);
			data.writeByte(team.color.ordinal());
			data.writeBoolean(team.isAlly);
		};

		public static final DataIn.Deserializer<Team> DESERIALIZER = data ->
		{
			Team team = new Team();
			team.ownerId = data.readUUID();
			team.formattedName = data.readString();
			team.color = EnumTeamColor.NAME_MAP.get(data.readUnsignedByte());
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
		public int flags;
		public Team team;
		public String owner = "";

		public boolean hasUpgrade(IChunkUpgrade upgrade)
		{
			return Bits.getFlag(flags, 1 << upgrade.getId());
		}

		public void setHasUpgrade(IChunkUpgrade upgrade, boolean val)
		{
			flags = Bits.setFlag(flags, 1 << upgrade.getId(), val);
		}
	}
}