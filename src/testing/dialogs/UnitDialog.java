package testing.dialogs;

import arc.*;
import arc.graphics.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import testing.ui.*;
import testing.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static testing.ui.TUDialogs.*;

public class UnitDialog extends BaseDialog{
    TextField search;
    Table all = new Table();
    UnitType spawnUnit = UnitTypes.dagger;
    Team spawnTeam = Team.get(settings.getInt("tu-default-team", 1));
    Vec2 spawnPos = new Vec2();
    int amount = 1;
    float radius = 2;
    static boolean despawns = true, initialized;

    boolean expectingPos;

    final int maxAmount = 100;
    final float minRadius = 0f, maxRadius = 10f;

    public UnitDialog(){
        super("@tu-unit-menu.name");

        shouldPause = false;
        addCloseButton();
        shown(this::rebuild);
        onResize(this::rebuild);
        despawns = settings.getBool("tu-despawns", true);

        all.margin(20).marginTop(0f);

        cont.table(s -> {
            s.image(Icon.zoom).padRight(8);
            search = s.field(null, text -> rebuild()).growX().get();
            search.setMessageText("@players.search");
        }).fillX().padBottom(4).row();

        cont.pane(all);

        if(!initialized){
            Events.run(Trigger.update, () -> {
                if(expectingPos){
                    if(!state.isGame()){
                        expectingPos = false;
                    }else if(input.justTouched()){
                        if(!scene.hasMouse()){
                            spawnPos.set(Mathf.round(input.mouseWorld().x), Mathf.round(input.mouseWorld().y));
                            ui.showInfoToast(bundle.format("tu-unit-menu.set-pos", spawnPos.x / 8f, spawnPos.y / 8f), 4f);
                            show();
                        }else{
                            ui.showInfoToast("@tu-unit-menu.cancel", 4f);
                        }
                        expectingPos = false;
                    }
                }
            });
            initialized = true;
        }
    }

    void rebuild(){
        expectingPos = false;
        all.clear();
        String text = search.getText();

        all.label(
            () -> bundle.get("tu-menu.selection") + spawnUnit.localizedName
        ).padBottom(6);
        all.row();

        Seq<UnitType> array = content.units().select(e -> e != UnitTypes.block && !e.isHidden() && (text.isEmpty() || e.localizedName.toLowerCase().contains(text.toLowerCase())));
        all.table(list -> {
            list.left();

            float iconMul = 1.5f;
            int cols = (int)Mathf.clamp((graphics.getWidth() - Scl.scl(30)) / Scl.scl(32 + 10) / iconMul, 1, 22 / iconMul);
            int count = 0;

            for(UnitType u : array){
                Image image = new Image(u.uiIcon).setScaling(Scaling.fit);
                list.add(image).size(8 * 4 * iconMul).pad(3);

                ClickListener listener = new ClickListener();
                image.addListener(listener);
                if(!mobile){
                    image.addListener(new HandCursorListener());
                    image.update(() -> image.color.lerp(listener.isOver() || spawnUnit == u ? Color.white : Color.lightGray, Mathf.clamp(0.4f * Time.delta)));
                }else{
                    image.update(() -> image.color.lerp(spawnUnit == u ? Color.white : Color.lightGray, Mathf.clamp(0.4f * Time.delta)));
                }

                image.clicked(() -> {
                    if(input.keyDown(KeyCode.shiftLeft) && Fonts.getUnicode(u.name) != 0){
                        app.setClipboardText((char)Fonts.getUnicode(u.name) + "");
                        ui.showInfoFade("@copied");
                    }else{
                        spawnUnit = u;
                    }
                });
                image.addListener(new Tooltip(t -> t.background(Tex.button).add(u.localizedName)));

                if((++count) % cols == 0){
                    list.row();
                }
            }
        }).growX().left().padBottom(10);
        all.row();

        all.table(t -> {
            TextField aField = TUElements.textField(
                String.valueOf(amount),
                field -> {
                    if(field.isValid()){
                        String s = Utils.extractNumber(field.getText());
                        if(!s.isEmpty()){
                            amount = Integer.parseInt(s);
                        }
                    }
                },
                () -> String.valueOf(amount)
            );

            t.slider(1, maxAmount, 1, amount, n -> {
                amount = (int)n;
                aField.setText(String.valueOf(n));
            }).right().tooltip("@tu-tooltip.unit-amount");
            t.add("@tu-unit-menu.amount").left().padLeft(6).tooltip("@tu-tooltip.unit-amount");
            t.add(aField).left().padLeft(6).tooltip("@tu-tooltip.unit-amount");

            t.row();

            TextField rField = TUElements.textField(
                String.valueOf(amount),
                field -> {
                    if(field.isValid()){
                        String s = Utils.extractNumber(field.getText());
                        if(!s.isEmpty()){
                            radius = Float.parseFloat(s);
                        }
                    }
                },
                () -> String.valueOf(radius)
            );

            t.slider(minRadius, maxRadius, 1, radius, n -> {
                radius = n;
                rField.setText(String.valueOf(n));
            }).right().tooltip("@tu-tooltip.unit-radius");
            t.add("@tu-unit-menu.radius").left().padLeft(6).tooltip("@tu-tooltip.unit-radius");
            t.add(rField).left().padLeft(6).tooltip("@tu-tooltip.unit-radius");
        });
        all.row();

        all.table(t -> {
            t.button(Icon.defense, TUStyles.lefti, 32, () -> teamDialog.show(spawnTeam, team -> spawnTeam = team))
                .tooltip("@tu-tooltip.unit-set-team").get()
                .label(() -> bundle.format("tu-unit-menu.set-team", "[#" + spawnTeam.color + "]" + teamName() + "[]")).padLeft(6).expandX();

            t.button(Icon.map, TUStyles.toggleRighti, 32, () -> {
                hide();
                expectingPos = true;
            }).tooltip("@tu-tooltip.unit-pos").get()
                .label(() -> bundle.format("tu-unit-menu.pos", spawnPos.x / 8f, spawnPos.y / 8f)).padLeft(6).expandX();
        }).padTop(6);
        all.row();

        all.table(b -> {
            ImageButton ib = b.button(Icon.units, TUStyles.lefti, 32, this::transform).expandX()
                .tooltip("@tu-tooltip.unit-transform").get();
            ib.setDisabled(() -> player.unit().type == UnitTypes.block);
            ib.label(() -> "@tu-unit-menu.transform").padLeft(6).expandX();

            ImageButton db = b.button(TUIcons.alpha, TUStyles.toggleRighti, 32, () -> despawns = !despawns).expandX()
                .tooltip("@tu-tooltip.unit-despawns").get();
            db.update(() -> db.setChecked(despawns));
            db.label(() -> "@tu-unit-menu.despawns").padLeft(6).expandX();
        }).padTop(6);

        all.row();
        all.table(b -> {
            b.button(Icon.add, TUStyles.lefti, 32, this::spawn).expandX()
                .tooltip("@tu-tooltip.unit-spawn").get()
                .label(() -> "@tu-unit-menu." + (amount != 1 ? "spawn-plural" : "spawn")).padLeft(6).expandX();

            b.button(Icon.waves, TUStyles.toggleRighti, 32, () -> {
                hide();
                waveChangeDialog.show();
            }).expandX()
                .tooltip("@tu-tooltip.unit-set-wave").get()
                .label(() -> "@tu-unit-menu.waves").padLeft(6).expandX();
        }).padTop(6);

        all.row();
        ImageButton cb = all.button(TUIcons.shard, 32f, this::placeCore).padTop(6).expandX()
            .tooltip("@tu-tooltip.unit-place-core").get();
        cb.setDisabled(() -> Vars.world.tileWorld(spawnPos.x, spawnPos.y) == null);
        cb.label(() -> "@tu-unit-menu.place-core")
            .update(l -> l.setColor(cb.isDisabled() ? Color.lightGray : Color.white)).padLeft(6).expandX();
    }

