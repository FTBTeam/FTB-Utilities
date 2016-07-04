package com.feed_the_beast.ftbu.webapi;

import com.feed_the_beast.ftbl.FTBLibStats;
import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbu.config.FTBUConfigModules;
import com.feed_the_beast.ftbu.config.FTBUConfigWebAPI;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.latmod.lib.json.LMJsonUtils;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManagerServer;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by LatvianModder on 18.06.2016.
 */
public class WebAPI extends Thread
{
    public static final WebAPI INST = new WebAPI();
    private ServerSocket serverSocket;

    private WebAPI()
    {
        super("FTBU_WebAPI");
    }

    public static String readArgs(InputStream is) throws Exception
    {
        if(is != null)
        {
            int a = is.available();

            if(a <= 0)
            {
                return Integer.toString(a);
            }

            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < a; i++)
            {
                sb.append((char) is.read());
            }

            return sb.toString();
        }

        return null;
    }

    @Override
    public void run()
    {
        try
        {
            serverSocket = new ServerSocket(FTBUConfigWebAPI.port.getAsInt());

            System.out.println(getName() + " started");

            while(isAPIRunning())
            {
                try
                {
                    Socket socket = serverSocket.accept();

                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    out.println("HTTP/1.0 200 OK");
                    out.println("Content-Type: application/json");
                    out.println("Server: Bot");
                    out.println("");

                    JsonTable table = new JsonTable();
                    table.setTitle("name", "Name");
                    table.setTitle("deaths", "Deaths");
                    table.setTitle("dph", "Deaths per hour");
                    table.setTitle("last_seen", "Last time seen");

                    for(ForgePlayer player : ForgeWorldMP.inst.playerMap.values())
                    {
                        StatisticsManagerServer stats = player.toMP().stats();

                        JsonTable.TableEntry tableEntry = new JsonTable.TableEntry();
                        tableEntry.set("name", new JsonPrimitive(player.getProfile().getName()));
                        tableEntry.set("deaths", new JsonPrimitive(stats.readStat(StatList.DEATHS)));
                        tableEntry.set("dph", new JsonPrimitive(FTBLibStats.getDeathsPerHour(stats)));
                        tableEntry.set("last_seen", new JsonPrimitive(player.toMP().isOnline() ? 0 : FTBLibStats.getLastSeen(player.toMP())));
                        table.addEntry(tableEntry);
                    }

                    JsonObject json = new JsonObject();
                    json.add("time", new JsonPrimitive(System.currentTimeMillis()));
                    json.add("stats", table.toJson());

                    String outputData = LMJsonUtils.toJson(LMJsonUtils.GSON, json);
                    out.print(outputData);

                    out.flush();
                    socket.close();

                    System.out.println("Sent data: " + outputData);
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }

            System.out.println(getName() + " closed");
        }
        catch(Exception ex0)
        {
            ex0.printStackTrace();
        }
    }

    public boolean isAPIRunning()
    {
        return FTBUConfigModules.web_api.getAsBoolean() && ForgeWorldMP.inst != null && serverSocket != null && !serverSocket.isClosed();
    }

    public void startAPI()
    {
        if(FTBUConfigModules.web_api.getAsBoolean() && !isAPIRunning())
        {
            start();
        }
    }

    public void stopAPI()
    {
        if(isAPIRunning())
        {
            try
            {
                serverSocket.close();
            }
            catch(Exception ex)
            {
                serverSocket = null;
            }
        }
    }
}