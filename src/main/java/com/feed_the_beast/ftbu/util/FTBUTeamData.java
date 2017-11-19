package com.feed_the_beast.ftbu.util;

import com.feed_the_beast.ftbl.api.EnumTeamStatus;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.team.ForgeTeamConfigEvent;
import com.feed_the_beast.ftbl.lib.config.ConfigBoolean;
import com.feed_the_beast.ftbl.lib.config.ConfigEnum;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.chunks.BlockInteractionType;
import com.feed_the_beast.ftbu.api.chunks.ChunkUpgrade;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import com.feed_the_beast.ftbu.handlers.FTBLibIntegration;
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
	public static FTBUTeamData get(IForgeTeam team)
	{
		return team.getData().get(FTBLibIntegration.FTBU_DATA);
	}

	public final IForgeTeam team;
	private final ConfigEnum<EnumTeamStatus> editBlocks = new ConfigEnum<>(EnumTeamStatus.NAME_MAP_PERMS);
	private final ConfigEnum<EnumTeamStatus> interactWithBlocks = new ConfigEnum<>(EnumTeamStatus.NAME_MAP_PERMS);
	private final ConfigEnum<EnumTeamStatus> attackEntities = new ConfigEnum<>(EnumTeamStatus.NAME_MAP_PERMS);
	public final ConfigBoolean explosions = new ConfigBoolean(false);
	public boolean canForceChunks = false;

	public FTBUTeamData(IForgeTeam t)
	{
		team = t;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();

		if (explosions.getBoolean())
		{
			nbt.setBoolean("Explosions", explosions.getBoolean());
		}

		nbt.setString("EditBlocks", editBlocks.getString());

		if (interactWithBlocks.getBoolean())
		{
			nbt.setString("InteractWithBlocks", interactWithBlocks.getString());
		}

		if (attackEntities.getBoolean())
		{
			nbt.setString("AttackEntities", attackEntities.getString());
		}

		Int2ObjectOpenHashMap<NBTTagList> claimedChunks = new Int2ObjectOpenHashMap<>();

		for (ClaimedChunk chunk : ClaimedChunks.INSTANCE.getTeamChunks(team))
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

			for (ChunkUpgrade upgrade : FTBUUniverseData.CHUNK_UPGRADES.values())
			{
				if (!upgrade.isInternal() && chunk.hasUpgrade(upgrade))
				{
					chunkNBT.setBoolean(upgrade.getName(), true);
				}
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

		NBTTagCompound claimedChunksTag = nbt.getCompoundTag("ClaimedChunks");

		for (String dim : claimedChunksTag.getKeySet())
		{
			NBTTagList list = claimedChunksTag.getTagList(dim, Constants.NBT.TAG_COMPOUND);
			int dimInt = Integer.parseInt(dim);

			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound chunkNBT = list.getCompoundTagAt(i);
				ClaimedChunk chunk = new ClaimedChunk(new ChunkDimPos(new ChunkPos(chunkNBT.getInteger("x"), chunkNBT.getInteger("z")), dimInt), this);

				for (ChunkUpgrade upgrade : FTBUUniverseData.CHUNK_UPGRADES.values())
				{
					if (!upgrade.isInternal() && chunkNBT.getBoolean(upgrade.getName()))
					{
						chunk.setHasUpgrade(upgrade, true);
					}
				}

				ClaimedChunks.INSTANCE.addChunk(chunk);
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
	}

	public int getMaxClaimChunks()
	{
		int p = 0;

		for (IForgePlayer player : team.getMembers())
		{
			p += FTBUtilitiesAPI.API.getRankConfig(player.getProfile(), FTBUPermissions.CLAIMS_MAX_CHUNKS).getInt();
		}

		return p;
	}

	public int getMaxChunkloaderChunks()
	{
		int p = 0;

		for (IForgePlayer player : team.getMembers())
		{
			p += FTBUtilitiesAPI.API.getRankConfig(player.getProfile(), FTBUPermissions.CHUNKLOADER_MAX_CHUNKS).getInt();
		}

		return p;
	}
}