    void spawn(){
        if(Utils.noCheat()){
            if(net.client()){
                Utils.runCommand("let tempUnit = Vars.content.units().find(b => b.name === \"" + Utils.fixQuotes(spawnUnit.name) + "\")");
                Utils.runCommand("let setPos = () => Tmp.v1.setToRandomDirection().setLength(" + radius * tilesize + "*Mathf.sqrt(Mathf.random())).add(" + spawnPos.x + "," + spawnPos.y + ")");
                Utils.runCommand("for(let i=0;i<" + amount + ";i++){setPos();tempUnit.spawn(Team.get(" + spawnTeam.id + "),Tmp.v1.x,Tmp.v1.y);}"); //TODO: not touching this
            }else{
                for(int i = 0; i < amount; i++){
                    float r = radius * tilesize * Mathf.sqrt(Mathf.random());
                    Tmp.v1.setToRandomDirection().setLength(r).add(spawnPos);
                    spawnUnit.spawn(spawnTeam, Tmp.v1);
                }
            }
        }
    }

    void transform(){
        if(Utils.noCheat()){
            if(net.client()){
                Utils.runCommand(
                    "Call.unitControl(" + Utils.getPlayerVar() + ",Δ=UnitTypes." + spawnUnit.name + ".spawn(" + Utils.getPlayerVar() + ".team()," + Utils.getPlayerVar() + ".x," + Utils.getPlayerVar() + ".y));" + //fun fact: pain
                    (despawns ? "Δ.spawnedByCore=true" : "") //89 chars for longest vanilla unit name (antumbra), should be fine
                );
            }else if(player.unit() != null){
                Unit u = spawnUnit.spawn(player.team(), player);
                float rot = player.unit().rotation;
                u.controller(player);
                u.rotation = rot;
                u.spawnedByCore = despawns;
                Fx.unitControl.at(u, true);
            }
            hide();
        }
    }

    void placeCore(){
        if(Utils.noCheat()){
            if(net.client()){
                Utils.runCommand("Vars.world.tileWorld(" + spawnPos.x + "," + spawnPos.y + ").setNet(Blocks.coreShard,Team.get(" + spawnTeam.id + "),0)");
            }else{
                Vars.world.tileWorld(spawnPos.x, spawnPos.y).setNet(Blocks.coreShard, spawnTeam, 0);
            }
        }
    }

    String teamName(){
        return teamDialog.teamName(spawnTeam);
    }

    public UnitType getUnit(){
        return spawnUnit;
    }
}
