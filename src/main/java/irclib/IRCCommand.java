package irclib;

import java.util.List;

/**
 * @author progwml6
 */
public interface IRCCommand {

    /**
     *
     * @param parameters sent(examples: "!tps 0" or "!players")
     * @return list of strings to send back to irc or null if you don't want to send anything
     */
    public List<String> onCommand(String parameters);

}
