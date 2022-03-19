package testing.buttons;

import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Health{
    public static void heal(boolean invincibility){
        if(net.client()){
            Utils.runCommand(
                Utils.getPlayerVar() + ".unit().dead=!0;" +
                Utils.getPlayerVar() + ".unit().shield=" + (invincibility ? "Number.MAX_VALUE" : "0") + ";" +
                Utils.getPlayerVar() + ".unit().health=" + Utils.getPlayerVar() + ".unit().maxHealth;"
            );
        }else if(player.unit() != null && player.unit().type != null){
            Unit u = player.unit();
            u.dead = false;
            u.shield = invincibility ? Float.POSITIVE_INFINITY : u.type.health;
            u.health = u.maxHealth;
        }
        Utils.spawnIconEffect(invincibility ? "invincibility" : "heal");
    }

    public static Cell<ImageButton> healing(Table t){
        Cell<ImageButton> i = t.button(TUIcons.heal, TUStyles.tuRedImageStyle, () -> {
            heal(false);
        }).growX().tooltip("@tu-tooltip.button-heal");
        ImageButton b = i.get();
        b.setDisabled(() -> state.isCampaign());
        b.label(() -> "[" + (b.isDisabled() ? "gray" : "white") + "]" + bundle.get("tu-ui-button.heal")).growX();
        b.resizeImage(40f);
        b.update(() -> {
            b.setColor(player.team().color != null ? player.team().color : TUVars.curTeam.color);
        });

        return i;
    }

    public static Cell<ImageButton> invincibility(Table t){
        Cell<ImageButton> i = t.button(TUIcons.invincibility, TUStyles.tuRedImageStyle, () -> {
            heal(true);
        }).growX().tooltip("@tu-tooltip.button-invincibility");
        ImageButton b = i.get();
        b.setDisabled(() -> state.isCampaign());
        b.label(() -> "[" + (b.isDisabled() ? "gray" : "white") + "]" + bundle.get("tu-ui-button.invincible")).growX();
        b.resizeImage(40f);
        b.update(() -> {
            b.setColor(player.team().color != null ? player.team().color : TUVars.curTeam.color);
        });

        return i;
    }
}
