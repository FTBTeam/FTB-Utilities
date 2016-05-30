package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.FTBLibPermissions;
import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeTeam;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.events.ForgePlayerEvent;
import com.feed_the_beast.ftbl.api.item.LMInvUtils;
import com.feed_the_beast.ftbl.api.notification.Notification;
import com.feed_the_beast.ftbl.api.permissions.Context;
import com.feed_the_beast.ftbl.api.permissions.PermissionAPI;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.EntityDimPos;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.config.FTBUConfigLogin;
import com.feed_the_beast.ftbu.config.FTBUConfigModules;
import com.feed_the_beast.ftbu.net.MessageAreaUpdate;
import com.feed_the_beast.ftbu.world.Backups;
import com.feed_the_beast.ftbu.world.ClaimedChunk;
import com.feed_the_beast.ftbu.world.ClaimedChunks;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUPlayerDataMP;
import com.feed_the_beast.ftbu.world.FTBUPlayerDataSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FTBUPlayerEventHandler
{
    @SubscribeEvent
    public void addPlayerData(ForgePlayerEvent.AttachCapabilities event)
    {
        event.addCapability(new ResourceLocation(FTBUFinals.MOD_ID, "data"), event.player.getWorld().getSide().isServer() ? new FTBUPlayerDataMP() : new FTBUPlayerDataSP());
    }

    @SubscribeEvent
    public void onDataSynced(ForgePlayerEvent.Sync event)
    {
        if(event.player.hasCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null))
        {
            FTBUPlayerData data = event.player.getCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null);

            if(event.player.getWorld().getSide().isServer())
            {
                NBTTagCompound tag = new NBTTagCompound();
                data.writeSyncData(event.player, tag, event.self);
                event.data.setTag("FTBU", tag);
            }
            else
            {
                data.readSyncData(event.player, event.data.getCompoundTag("FTBU"), event.self);
            }
        }
    }

    @SubscribeEvent
    public void onLoggedIn(ForgePlayerEvent.LoggedIn event)
    {
        if(event.player.getWorld().getSide().isServer() && event.player.hasCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null))
        {
            EntityPlayerMP ep = event.player.toMP().getPlayer();

            if(event.first)
            {
                if(FTBUConfigModules.starting_items.getAsBoolean())
                {
                    for(ItemStack is : FTBUConfigLogin.starting_items.getItems())
                    {
                        LMInvUtils.giveItem(ep, is);
                    }
                }
            }

            if(FTBUConfigModules.motd.getAsBoolean())
            {
                FTBUConfigLogin.motd.components.forEach(ep::addChatMessage);
            }

            Backups.hadPlayer = true;

            new MessageAreaUpdate(event.player.toMP().getPos(), 4).sendTo(ep);
            FTBUChunkEventHandler.instance.markDirty(null);
        }
    }

    @SubscribeEvent
    public void onLoggedOut(ForgePlayerEvent.LoggedOut event)
    {
        if(event.player.hasCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null))
        {
            FTBUChunkEventHandler.instance.markDirty(null);
            Backups.hadPlayer = true;
        }
    }

    @SubscribeEvent
    public void onMyServerSettings(ForgePlayerEvent.MyServerSettings event)
    {
        if(event.player.hasCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null))
        {
            event.settings.add(event.player.getCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null).toMP().addMyServerSettings(), false);
        }
    }

    @SubscribeEvent
    public void addInfo(ForgePlayerEvent.AddInfo event)
    {
        if(event.player.getWorld().getSide().isServer())
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
    }

    @SubscribeEvent
    public void onChunkChanged(EntityEvent.EnteringChunk e)
    {
        if(e.getEntity().worldObj.isRemote || !(e.getEntity() instanceof EntityPlayerMP))
        {
            return;
        }

        EntityPlayerMP ep = (EntityPlayerMP) e.getEntity();
        ForgePlayerMP player = ForgeWorldMP.inst.getPlayer(ep);

        if(player == null || !player.isOnline())
        {
            return;
        }

        player.lastPos = new EntityDimPos(ep).toBlockDimPos();

        ClaimedChunk chunk = ClaimedChunks.inst.getChunk(new ChunkDimPos(ep.dimension, e.getNewChunkX(), e.getNewChunkZ()));

        FTBUPlayerDataMP d = FTBUPlayerData.get(player).toMP();

        int newTeamID = (chunk == null || !chunk.getOwner().hasTeam()) ? 0 : chunk.getOwner().getTeamID();

        if(d.lastChunksTeamID != newTeamID)
        {
            d.lastChunksTeamID = newTeamID;

            if(newTeamID > 0)
            {
                ForgeTeam team = chunk.getOwner().getTeam();

                ITextComponent msg = new TextComponentString(team.getTitle());
                msg.getStyle().setBold(true);
                Notification n = new Notification("chunk_changed", msg, 3000);

                if(team.getDesc() != null)
                {
                    msg = new TextComponentString(team.getDesc());
                    msg.getStyle().setItalic(true);
                    n.setDesc(msg);
                }

                n.setColor(0xFF000000 | team.getColor().getMapColor().colorValue);
                FTBLib.notifyPlayer(ep, n);
            }
            else
            {
                ITextComponent msg = ClaimedChunk.LANG_WILDERNESS.textComponent();
                msg.getStyle().setBold(true);
                Notification n = new Notification("chunk_changed", msg, 3000);
                n.setColor(0xFF00A010);
                FTBLib.notifyPlayer(ep, n);
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

        int dim = e.getEntity().dimension;
        if(dim != 0 || !(e.getEntity() instanceof EntityPlayerMP) || e.getEntity() instanceof FakePlayer)
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
            else if(entity instanceof EntityPlayerMP && PermissionAPI.hasPermission(((EntityPlayerMP) entity).getGameProfile(), FTBLibPermissions.INTERACT_SECURE, false, new Context(entity)))
            {
                return;
            }

            if((FTBUConfigGeneral.safe_spawn.getAsBoolean() && ClaimedChunks.isInSpawnD(dim, e.getEntity().posX, e.getEntity().posZ)))
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
}