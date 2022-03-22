package testing.buttons;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.gen.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static testing.ui.TUDialogs.*;

public class TeamChanger{
    static float tTimer;

    public static void changeTeam(Team team){
        if(Utils.noCheat()){
            if(Vars.net.client()){
                Utils.runCommand(Utils.getPlayerVar() + ".team(Team.get(" + team.id + "));");
            }else{
                player.team(team);
            }
        }
    }

    public static Cell<Button> addMini(Table t){
        Cell<Button> i = t.button(b -> {
            TUElements.boxTooltip(b, "@tu-tooltip.button-team");
            b.setDisabled(() -> state.isCampaign() || player.unit().type == UnitTypes.block);
            b.label(TeamChanger::teamName);
        }, TUStyles.redButtonStyle, () -> {
            if(tTimer > TUVars.longPress) return;
            changeTeam(getNextTeam());
        }).size(40).color(curTeam().color);

        Button b = i.get();
        b.update(() -> {
            if(b.isPressed()){
                tTimer += graphics.getDeltaTime() * 60f;
                if(tTimer > TUVars.longPress){
                    teamDialog.show(curTeam(), TeamChanger::changeTeam);
                }
            }else{
                tTimer = 0f;
            }

            b.setColor(curTeam().color);
        });

        return i;
    }

    public static Team curTeam(){
        return player.team();
    }

    public static Team getNextTeam(){
        if(player.team() == Vars.state.rules.defaultTeam){
            return Vars.state.rules.waveTeam;
        }else{
            return Vars.state.rules.defaultTeam;
        }
    }

    public static void add(Table table){
        table.table(Tex.pane, t -> {
            addMini(t).width(100);
        }).padBottom(TUVars.TCOffset);
    }

    static String teamName(){
        String t = teamDialog.teamName(curTeam());
        return "[#" + curTeam().color.toString() + "]" + t.substring(0, 1).toUpperCase() + t.substring(1); //this is probably bad but i forgor what function to use to fix it
    }
}
