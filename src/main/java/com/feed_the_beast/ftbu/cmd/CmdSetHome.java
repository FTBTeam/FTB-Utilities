package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.rankconfig.RankConfigAPI;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.math.EntityDimPos;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public class CmdSetHome extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "sethome";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender ics)
    {
        return '/' + getCommandName() + " <ID>";
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        if(args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FTBUPlayerData.get(FTBLibIntegration.API.getUniverse().getPlayer(sender)).listHomes());
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
        IForgePlayer p = getForgePlayer(ep);
        FTBUPlayerData d = FTBUPlayerData.get(p);
        checkArgs(args, 1, "<home>");

        args[0] = args[0].toLowerCase();

        int maxHomes = RankConfigAPI.getRankConfig(ep, FTBUPermissions.HOMES_MAX).getInt();

        if(maxHomes <= 0 || d.homesSize() >= maxHomes)
        {
            if(maxHomes == 0 || d.getHome(args[0]) == null)
            {
                throw FTBULang.HOME_LIMIT.commandError();
            }
        }

        d.setHome(args[0], new EntityDimPos(ep).toBlockDimPos());
        FTBULang.HOME_SET.printChat(ics, args[0]);
    }
}