package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.math.EntityDimPos;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBUNotifications;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.feed_the_beast.ftbu.handlers.FTBUPlayerEventHandler;
import com.feed_the_beast.ftbu.net.MessageUpdateChunkData;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;

/**
 * Created by LatvianModder on 27.06.2016.
 */
public class CmdChunks extends CommandTreeBase
{
    public static void updateChunk(EntityPlayerMP ep, int x, int z)
    {
        FTBUPlayerEventHandler.updateChunkMessage(ep, new ChunkDimPos(x, z, ep.dimension));
        new MessageUpdateChunkData(ep, x, z, 1, 1).sendTo(ep);
    }

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
                FTBLibIntegration.API.sendNotification(ep, FTBUNotifications.CHUNK_CLAIMED);
                updateChunk(ep, pos.posX, pos.posZ);
            }
            else
            {
                FTBLibIntegration.API.sendNotification(ep, FTBUNotifications.CANT_MODIFY_CHUNK);
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

            if(!p.equalsPlayer(FTBUtilitiesAPI_Impl.INSTANCE.getClaimedChunks().getChunkOwner(pos)) && !PermissionAPI.hasPermission(ep.getGameProfile(), FTBUPermissions.CLAIMS_MODIFY_OTHER_CHUNKS, new BlockPosContext(ep, pos.getChunkPos())))
            {
                throw new CommandException("commands.generic.permission");
            }

            if(FTBUUniverseData.unclaimChunk(p, pos))
            {
                FTBLibIntegration.API.sendNotification(ep, FTBUNotifications.CHUNK_UNCLAIMED);
                updateChunk(ep, pos.posX, pos.posZ);
            }
            else
            {
                FTBLibIntegration.API.sendNotification(ep, FTBUNotifications.CANT_MODIFY_CHUNK);
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
                FTBLibIntegration.API.sendNotification(ep, FTBUNotifications.CHUNK_LOADED);
                updateChunk(ep, pos.posX, pos.posZ);
            }
            else
            {
                FTBLibIntegration.API.sendNotification(ep, FTBUNotifications.CANT_MODIFY_CHUNK);
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
                FTBLibIntegration.API.sendNotification(ep, FTBUNotifications.CHUNK_UNLOADED);
                updateChunk(ep, pos.posX, pos.posZ);
            }
            else
            {
                FTBLibIntegration.API.sendNotification(ep, FTBUNotifications.CANT_MODIFY_CHUNK);
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
                if(!PermissionAPI.hasPermission(ep, FTBUPermissions.CLAIMS_MODIFY_OTHER_CHUNKS))
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
            FTBLibIntegration.API.sendNotification(ep, FTBUNotifications.UNCLAIMED_ALL);
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

            for(ChunkDimPos chunk : FTBUtilitiesAPI_Impl.INSTANCE.getLoadedChunks().getChunks(p))
            {
                FTBUtilitiesAPI_Impl.INSTANCE.getLoadedChunks().setLoaded(chunk, null);
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
        addSubcommand(new CmdClaim());
        addSubcommand(new CmdUnclaim());
        addSubcommand(new CmdLoad());
        addSubcommand(new CmdUnload());

        addSubcommand(new CmdUnclaimAll());
        addSubcommand(new CmdUnloadAll());
        addSubcommand(new CmdAdminUnclaimAll());
    }

    @Override
    public String getCommandName()
    {
        return "chunks";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "command.ftb.chunks.usage";
    }
}
