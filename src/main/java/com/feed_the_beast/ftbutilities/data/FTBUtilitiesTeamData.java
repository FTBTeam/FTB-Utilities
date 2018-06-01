package com.feed_the_beast.ftbutilities.data;

import com.feed_the_beast.ftblib.events.team.ForgeTeamConfigEvent;
import com.feed_the_beast.ftblib.lib.EnumTeamStatus;
import com.feed_the_beast.ftblib.lib.config.ConfigBoolean;
import com.feed_the_beast.ftblib.lib.config.ConfigEnum;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.IHasCache;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;
import java.util.OptionalInt;

/**
 * @author LatvianModder
 */
public class FTBUtilitiesTeamData implements INBTSerializable<NBTTagCompound>, IHasCache
{
	public static FTBUtilitiesTeamData get(ForgeTeam team)
	{
		return team.getData(FTBUtilities.MOD_ID);
	}

	public final ForgeTeam team;
	private final ConfigEnum<EnumTeamStatus> editBlocks = new ConfigEnum<>(EnumTeamStatus.NAME_MAP_PERMS);
	private final ConfigEnum<EnumTeamStatus> interactWithBlocks = new ConfigEnum<>(EnumTeamStatus.NAME_MAP_PERMS);
	private final ConfigEnum<EnumTeamStatus> attackEntities = new ConfigEnum<>(EnumTeamStatus.NAME_MAP_PERMS);
	private final ConfigBoolean explosions = new ConfigBoolean(false);
	public boolean canForceChunks = false;
	private int cachedMaxClaimChunks, cachedMaxChunkloaderChunks;

	public FTBUtilitiesTeamData(ForgeTeam t)
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

		if (ClaimedChunks.isActive())
		{
			Int2ObjectOpenHashMap<NBTTagList> claimedChunks = new Int2ObjectOpenHashMap<>();

			for (ClaimedChunk chunk : ClaimedChunks.instance.getTeamChunks(team, OptionalInt.empty()))
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
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		explosions.setBoolean(nbt.getBoolean("Explosions"));
		editBlocks.setValue(nbt.getString("EditBlocks"));
		interactWithBlocks.setValue(nbt.getString("InteractWithBlocks"));
		attackEntities.setValue(nbt.getString("AttackEntities"));

		if (ClaimedChunks.isActive())
		{
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
	}

	public void addConfig(ForgeTeamConfigEvent event)
	{
		event.getConfig().setGroupName(FTBUtilities.MOD_ID, new TextComponentString(FTBUtilities.MOD_NAME));
		event.getConfig().add(FTBUtilities.MOD_ID, "explosions", explosions);
		event.getConfig().add(FTBUtilities.MOD_ID, "blocks_edit", editBlocks);
		event.getConfig().add(FTBUtilities.MOD_ID, "blocks_interact", interactWithBlocks);
		event.getConfig().add(FTBUtilities.MOD_ID, "attack_entities", attackEntities);
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

	public boolean hasExplosions()
	{
		return explosions.getBoolean();
	}

	public int getMaxClaimChunks()
	{
		if (!ClaimedChunks.isActive())
		{
			return -1;
		}
		else if (!team.isValid())
		{
			return -2;
		}
		else if (cachedMaxClaimChunks >= 0)
		{
			return cachedMaxClaimChunks;
		}

		cachedMaxClaimChunks = 0;

		for (ForgePlayer player : team.getMembers())
		{
			cachedMaxClaimChunks += player.getRankConfig(FTBUtilitiesPermissions.CLAIMS_MAX_CHUNKS).getInt();
		}

		return cachedMaxClaimChunks;
	}

	public int getMaxChunkloaderChunks()
	{
		if (!ClaimedChunks.isActive())
		{
			return -1;
		}
		else if (!team.isValid())
		{
			return -2;
		}
		else if (cachedMaxChunkloaderChunks >= 0)
		{
			return cachedMaxChunkloaderChunks;
		}

		cachedMaxChunkloaderChunks = 0;

		for (ForgePlayer player : team.getMembers())
		{
			cachedMaxChunkloaderChunks += player.getRankConfig(FTBUtilitiesPermissions.CHUNKLOADER_MAX_CHUNKS).getInt();
		}

		return cachedMaxChunkloaderChunks;
	}

	@Override
	public void clearCache()
	{
		cachedMaxClaimChunks = -1;
		cachedMaxChunkloaderChunks = -1;
	}
}