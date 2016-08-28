package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.events.player.AttachPlayerCapabilitiesEvent;
import com.feed_the_beast.ftbl.api.events.player.ForgePlayerDeathEvent;
import com.feed_the_beast.ftbl.api.events.player.ForgePlayerInfoEvent;
import com.feed_the_beast.ftbl.api.events.player.ForgePlayerLoggedInEvent;
import com.feed_the_beast.ftbl.api.events.player.ForgePlayerLoggedOutEvent;
import com.feed_the_beast.ftbl.api.events.player.ForgePlayerSettingsEvent;
import com.feed_the_beast.ftbl.api.item.LMInvUtils;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBUNotifications;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.IClaimedChunk;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.config.FTBUConfigLogin;
import com.feed_the_beast.ftbu.world.data.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.data.FTBUWorldDataMP;
import com.google.gson.JsonElement;
import com.latmod.lib.math.BlockDimPos;
import com.latmod.lib.math.ChunkDimPos;
import com.latmod.lib.math.EntityDimPos;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FTBUPlayerEventHandler
{
    @SubscribeEvent
    public void attachCapabilities(AttachPlayerCapabilitiesEvent event)
    {
        event.addCapability(new ResourceLocation(FTBUFinals.MOD_ID, "data"), new FTBUPlayerData());
    }

    @SubscribeEvent
    public void onLoggedIn(ForgePlayerLoggedInEvent event)
    {
        if(event.getPlayer().hasCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null))
        {
            EntityPlayerMP ep = event.getPlayer().getPlayer();

            if(event.isFirstLogin())
            {
                if(FTBUConfigLogin.enable_starting_items.getAsBoolean())
                {
                    for(ItemStack is : FTBUConfigLogin.starting_items.getItems())
                    {
                        LMInvUtils.giveItem(ep, is);
                    }
                }
            }

            if(FTBUConfigLogin.enable_motd.getAsBoolean())
            {
                FTBUConfigLogin.motd.components.forEach(ep::addChatMessage);
            }

            FTBUChunkEventHandler.INSTANCE.markDirty(null);
        }
    }

    @SubscribeEvent
    public void onLoggedOut(ForgePlayerLoggedOutEvent event)
    {
        if(event.getPlayer().hasCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null))
        {
            FTBUChunkEventHandler.INSTANCE.markDirty(null);
        }
    }

    @SubscribeEvent
    public void onDeath(ForgePlayerDeathEvent event)
    {
        if(event.getPlayer().hasCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null))
        {
            FTBUPlayerData data = event.getPlayer().getCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null);
            data.lastDeath = new EntityDimPos(event.getPlayer().getPlayer()).toBlockDimPos();
        }
    }

    @SubscribeEvent
    public void getSettings(ForgePlayerSettingsEvent event)
    {
        if(event.getPlayer().hasCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null))
        {
            FTBUPlayerData data = event.getPlayer().getCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null);
            event.getSettings().add("ftbu", data.createConfigGroup());
        }
    }

    @SubscribeEvent
    public void addInfo(ForgePlayerInfoEvent event)
    {
        /*
        if(owner.getRank().config.show_rank.getMode())
		{
		    Rank rank = getRank();
		    IChatComponent rankC = new ChatComponentText("[" + rank.ID + "]");
		    rankC.getChatStyle().setColor(rank.color.getMode());
		    info.add(rankC);
		}
		*/
    }

    @SubscribeEvent
    public void onChunkChanged(EntityEvent.EnteringChunk e)
    {
        if(e.getEntity().worldObj.isRemote || !(e.getEntity() instanceof EntityPlayerMP))
        {
            return;
        }

        EntityPlayerMP ep = (EntityPlayerMP) e.getEntity();
        IForgePlayer player = FTBLibAPI.get().getUniverse().getPlayer(ep);

        if(player == null || !player.isOnline())
        {
            return;
        }

        FTBUPlayerData data = player.getCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null);
        IClaimedChunk chunk = FTBUWorldDataMP.chunks.getChunk(new ChunkDimPos(ep.dimension, e.getNewChunkX(), e.getNewChunkZ()));

        data.lastSafePos = new EntityDimPos(ep).toBlockDimPos();

        String newTeamID = (chunk == null || chunk.getOwner().getTeam() == null) ? "" : chunk.getOwner().getTeamID();

        if(data.lastChunkID == null || !data.lastChunkID.equals(newTeamID))
        {
            data.lastChunkID = newTeamID;

            if(!newTeamID.isEmpty())
            {
                IForgeTeam team = chunk.getOwner().getTeam();

                if(team != null)
                {
                    FTBLibAPI.get().sendNotification(ep, FTBUNotifications.chunkChanged(team));
                }
            }
            else
            {
                FTBLibAPI.get().sendNotification(ep, FTBUNotifications.chunkChanged(null));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerAttacked(LivingAttackEvent e)
    {
        if(e.getEntity().worldObj.isRemote)
        {
            return;
        }

        if(e.getEntity().dimension != 0 || !(e.getEntity() instanceof EntityPlayerMP) || e.getEntity() instanceof FakePlayer)
        {
            return;
        }

        Entity entity = e.getSource().getSourceOfDamage();

        if(entity != null && (entity instanceof EntityPlayerMP || entity instanceof IMob))
        {
            if(entity instanceof FakePlayer)
            {
                return;
            }
            /*else if(entity instanceof EntityPlayerMP && PermissionAPI.hasPermission(((EntityPlayerMP) entity).getGameProfile(), FTBLibPermissions.INTERACT_SECURE, false, new Context(entity)))
            {
                return;
            }*/

            if((FTBUConfigGeneral.safe_spawn.getAsBoolean() && FTBUWorldDataMP.isInSpawnD(e.getEntity().dimension, e.getEntity().posX, e.getEntity().posZ)))
            {
                e.setCanceled(true);
            }
            /*else
            {
				ClaimedChunk c = Claims.getMode(dim, cx, cz);
				if(c != null && c.claims.settings.isSafe()) e.setCanceled(true);
			}*/
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if(event.getEntityPlayer() instanceof EntityPlayerMP)
        {
            IForgePlayer player = FTBLibAPI.get().getUniverse().getPlayer(event.getEntityPlayer());

            if(player != null)
            {
                IClaimedChunk chunk = FTBUWorldDataMP.chunks.getChunk(new BlockDimPos(event.getPos(), player.getPlayer().dimension).toChunkPos());

                if(chunk != null && !chunk.canInteract(player, false, event.getPos()))
                {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event)
    {
        if(event.getPlayer() instanceof EntityPlayerMP)
        {
            IForgePlayer player = FTBLibAPI.get().getUniverse().getPlayer(event.getPlayer());

            if(player != null)
            {
                for(JsonElement e : FTBUPermissions.CLAIMS_BREAK_WHITELIST.getJson(player.getProfile()).getAsJsonArray())
                {
                    if(e.getAsString().equals(Block.REGISTRY.getNameForObject(player.getPlayer().worldObj.getBlockState(event.getPos()).getBlock()).toString()))
                    {
                        return;
                    }
                }

                IClaimedChunk chunk = FTBUWorldDataMP.chunks.getChunk(new BlockDimPos(event.getPos(), player.getPlayer().dimension).toChunkPos());

                if(chunk != null && !chunk.canInteract(player, true, event.getPos()))
                {
                    event.setCanceled(true);
                }
            }
        }
    }
}