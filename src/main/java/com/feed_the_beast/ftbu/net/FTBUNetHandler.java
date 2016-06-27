package com.feed_the_beast.ftbu.net;

import com.feed_the_beast.ftbl.api.net.LMNetworkWrapper;

public class FTBUNetHandler
{
    static final LMNetworkWrapper NET = LMNetworkWrapper.newWrapper("FTBU");

    public static void init()
    {
        NET.register(1, new MessageSendBadge());
        //2
        NET.register(3, new MessageRequestBadge());
        NET.register(4, new MessageRequestServerInfo());
        NET.register(5, new MessageAreaUpdate());
        NET.register(6, new MessageAreaRequest());
    }
}