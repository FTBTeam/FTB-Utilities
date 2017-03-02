package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.lib.net.LMNetworkWrapper;

public class FTBUNetHandler
{
    static final LMNetworkWrapper NET = LMNetworkWrapper.newWrapper("FTBU");

    public static void init()
    {
        NET.register(1, new MessageRequestServerInfo());
        NET.register(2, new MessageRequestBadge());
        NET.register(3, new MessageSendBadge());
        NET.register(4, new MessageClaimedChunksRequest());
        NET.register(5, new MessageClaimedChunksUpdate());
        NET.register(6, new MessageClaimedChunksModify());
        NET.register(7, new MessageRequestWarpList());
        NET.register(8, new MessageSendWarpList());
        NET.register(9, new MessageSendFTBUClientFlags());
        NET.register(10, new MessageOpenClaimedChunksGui());
        NET.register(11, new MessageJMRequest());
        NET.register(12, new MessageJMUpdate());
    }
}