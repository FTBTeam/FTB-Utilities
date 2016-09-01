package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.cmd.CommandTreeBase;
import com.feed_the_beast.ftbl.api.permissions.PermissionAPI;
import com.feed_the_beast.ftbl.api.permissions.context.ContextKey;
import com.feed_the_beast.ftbl.api.permissions.context.PlayerContext;
import com.feed_the_beast.ftbu.FTBUNotifications;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.net.MessageAreaUpdate;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import com.latmod.lib.math.ChunkDimPos;
import com.latmod.lib.math.EntityDimPos;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/**
 * Created by LatvianModder on 27.06.2016.
 */
public class CmdChunks extends CommandTreeBase
{
    public class CmdClaim extends CommandLM
    {
        public CmdClaim()
        {
            super("claim");
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 0;
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
            IForgePlayer p = getForgePlayer(ep);

            ChunkDimPos pos;

            if(args.length >= 2)
            {
                pos = new ChunkDimPos(ep.dimension, parseInt(args[0]), parseInt(args[1]));
            }
            else
            {
                pos = new EntityDimPos(ep).toBlockDimPos().toChunkPos();
            }

            if(FTBUUniverseData.claimChunk(p, pos))
            {
                FTBLibAPI.get().sendNotification(ep, FTBUNotifications.CHUNK_CLAIMED);
                new MessageAreaUpdate(pos.posX, pos.posZ, pos.dim, 1, 1).sendTo(ep);
            }
            else
            {
                FTBLibAPI.get().sendNotification(ep, FTBUNotifications.CANT_MODIFY_CHUNK);
            }
        }
    }

    public class CmdUnclaim extends CommandLM
    {
        public CmdUnclaim()
        {
            super("unclaim");
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 0;
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
            IForgePlayer p = getForgePlayer(ep);

            ChunkDimPos pos;

            if(args.length >= 2)
            {
                pos = new ChunkDimPos(parseInt(args[0]), parseInt(args[1]), ep.dimension);
            }
            else
            {
                pos = new EntityDimPos(ep).toBlockDimPos().toChunkPos();
            }

            if(!p.equalsPlayer(FTBUtilitiesAPI.get().getClaimedChunks().getChunkOwner(pos)) && !PermissionAPI.hasPermission(ep.getGameProfile(), FTBUPermissions.CLAIMS_MODIFY_OTHER_CHUNKS, false, new PlayerContext(ep).set(ContextKey.CHUNK, pos.getChunkPos())))
            {
                throw new CommandException("commands.generic.permission");
            }

            if(FTBUUniverseData.unclaimChunk(p, pos))
            {
                FTBLibAPI.get().sendNotification(ep, FTBUNotifications.CHUNK_UNCLAIMED);
                new MessageAreaUpdate(pos.posX, pos.posZ, pos.dim, 1, 1).sendTo(ep);
            }
            else
            {
                FTBLibAPI.get().sendNotification(ep, FTBUNotifications.CANT_MODIFY_CHUNK);
            }
        }
    }

    public class CmdLoad extends CommandLM
    {
        public CmdLoad()
        {
            super("load");
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 0;
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
            IForgePlayer p = getForgePlayer(ep);

            ChunkDimPos pos;

            if(args.length >= 2)
            {
                pos = new ChunkDimPos(parseInt(args[0]), parseInt(args[1]), ep.dimension);
            }
            else
            {
                pos = new EntityDimPos(ep).toBlockDimPos().toChunkPos();
            }

            if(FTBUUniverseData.setLoaded(p, pos, true))
            {
                FTBLibAPI.get().sendNotification(ep, FTBUNotifications.CHUNK_LOADED);
                new MessageAreaUpdate(pos.posX, pos.posZ, pos.dim, 1, 1).sendTo(ep);
            }
            else
            {
                FTBLibAPI.get().sendNotification(ep, FTBUNotifications.CANT_MODIFY_CHUNK);
            }
        }
    }

    public class CmdUnload extends CommandLM
    {
        public CmdUnload()
        {
            super("unload");
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 0;
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
            IForgePlayer p = getForgePlayer(ep);
            ChunkDimPos pos;

            if(args.length >= 2)
            {
                pos = new ChunkDimPos(parseInt(args[0]), parseInt(args[1]), ep.dimension);
            }
            else
            {
                pos = new EntityDimPos(ep).toBlockDimPos().toChunkPos();
            }

            if(FTBUUniverseData.setLoaded(p, pos, false))
            {
                FTBLibAPI.get().sendNotification(ep, FTBUNotifications.CHUNK_UNLOADED);
                new MessageAreaUpdate(pos.posX, pos.posZ, pos.dim, 1, 1).sendTo(ep);
            }
            else
            {
                FTBLibAPI.get().sendNotification(ep, FTBUNotifications.CANT_MODIFY_CHUNK);
            }
        }
    }

    public class CmdUnclaimAll extends CommandLM
    {
        public CmdUnclaimAll()
        {
            super("unclaim_all");
        }

        @Override
        public int getRequiredPermissionLevel()
        {
            return 0;
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(sender);

            checkArgs(args, 1, "<all_dimensions> [player]");

            IForgePlayer p;

            if(args.length >= 2)
            {
                if(!PermissionAPI.hasPermission(ep.getGameProfile(), FTBUPermissions.CLAIMS_MODIFY_OTHER_CHUNKS, false, new PlayerContext(ep)))
                {
                    throw new CommandException("commands.generic.permission");
                }

                p = getForgePlayer(args[1]);
            }
            else
            {
                p = getForgePlayer(ep);
            }

            FTBUUniverseData.unclaimAllChunks(p, parseBoolean(args[0]) ? null : ep.dimension);
            FTBLibAPI.get().sendNotification(ep, FTBUNotifications.UNCLAIMED_ALL);
        }
    }

    public class CmdUnloadAll extends CommandLM
    {
        public CmdUnloadAll()
        {
            super("admin_unload_all");
        }

        @Override
        public boolean isUsernameIndex(String[] args, int i)
        {
            return i == 0;
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
        {
            checkArgs(args, 1, "<player>");
            IForgePlayer p = getForgePlayer(args[0]);

            for(ChunkDimPos chunk : FTBUtilitiesAPI.get().getLoadedChunks().getChunks(p))
            {
                FTBUtilitiesAPI.get().getLoadedChunks().setLoaded(chunk, null);
            }

            ics.addChatMessage(new TextComponentString("Unloaded all " + p.getProfile().getName() + "'s chunks")); //TODO: Lang
        }
    }

    public class CmdAdminUnclaimAll extends CommandLM
    {
        public CmdAdminUnclaimAll()
        {
            super("admin_unclaim_all");
        }

        @Override
        public String getCommandUsage(ICommandSender ics)
        {
            return '/' + commandName + " <player | @a>";
        }

        @Override
        public boolean isUsernameIndex(String[] args, int i)
        {
            return i == 0;
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
        {
            checkArgs(args, 1, "<player>");
            IForgePlayer p = getForgePlayer(args[0]);
            FTBUUniverseData.unclaimAllChunks(p, null);
            ics.addChatMessage(new TextComponentString("Unclaimed all " + p.getProfile().getName() + "'s chunks")); //TODO: Lang
        }
    }

    public CmdChunks()
    {
        super("chunks");
        add(new CmdClaim());
        add(new CmdUnclaim());
        add(new CmdLoad());
        add(new CmdUnload());

        add(new CmdUnclaimAll());
        add(new CmdUnloadAll());
        add(new CmdAdminUnclaimAll());
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
}
