package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Created by LatvianModder on 28.04.2016.
 */
public class CmdJS extends CommandLM
{
    private final ScriptEngine engine;

    public CmdJS()
    {
        engine = new ScriptEngineManager(null).getEngineByName("nashorn");
    }

    @Override
    public String getCommandName()
    {
        return "js";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if(engine == null)
        {
            throw FTBLibLang.RAW.commandError("Error: No JavaScript engine found!");
        }

        checkArgs(args, 1, "<file | 'raw'> [script...]");

        try
        {
            if(args[0].equals("raw"))
            {
                sender.addChatMessage(new TextComponentString(String.valueOf(engine.eval(String.join(" ", LMStringUtils.shiftArray(args))))));
            }
            else
            {
                throw FTBLibLang.FEATURE_DISABLED.commandError();
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();

            for(String s : ex.getLocalizedMessage().split("\n"))
            {
                sender.addChatMessage(new TextComponentString("> " + s.replace('\r', ' ')));
            }
        }
    }
}