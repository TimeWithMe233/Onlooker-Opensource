package dev.onlooker.utils.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ibm.icu.impl.duration.impl.Utils;
import dev.onlooker.Client;
import dev.onlooker.module.Module;
import dev.onlooker.module.impl.display.ArrayListMod;
import dev.onlooker.utils.animations.Animation;
import dev.onlooker.utils.animations.Direction;
import dev.onlooker.utils.animations.impl.DecelerateAnimation;
import dev.onlooker.utils.misc.HoveringUtil;
import dev.onlooker.utils.misc.MathUtil;
import dev.onlooker.utils.render.RenderUtil;
import dev.onlooker.utils.skidfont.FontManager;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.List;

import static dev.onlooker.utils.Utils.*;

public class Dragging extends Utils {
    @Expose
    @SerializedName("scale-x")
    @Getter
    @Setter
    private float bPos;

    @Expose
    @SerializedName("scale-y")
    @Getter
    @Setter
    private float cPos;

    @Expose
    @SerializedName("x")
    @Getter
    @Setter
    private float xPos;

    @Expose
    @SerializedName("y")
    @Getter
    @Setter
    private float yPos;

    @Expose
    @SerializedName("name")
    @Getter
    @Setter
    private String name;

    public float initialXVal;
    public float initialYVal;

    private float startX, startY;
    private boolean dragging;

    @Getter
    @Setter
    private float width, height;

    @Getter
    private final Module module;

    public Animation hoverAnimation = new DecelerateAnimation(100, 1, Direction.BACKWARDS);
    public Dragging(Module module, String name, float x, float y) {
        ScaledResolution sr = new ScaledResolution(mc);
        this.module = module;
        this.name = name;
        this.xPos = x;
        this.yPos = y;
        this.initialXVal = x;
        this.initialYVal = y;
    }
    public final void onDraw(int mouseX, int mouseY) {
        ScaledResolution sr = new ScaledResolution(mc);
        boolean hovering = HoveringUtil.isHovering(xPos, yPos, width, height, mouseX, mouseY);
        if (dragging) {
            xPos = (mouseX - startX);
            yPos = (mouseY - startY);
            if(xPos < 0)
                xPos = 0;
            if(yPos < 0)
                yPos = 0;
            if(xPos  + getWidth() > sr.getScaledWidth())
                xPos = sr.getScaledWidth() - getWidth();
            if(yPos  + getHeight() > sr.getScaledHeight())
                yPos = sr.getScaledHeight() - getHeight();
            this.setBPos((float) (xPos/sr.getScaledWidth_double()));
            this.setCPos((float) (yPos/sr.getScaledHeight_double()));
        }
        hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        if((xPos - 4 + xPos - 4 + width + 8)/2< (float) sr.getScaledWidth() /2) {
            tenacityBoldFont14.drawString("X:"+String.format("%.1f",xPos), xPos - 4 + width + 8,
                    yPos + 6, new Color(255, 255, 255, 160));
            tenacityBoldFont14.drawString("Y:"+String.format("%.1f",yPos), xPos - 4 + width + 8,
                    yPos + 4 + tenacityBoldFont14.getStringWidth("Y:"+String.format("%.1f",yPos)) - 4, new Color(255, 255, 255, 160));
        } else {
            tenacityBoldFont14.drawString("X:"+String.format("%.1f",xPos), xPos - 3 -
                    tenacityBoldFont14.getStringWidth("X:"+String.format("%.1f",xPos)) - 1, yPos + 6,new Color(255, 255, 255, 160));
            tenacityBoldFont14.drawString("Y:"+String.format("%.1f",yPos), xPos - 3 -
                    tenacityBoldFont14.getStringWidth("Y:"+String.format("%.1f",yPos)) - 1, yPos - 4 +
                    tenacityBoldFont14.getStringWidth("Y:"+String.format("%.1f",yPos)) + 4, new Color(255, 255, 255, 160));
        }
        tenacityBoldFont18.drawString(this.getName(), (float) (xPos + (27 *  hoverAnimation.getOutput()) + 2 ), yPos - 11 - 3+ 3, new Color(255, 255, 255, 150).getRGB());
        if (!hoverAnimation.isDone() || hoverAnimation.finished(Direction.FORWARDS)) {
            RenderUtil.scaleStart((xPos + tenacityBoldFont18.getStringWidth("Move") / 2f), yPos + 3, hoverAnimation.getOutput().floatValue());
            tenacityBoldFont18.drawString("Move", (float) (xPos - 8 - 17 + (27 *  hoverAnimation.getOutput())+2), yPos - 14 + 3,
                    new Color(255, 255, 255, 180).getRGB());
            RenderUtil.scaleEnd();
        }
    }

