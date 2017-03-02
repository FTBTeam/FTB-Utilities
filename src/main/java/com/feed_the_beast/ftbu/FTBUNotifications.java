package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api.INotification;
import com.feed_the_beast.ftbl.api.NotificationId;
import com.feed_the_beast.ftbl.lib.Notification;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrade;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 31.07.2016.
 */
public class FTBUNotifications
{
    public static final INotification NO_TEAM = create("no_team", 0).setError(FTBLibLang.TEAM_NO_TEAM.textComponent());
    public static final INotification CANT_MODIFY_CHUNK = create("cant_modify_chunk", 0).setError(new TextComponentString("Can't modify this chunk!"));
    public static final INotification CLAIMING_NOT_ENABLED = create("cant_claim_chunk", 0).setError(new TextComponentString("Claiming is not enabled on this server!"));
    public static final INotification CLAIMING_NOT_ALLOWED = create("cant_claim_chunk", 1).setError(new TextComponentString("You are not allowed to claim this chunk"));
    public static final INotification UNCLAIMED_ALL = create("unclaimed_all", 0).addText(new TextComponentString("Unclaimed all chunks"));
    public static final INotification CHUNK_CLAIMED = create("chunk_modified", 0).addText(new TextComponentString("Chunk claimed"));
    public static final INotification CHUNK_UNCLAIMED = create("chunk_modified", 1).addText(new TextComponentString("Chunk unclaimed"));
    public static final INotification CHUNK_LOADED = create("chunk_modified", 2).addText(new TextComponentString("Chunk loaded"));
    public static final INotification CHUNK_UNLOADED = create("chunk_modified", 3).addText(new TextComponentString("Chunk unloaded"));
    public static final Notification WILDERNESS = create("chunk_changed", 0).setTimer(3000).setColor(0xFF3A913A).setItem(new ItemStack(Blocks.VINE));

    static
    {
        ITextComponent msg = ChunkUpgrade.WILDERNESS.getLangKey().textComponent();
        msg.getStyle().setBold(true);
        WILDERNESS.addText(msg);
    }

    public static void init(IFTBLibRegistry reg)
    {
        reg.addNotification(NO_TEAM);
        reg.addNotification(CANT_MODIFY_CHUNK);
        reg.addNotification(CLAIMING_NOT_ENABLED);
        reg.addNotification(CLAIMING_NOT_ALLOWED);
        reg.addNotification(UNCLAIMED_ALL);
        reg.addNotification(CHUNK_CLAIMED);
        reg.addNotification(CHUNK_UNCLAIMED);
        reg.addNotification(CHUNK_LOADED);
        reg.addNotification(CHUNK_UNLOADED);
        reg.addNotification(WILDERNESS);
    }

    private static Notification create(String s, int v)
    {
        return new Notification(new NotificationId(FTBUFinals.get(s), v));
    }

    public static INotification chunkClaimedFor(int chunkXPos, int chunkZPos, int dimension, IForgePlayer p)
    {
        String label = String.format("Claimed the chunk %d, %d in dim [%d] on behalf of %s", chunkXPos, chunkZPos, dimension, p.getName());
        return create("chunk_modified", 0).addText(new TextComponentString(label));
    }

    public static INotification chunkUnclaimedFor(int chunkXPos, int chunkZPos, int dimension, IForgePlayer p)
    {
        String label = String.format("Unclaimed the chunk %d, %d in dim [%d] on behalf of %s", chunkXPos, chunkZPos, dimension, p.getName());
        return create("chunk_modified", 0).addText(new TextComponentString(label));
    }

    public static INotification chunkChanged(@Nullable IForgeTeam team)
    {
        if(team == null)
        {
            return WILDERNESS;
        }

        ITextComponent msg = new TextComponentString(team.getTitle());
        msg.getStyle().setBold(true);
        Notification n = new Notification(WILDERNESS.getId().variant(1));
        n.addText(msg);

        if(!team.getDesc().isEmpty())
        {
            msg = new TextComponentString(team.getDesc());
            msg.getStyle().setItalic(true);
            n.addText(msg);
        }

        n.setTimer(3000);
        n.setColor(team.getColor().getColor());
        return n;
    }
}