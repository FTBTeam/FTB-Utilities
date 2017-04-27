package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.lib.client.FTBLibClient;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbu.FTBU;
import com.feed_the_beast.ftbu.gui.Guides;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.server.command.CommandTreeBase;

/**
 * @author LatvianModder
 */
public class CmdInternalClient extends CommandTreeBase
{
    public static final String CMD = "ftbu_client_internal";

    public CmdInternalClient()
    {
        FTBU.PROXY.registerClientCommands(this);
    }

    @SideOnly(Side.CLIENT)
    public void registerCommands()
    {
        addSubcommand(new CmdBase()
        {
            @Override
            public String getName()
            {
                return "refresh_guide";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
            {
                Guides.refresh();
            }
        });

        addSubcommand(new CmdBase()
        {
            @Override
            public String getName()
            {
                return "open_guide";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
            {
                Guides.openGui();
            }
        });

        addSubcommand(new CmdBase()
        {
            @Override
            public String getName()
            {
                return "toggle_gamemode";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
            {
                FTBLibClient.execClientCommand("/gamemode " + (Minecraft.getMinecraft().player.capabilities.isCreativeMode ? 0 : 1));
            }
        });
    }

    @Override
    public String getName()
    {
        return CMD;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "command.ftbu_client_internal.usage";
    }
}
