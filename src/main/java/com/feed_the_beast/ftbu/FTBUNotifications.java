package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.FTBLibLang;
import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.INotification;
import com.feed_the_beast.ftbl.api_impl.Notification;
import com.feed_the_beast.ftbu.world.chunks.ClaimedChunk;
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
    public static final INotification NO_TEAM = Notification.error(register("no_team"), FTBLibLang.TEAM_NO_TEAM.textComponent());
    public static final INotification CANT_MODIFY_CHUNK = Notification.error(register("cant_modify_chunk"), new TextComponentString("Can't modify this chunk!"));
    public static final INotification UNCLAIMED_ALL = new Notification(register("unclaimed_all")).addText(new TextComponentString("Unclaimed all chunks"));
    private static final int CHUNK_MODIFIED = register("chunk_modified");
    public static final INotification CHUNK_CLAIMED = new Notification(CHUNK_MODIFIED).addText(new TextComponentString("Chunk claimed"));
    public static final INotification CHUNK_UNCLAIMED = new Notification(CHUNK_MODIFIED).addText(new TextComponentString("Chunk unclaimed"));
    public static final INotification CHUNK_LOADED = new Notification(CHUNK_MODIFIED).addText(new TextComponentString("Chunk loaded"));
    public static final INotification CHUNK_UNLOADED = new Notification(CHUNK_MODIFIED).addText(new TextComponentString("Chunk unloaded"));
    private static final int CHUNK_CHANGED = register("chunk_changed");

    private static int register(String s)
    {
        return FTBLibAPI.get().getRegistries().notifications().getOrCreateIDFromKey(new ResourceLocation(FTBUFinals.MOD_ID, s));
    }

    public static INotification chunkChanged(@Nullable IForgeTeam team)
    {
        if(team == null)
        {
            ITextComponent msg = ClaimedChunk.LANG_WILDERNESS.textComponent();
            msg.getStyle().setBold(true);

            Notification n = new Notification(CHUNK_CHANGED);
            n.addText(msg);
            n.setTimer(3000);
            n.setColor(0xFF00A010);
            n.setItem(new ItemStack(Blocks.VINE));
            return n;
        }

        ITextComponent msg = new TextComponentString(team.getTitle());
        msg.getStyle().setBold(true);
        Notification n = new Notification(CHUNK_CHANGED);
        n.addText(msg);

        if(team.getDesc() != null)
        {
            msg = new TextComponentString(team.getDesc());
            msg.getStyle().setItalic(true);
            n.addText(msg);
        }

        n.setTimer(3000);
        n.setColor(0xFF000000 | team.getColor().getColor());
        return n;
    }

    public static void init()
    {
    }
}