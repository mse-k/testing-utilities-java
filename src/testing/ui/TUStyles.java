package testing.ui;

import arc.scene.style.*;
import arc.scene.ui.Button.*;
import arc.scene.ui.ImageButton.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static arc.Core.*;
import static arc.graphics.Color.*;

public class TUStyles{
    public static Drawable redBack, buttonLeft, buttonLeftDown, buttonLeftOver, buttonRight, buttonRightOver, buttonRightDown;
    public static ButtonStyle redButtonStyle;
    public static ImageButtonStyle tuImageStyle, tuRedImageStyle, lefti, toggleLefti, righti, toggleRighti;

    public static void init(){
        redBack = ((TextureRegionDrawable)(Tex.whiteui)).tint(0.625f, 0, 0, 0.8f);
        buttonLeft = atlas.getDrawable("test-utils-button-left");
        buttonLeftDown = atlas.getDrawable("test-utils-button-left-down");
        buttonLeftOver = atlas.getDrawable("test-utils-button-left-over");
        buttonRight = atlas.getDrawable("test-utils-button-right");
        buttonRightDown = atlas.getDrawable("test-utils-button-right-down");
        buttonRightOver = atlas.getDrawable("test-utils-button-right-over");

        redButtonStyle = new ButtonStyle(Styles.logict){{
            disabled = redBack;
        }};

        tuImageStyle = new ImageButtonStyle(Styles.logici){{
            down = Styles.flatDown;
            over = Styles.flatOver;
            imageDisabledColor = gray;
            imageUpColor = white;
        }};

        tuRedImageStyle = new ImageButtonStyle(tuImageStyle){{
            disabled = redBack;
        }};

        lefti = new ImageButtonStyle(Styles.defaulti){{
            up = buttonLeft;
            down = buttonLeftDown;
            over = buttonLeftOver;
        }};

        toggleLefti = new ImageButtonStyle(lefti){{
            checked = buttonLeftOver;
        }};

        righti = new ImageButtonStyle(Styles.defaulti){{
            up = buttonRight;
            down = buttonRightDown;
            over = buttonRightOver;
        }};

        toggleRighti = new ImageButtonStyle(righti){{
            checked = buttonRightOver;
        }};
    }
}
