package testing.util;

import arc.util.*;
import arc.util.async.*;
import mindustry.core.*;
import mindustry.gen.*;
import testing.content.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Utils{
    private static String playerVar = "uh";
    public static String getPlayerVar(){
        return playerVar;
    }
    public static String createPlayerVar(Boolean makeNew){
        if (makeNew || playerVar == "uh") genVar();
        runCommand(playerVar + "=Groups.player.getByID(" + player.id + ");")
    }
    private static String genVar() {
        return playerVar = String.valueOf((char) (0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1))));
    }
    /** Extracts a number out of a string by removing every non-numerical character  */
    public static String extractNumber(String s){
        //God, I love google. I have no idea what the "[^\\d.]" part even is.
        return s.replaceAll("[^\\d.]", "");
    }

    public static void spawnIconEffect(String sprite){
        TUFx.iconEffect.at(player.x, player.y, 0, "test-utils-" + sprite);
    }

    public static boolean noCheat(){
        if(!net.client() && state.isCampaign()){
            /* lmao
            Groups.build.each(b -> {
                if(b.team == state.rules.defaultTeam){
                    b.kill();
                }
            });
            */
            //Threads.throwAppException(new Throwable("No cheating! Don't use Testing Utilities in campaign!"));
            //you literally already check this dont crash the game
            return false;
        }
        return true;
    }

    public static void runCommand(String command){
        Call.sendChatMessage("/js " + command);
    }

    /*public static void runCommandPlayer(String command){
        runCommand("let p = Groups.player.find(p=>p.name==\"" + fixQuotes(player.name) + "\")");
        runCommand(command);
    }
    public static void runCommandPlayerFast(String command){
        runCommand("Groups.player.find(p=>p.name==\"" + fixQuotes(player.name) + "\")" + command);
    }*/

    public static String fixQuotes(String s){
        return s.replaceAll("\"", "\\\\\""); // " > \\"
    }

    public static String roundAmount(float amount){
        amount = UI.roundAmount((int)amount);
        if(amount >= 1_000_000_000){
            return Strings.autoFixed(amount / 1_000_000_000, 1) + bundle.get("unit.billions");
        }else if(amount >= 1_000_000){
            return Strings.autoFixed(amount / 1_000_000, 1) + bundle.get("unit.millions");
        }else if(amount >= 1000){
            return Strings.autoFixed(amount / 1000, 1) + bundle.get("unit.thousands");
        }else{
            return String.valueOf((int)amount);
        }
    }
}