    public final void onDrawArraylist(ArrayListMod arraylistMod, int mouseX, int mouseY) {
        ScaledResolution sr = new ScaledResolution(mc);

        List<Module> modules = Client.INSTANCE.getModuleCollection().getArraylistModules(arraylistMod, arraylistMod.modules);

        String longest = getLongestModule(arraylistMod);

        width = (float) MathUtil.roundToHalf(FontManager.PingFang_bold16.getStringWidth(longest) + 5);
        height = (float) MathUtil.roundToHalf((arraylistMod.height.getValue().floatValue() + 1) * modules.size());

        float textVal = FontManager.PingFang_bold16.getStringWidth(longest);
        float xVal = sr.getScaledWidth() - (textVal + 8 + xPos);

        if (sr.getScaledWidth() - xPos <= sr.getScaledWidth() / 2f) {
            xVal += textVal - 2;
        }

        boolean hovering = HoveringUtil.isHovering(xVal, yPos, width, height, mouseX, mouseY);

        if (dragging) {
            xPos = -(mouseX - startX);
            yPos = mouseY - startY;
        }
        hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        tenacityBoldFont18.drawString(this.getName(), (float) (xVal + 8.0f + (24 *  hoverAnimation.getOutput())),
                yPos - 15 - 3 + 3, new Color(255, 255, 255, 150).getRGB());
        if (!hoverAnimation.isDone() || hoverAnimation.finished(Direction.FORWARDS)) {
            RenderUtil.scaleStart((xVal + tenacityBoldFont18.getStringWidth("Move") / 2f), yPos - 3 + 6, hoverAnimation.getOutput().floatValue());
            tenacityBoldFont18.drawString("Move", (float) (xVal - 8 - 12 + (27 *  hoverAnimation.getOutput())), yPos - 18 + 3,
                    new Color(255, 255, 255, 180).getRGB());
            RenderUtil.scaleEnd();
        }
    }

    public final void onClick(int mouseX, int mouseY, int button) {
        boolean canDrag = HoveringUtil.isHovering(xPos, yPos, width, height, mouseX, mouseY);
        if (button == 0 && canDrag) {
            dragging = true;
            startX = (int) (mouseX - xPos);
            startY = (int) (mouseY - yPos);
        }
    }

    public final void onClickArraylist(ArrayListMod arraylistMod, int mouseX, int mouseY, int button) {
        ScaledResolution sr = new ScaledResolution(mc);

        String longest = getLongestModule(arraylistMod);

        float textVal = FontManager.PingFang_bold16.getStringWidth(longest);
        float xVal = sr.getScaledWidth() - (textVal + 8 + xPos);

        if (sr.getScaledWidth() - xPos <= sr.getScaledWidth() / 2f) {
            xVal += textVal - 2;
        }

        boolean canDrag = HoveringUtil.isHovering(xVal, yPos - 8, width + 20, height + 16, mouseX, mouseY);

        if (button == 0 && canDrag) {
            dragging = true;
            startX = (int) (mouseX + xPos);
            startY = (int) (mouseY - yPos);
        }
    }

    private String getLongestModule(ArrayListMod arraylistMod) {
        return arraylistMod.longest;
    }

    public final void onRelease(int button) {
        if (button == 0) dragging = false;
    }

    public float getX() {
        return xPos;
    }

    public void setX(float x) {
        this.xPos = x;
    }

    public float getY() {
        return yPos;
    }

    public void setY(float y) {
        this.yPos = y;
    }

}
