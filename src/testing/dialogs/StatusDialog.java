package testing.dialogs;

import arc.graphics.*;
import arc.input.*;
import arc.math.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class StatusDialog extends BaseDialog{
    TextField search;
    Table all = new Table();
    StatusEffect status = StatusEffects.burning;
    float duration = 10f;
    static boolean perma;

    float minDur = 0.125f, maxDur = 60f;

    public StatusDialog(){
        super("@tu-status-menu.name");

        shouldPause = false;
        addCloseButton();
        shown(this::rebuild);
        onResize(this::rebuild);
        perma = settings.getBool("tu-permanent", false);

        all.margin(20).marginTop(0f);

        cont.table(s -> {
            s.image(Icon.zoom).padRight(8);
            search = s.field(null, text -> rebuild()).growX().get();
            search.setMessageText("@players.search");
        }).fillX().padBottom(4).row();

        cont.pane(all);

        buttons.button("$tu-status-menu.clear", Icon.cancel, this::clearStatus)
            .tooltip("@tu-tooltip.status-clear");
    }

    void rebuild(){
        all.clear();
        String text = search.getText();

        all.label(
            () -> bundle.get("tu-menu.selection") + "[#" + status.color + "]" +
            status.localizedName +
            (status.permanent ? bundle.get("tu-status-menu.permaeff") : "")
        ).padBottom(6);
        all.row();

        Seq<StatusEffect> array = content.statusEffects().select(e -> e != StatusEffects.none && (text.isEmpty() || e.localizedName.toLowerCase().contains(text.toLowerCase())));
        all.table(list -> {
            list.left();

            float iconMul = 1.5f;
            int cols = (int)Mathf.clamp((graphics.getWidth() - Scl.scl(30)) / Scl.scl(32 + 10) / iconMul, 1, 22 / iconMul);
            int count = 0;

            for(StatusEffect s : array){
                Image image = new Image(s.uiIcon).setScaling(Scaling.fit);
                list.add(image).size(8 * 4 * iconMul).pad(3);

                ClickListener listener = new ClickListener();
                image.addListener(listener);
                if(!mobile){
                    image.addListener(new HandCursorListener());
                    image.update(() -> image.color.lerp(listener.isOver() || status == s ? Color.white : Color.lightGray, Mathf.clamp(0.4f * Time.delta)));
                }else{
                    image.update(() -> image.color.lerp(status == s ? Color.white : Color.lightGray, Mathf.clamp(0.4f * Time.delta)));
                }

                image.clicked(() -> {
                    if(input.keyDown(KeyCode.shiftLeft) && Fonts.getUnicode(s.name) != 0){
                        app.setClipboardText((char)Fonts.getUnicode(s.name) + "");
                        ui.showInfoFade("@copied");
                    }else{
                        status = s;
                    }
                });
                image.addListener(new Tooltip(t -> t.background(Tex.button).add(s.localizedName)));

                if((++count) % cols == 0){
                    list.row();
                }
            }
        }).growX().left().padBottom(10);
        all.row();

        all.table(d -> {
            TextField dField = TUElements.textField(
                String.valueOf(duration),
                field -> {
                    if(field.isValid()){
                        String s = Utils.extractNumber(field.getText());
                        if(!s.isEmpty()){
                            duration = Float.parseFloat(s);
                        }
                    }
                },
                () -> String.valueOf(duration)
            );

            d.slider(minDur, maxDur, 0.125f, duration, n -> {
                duration = n;
                dField.setText(String.valueOf(n));
            }).right().tooltip("@tu-tooltip.status-duration")
                .disabled(s -> perma || status.permanent);
            d.add("@tu-status-menu.duration").left().padLeft(6).tooltip("@tu-tooltip.status-duration")
                .disabled(s -> perma || status.permanent);
            d.add(dField).left().padLeft(6).tooltip("@tu-tooltip.status-duration")
                .disabled(s -> perma || status.permanent);
        }).bottom();
        all.row();

        all.table(null, b -> {
            ImageButton ab = b.button(Icon.add, TUStyles.lefti, 32, this::apply)
                .tooltip("@tu-tooltip.status-apply").get();
            ab.label(() -> "@tu-status-menu.apply").padLeft(6).growX();

            ImageButton pb = b.button(Icon.refresh, TUStyles.toggleRighti, 32, () -> perma = !perma)
                .tooltip("@tu-tooltip.status-perma").get();
            Label pl = pb.label(() -> "@tu-status-menu.perma").padLeft(6).growX().get();
            pb.setDisabled(() -> status.permanent);
            pb.update(() -> {
                pb.setChecked(perma);
                pl.setColor(pb.isDisabled() ? Color.gray : Color.white);
            });
        }).padTop(6);
    }

    void apply(){
        if(Utils.noCheat()){
            if(net.client()){
                Utils.runCommand("let tempEff = Vars.content.statusEffects().find(b => b.name === \"" + Utils.fixQuotes(status.name) + "\")"); //TODO: bad, will fix later
                Utils.runCommand(Utils.getPlayerVar() + ".unit().apply(tempEff, " + (perma ? "Number.MAX_VALUE" : duration * 60) + ");");
            }else if(player.unit() != null){
                player.unit().apply(status, perma ? Float.MAX_VALUE : duration * 60);
            }
        }
    }

    void clearStatus(){
        if(Utils.noCheat()){
            if(net.client()){
                Utils.runCommand(Utils.getPlayerVar() + ".unit().clearStatuses();");
            }else if(player.unit() != null){
                player.unit().clearStatuses();
            }
        }
    }

    public StatusEffect getStatus(){
        return status;
    }
}
