package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.events.team.ForgeTeamConfigEvent;
import com.feed_the_beast.ftblib.lib.EnumTeamStatus;
import com.feed_the_beast.ftblib.lib.config.ConfigBoolean;
import com.feed_the_beast.ftblib.lib.config.ConfigEnum;
import com.feed_the_beast.ftblib.lib.config.ConfigString;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbutilities.FTBUFinals;
import com.feed_the_beast.ftbutilities.FTBUPermissions;
import com.feed_the_beast.ftbutilities.integration.FTBLibIntegration;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class FTBUTeamData implements INBTSerializable<NBTTagCompound>
{
	public static FTBUTeamData get(ForgeTeam team)
	{
		return team.getData().get(FTBLibIntegration.FTBU_DATA);
	}

	public final ForgeTeam team;
	private final ConfigEnum<EnumTeamStatus> editBlocks = new ConfigEnum<>(EnumTeamStatus.NAME_MAP_PERMS);
	private final ConfigEnum<EnumTeamStatus> interactWithBlocks = new ConfigEnum<>(EnumTeamStatus.NAME_MAP_PERMS);
	private final ConfigEnum<EnumTeamStatus> attackEntities = new ConfigEnum<>(EnumTeamStatus.NAME_MAP_PERMS);
	public final ConfigBoolean explosions = new ConfigBoolean(false);
	public final ConfigString tag = new ConfigString("", 3);
	public boolean canForceChunks = false;

	public FTBUTeamData(ForgeTeam t)
	{
		team = t;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("Explosions", explosions.getBoolean());
		nbt.setString("EditBlocks", editBlocks.getString());
		nbt.setString("InteractWithBlocks", interactWithBlocks.getString());
		nbt.setString("AttackEntities", attackEntities.getString());
		nbt.setString("Tag", tag.getString());

		Int2ObjectOpenHashMap<NBTTagList> claimedChunks = new Int2ObjectOpenHashMap<>();

		for (ClaimedChunk chunk : ClaimedChunks.instance.getTeamChunks(team))
		{
			ChunkDimPos pos = chunk.getPos();

			NBTTagList list = claimedChunks.get(pos.dim);

			if (list == null)
			{
				list = new NBTTagList();
				claimedChunks.put(pos.dim, list);
			}

			NBTTagCompound chunkNBT = new NBTTagCompound();
			chunkNBT.setInteger("x", pos.posX);
			chunkNBT.setInteger("z", pos.posZ);

			if (chunk.isLoaded())
			{
				chunkNBT.setBoolean("loaded", true);
			}

			list.appendTag(chunkNBT);
		}

		NBTTagCompound claimedChunksTag = new NBTTagCompound();

		for (Map.Entry<Integer, NBTTagList> entry : claimedChunks.entrySet())
		{
			claimedChunksTag.setTag(entry.getKey().toString(), entry.getValue());
		}

		if (!claimedChunksTag.hasNoTags())
		{
			nbt.setTag("ClaimedChunks", claimedChunksTag);
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		explosions.setBoolean(nbt.getBoolean("Explosions"));
		editBlocks.setValueFromString(nbt.getString("EditBlocks"), false);
		interactWithBlocks.setValueFromString(nbt.getString("InteractWithBlocks"), false);
		attackEntities.setValueFromString(nbt.getString("AttackEntities"), false);
		tag.setValueFromString(nbt.getString("Tag"), false);

		NBTTagCompound claimedChunksTag = nbt.getCompoundTag("ClaimedChunks");

		for (String dim : claimedChunksTag.getKeySet())
		{
			NBTTagList list = claimedChunksTag.getTagList(dim, Constants.NBT.TAG_COMPOUND);
			int dimInt = Integer.parseInt(dim);

			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound chunkNBT = list.getCompoundTagAt(i);
				ClaimedChunk chunk = new ClaimedChunk(new ChunkDimPos(new ChunkPos(chunkNBT.getInteger("x"), chunkNBT.getInteger("z")), dimInt), this);
				chunk.setLoaded(chunkNBT.getBoolean("loaded"));
				ClaimedChunks.instance.addChunk(chunk);
			}
		}
	}

	public EnumTeamStatus getStatusFromType(BlockInteractionType type)
	{
		if (type == BlockInteractionType.INTERACT)
		{
			return interactWithBlocks.getValue();
		}

		return editBlocks.getValue();
	}

	public EnumTeamStatus getAttackEntitiesStatus()
	{
		return attackEntities.getValue();
	}

	public void addConfig(ForgeTeamConfigEvent event)
	{
		String group = FTBUFinals.MOD_ID;
		event.getConfig().setGroupName(group, new TextComponentString(FTBUFinals.MOD_NAME));
		event.getConfig().add(group, "explosions", explosions);
		event.getConfig().add(group, "blocks_edit", editBlocks);
		event.getConfig().add(group, "blocks_interact", interactWithBlocks);
		event.getConfig().add(group, "attack_entities", attackEntities);
		event.getConfig().add(group, "tag", tag);
	}

	public int getMaxClaimChunks()
	{
		int p = 0;

		for (ForgePlayer player : team.getMembers())
		{
			p += Ranks.getRank(team.universe.server, player.getProfile()).getConfig(FTBUPermissions.CLAIMS_MAX_CHUNKS).getInt();
		}

		return p;
	}

	public int getMaxChunkloaderChunks()
	{
		int p = 0;

		for (ForgePlayer player : team.getMembers())
		{
			p += Ranks.getRank(team.universe.server, player.getProfile()).getConfig(FTBUPermissions.CHUNKLOADER_MAX_CHUNKS).getInt();
		}

		return p;
	}
}