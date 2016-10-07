package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.FTBLibLang;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.INotification;
import com.feed_the_beast.ftbl.api.NotificationVariant;
import com.feed_the_beast.ftbl.lib.Notification;
import com.feed_the_beast.ftbu.api.FTBULang;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 31.07.2016.
 */
public class FTBUNotifications
{
    @NotificationVariant
    public static final INotification NO_TEAM = create("no_team", 0).setError(FTBLibLang.TEAM_NO_TEAM.textComponent());

    @NotificationVariant
    public static final INotification CANT_MODIFY_CHUNK = create("cant_modify_chunk", 0).setError(new TextComponentString("Can't modify this chunk!"));

    @NotificationVariant
    public static final INotification UNCLAIMED_ALL = create("unclaimed_all", 0).addText(new TextComponentString("Unclaimed all chunks"));

    @NotificationVariant
    public static final INotification CHUNK_CLAIMED = create("chunk_modified", 0).addText(new TextComponentString("Chunk claimed"));

    @NotificationVariant
    public static final INotification CHUNK_UNCLAIMED = create("chunk_modified", 1).addText(new TextComponentString("Chunk unclaimed"));

    @NotificationVariant
    public static final INotification CHUNK_LOADED = create("chunk_modified", 2).addText(new TextComponentString("Chunk loaded"));

    @NotificationVariant
    public static final INotification CHUNK_UNLOADED = create("chunk_modified", 3).addText(new TextComponentString("Chunk unloaded"));

    @NotificationVariant
    public static final Notification WILDERNESS = create("chunk_changed", (byte) 0).setTimer(3000).setColorID((byte) 38).setItem(new ItemStack(Blocks.VINE));

    static
    {
        ITextComponent msg = FTBULang.CHUNKTYPE_WILDERNESS.textComponent().createCopy();
        msg.getStyle().setBold(true);
        WILDERNESS.addText(msg);
    }

    private static Notification create(String s, int v)
    {
        return new Notification(new ResourceLocation(FTBUFinals.MOD_ID, s), (byte) v);
    }

    public static INotification chunkChanged(@Nullable IForgeTeam team)
    {
        if(team == null)
        {
            return WILDERNESS;
        }

        ITextComponent msg = new TextComponentString(team.getTitle());
        msg.getStyle().setBold(true);
        Notification n = new Notification(WILDERNESS.getID(), (byte) 1);
        n.addText(msg);

        if(!team.getDesc().isEmpty())
        {
            msg = new TextComponentString(team.getDesc());
            msg.getStyle().setItalic(true);
            n.addText(msg);
        }

        n.setTimer(3000);
        n.setColorID(team.getColor().getColorID());
        return n;
    }
}