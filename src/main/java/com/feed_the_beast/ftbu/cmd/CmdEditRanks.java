package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.config.IConfigContainer;
import com.feed_the_beast.ftbl.api.config.IConfigTree;
import com.feed_the_beast.ftbl.lib.cmd.CmdEditConfigBase;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.google.gson.JsonObject;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 30.09.2016.
 */
public class CmdEditRanks extends CmdEditConfigBase
{
    @Override
    public String getCommandName()
    {
        return "edit_ranks";
    }

    @Override
    public IConfigContainer getConfigContainer(ICommandSender sender) throws CommandException
    {
        return new IConfigContainer()
        {
            @Override
            public IConfigTree getConfigTree()
            {
                return Ranks.INSTANCE.ranksConfigTree;
            }

            @Override
            public ITextComponent getTitle()
            {
                return new TextComponentString("Ranks"); //TODO: Lang
            }

            @Override
            public void saveConfig(ICommandSender sender, @Nullable NBTTagCompound nbt, JsonObject json)
            {
                Ranks.INSTANCE.ranksConfigTree.fromJson(json);
                Ranks.INSTANCE.saveRanks();
            }
        };
    }
}
