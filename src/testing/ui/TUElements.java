package testing.ui;

import arc.func.*;
import arc.graphics.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.*;

public class TUElements{
    public static void sliderSet(Table t, Cons<TextField> changed, Prov<String> fieldText, float min, float max, float step, float def, Cons2<Float, TextField> sliderChanged, String title, String tooltip){
        TextField field = textField(String.valueOf(def), changed, fieldText);

        Tooltip tip = new Tooltip(to -> to.background(Tex.button).add(tooltip));
        t.slider(min, max, step, def, s -> sliderChanged.get(s, field)).right().get().addListener(tip);
        t.add(title).left().padLeft(6f).get().addListener(tip);
        t.add(field).left().padLeft(6f).get().addListener(tip);
    }

    public static TextField textField(String text, Cons<TextField> changed, Prov<String> setText){
        TextField field = new TextField(text);
        field.changed(() -> changed.get(field));
        field.update(() -> {
            Scene stage = field.getScene();
            if(!(stage != null && stage.getKeyboardFocus() == field))
                field.setText(setText.get());
        });

        return field;
    }

    public static Stack itemImage(TextureRegionDrawable region, Prov<CharSequence> text){
        return itemImage(region, text, Color.white, Color.white, 1f, Align.bottomLeft);
    }

    public static Stack itemImage(TextureRegionDrawable region, Prov<CharSequence> text, Color icolor, Color tcolor, float fontScl, int align){
        Stack stack = new Stack();

        Table t = new Table().align(align);
        t.label(text).color(tcolor).fontScale(fontScl);

        Image i = new Image(region).setScaling(Scaling.fit);
        i.setColor(icolor);

        stack.add(i);
        stack.add(t);
        return stack;
    }

    public static void boxTooltip(Element e, Prov<CharSequence> text){
        e.addListener(new Tooltip(t -> t.background(Tex.button).label(text)));
    }

    public static void boxTooltip(Element e, String text){
        e.addListener(new Tooltip(t -> t.background(Tex.button).add(text)));
    }
}
