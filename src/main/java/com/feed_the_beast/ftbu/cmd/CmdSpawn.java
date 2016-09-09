package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.FTBLibLang;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.permissions.PermissionAPI;
import com.feed_the_beast.ftbl.api.permissions.context.PlayerContext;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.dims.mining_dim.DimConfigMining;
import com.feed_the_beast.ftbu.dims.void_dim.DimConfigVoid;
import com.latmod.lib.EnumNameMap;
import com.latmod.lib.math.BlockDimPos;
import com.latmod.lib.util.LMServerUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nullable;
import java.util.List;

public class CmdSpawn extends CommandLM
{
    private enum EnumDimensions
    {
        OVERWORLD,
        NETHER,
        END,
        VOID,
        MINING;

        public int getDimension()
        {
            switch(this)
            {
                case NETHER:
                    return -1;
                case END:
                    return 1;
                case VOID:
                    return DimConfigVoid.dimensionType.getId();
                case MINING:
                    return DimConfigMining.dimensionType.getId();
                default:
                    return 0;
            }
        }

        private static final EnumNameMap<EnumDimensions> NAME_MAP = new EnumNameMap<>(false, values());
    }

    public CmdSpawn()
    {
        super("spawn");
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        if(args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, EnumDimensions.NAME_MAP.getKeys());
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        EntityPlayerMP player = getCommandSenderAsPlayer(ics);
        EnumDimensions ed = args.length > 0 ? EnumDimensions.NAME_MAP.get(args[0]) : EnumDimensions.OVERWORLD;

        if(ed == null)
        {
            ed = EnumDimensions.OVERWORLD;
        }

        if(!PermissionAPI.hasPermission(player.getGameProfile(), "command.ftb.spawn." + EnumNameMap.getEnumName(ed), ed == EnumDimensions.OVERWORLD, new PlayerContext(player)))
        {
            throw FTBLibLang.COMMAND_PERMISSION.commandError();
        }

        DimensionManager.initDimension(ed.getDimension());
        World w = DimensionManager.getWorld(ed.getDimension());

        BlockPos spawnpoint = (w == null) ? ics.getPosition() : w.getSpawnPoint();

        while(w != null && w.getBlockState(spawnpoint).isFullCube())
        {
            spawnpoint = spawnpoint.up(2);
        }

        LMServerUtils.teleportPlayer(player, new BlockDimPos(spawnpoint, ed.getDimension()));
        FTBULang.WARP_SPAWN.printChat(ics);
    }
}