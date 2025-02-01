package dev.onlooker.utils;

import dev.onlooker.utils.font.CustomFont;
import dev.onlooker.utils.font.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;

public interface Utils {
    Minecraft mc = Minecraft.getMinecraft();
    IFontRenderer fr = mc.fontRendererObj;

    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();

    FontUtil.FontType tenacityFont = FontUtil.FontType.TENACITY,
            iconFont = FontUtil.FontType.ICON,
            fluxIcon = FontUtil.FontType.Flux,
            tahomaFont = FontUtil.FontType.TAHOMA;

    public static final FontUtil.FontType museoSansFont = FontUtil.FontType.MuseoSansCyrl;

    CustomFont museoSansFont12 = museoSansFont.size(12);
    CustomFont museoSansFont14 = museoSansFont.size(14);
    CustomFont museoSansFont16 = museoSansFont.size(16);
    CustomFont museoSansFont18 = museoSansFont.size(18);
    CustomFont museoSansFont20 = museoSansFont.size(20);
    CustomFont museoSansFont22 = museoSansFont.size(22);
    CustomFont museoSansFont24 = museoSansFont.size(24);
    public static CustomFont museoSansFont26 = museoSansFont.size(26);
    CustomFont museoSansFont28 = museoSansFont.size(28);
    CustomFont museoSansFont32 = museoSansFont.size(32);
    CustomFont museoSansFont40 = museoSansFont.size(40);
    CustomFont museoSansFont70 = museoSansFont.size(70);
    CustomFont museoSansFont78 = museoSansFont.size(78);
    CustomFont museoSansFont80 = museoSansFont.size(80);
    CustomFont museoSansBoldFont14 = museoSansFont14.getBoldFont();
    CustomFont museoSansBoldFont16 = museoSansFont16.getBoldFont();
    CustomFont museoSansBoldFont18 = museoSansFont18.getBoldFont();
    CustomFont museoSansBoldFont20 = museoSansFont20.getBoldFont();
    CustomFont museoSansBoldFont22 = museoSansFont22.getBoldFont();
    CustomFont museoSansBoldFont24 = museoSansFont24.getBoldFont();
    CustomFont museoSansBoldFont26 = museoSansFont26.getBoldFont();
    CustomFont museoSansBoldFont28 = museoSansFont28.getBoldFont();
    CustomFont museoSansBoldFont32 = museoSansFont32.getBoldFont();
    CustomFont museoSansBoldFont40 = museoSansFont40.getBoldFont();
    CustomFont museoSansBoldFont70 = museoSansFont70.getBoldFont();
    CustomFont museoSansBoldFont78 = museoSansFont78.getBoldFont();
    CustomFont museoSansBoldFont80 = museoSansFont80.getBoldFont();
    //Regular Fonts
    CustomFont tenacityFont12 = tenacityFont.size(12),
            tenacityFont14 = tenacityFont.size(14),
            tenacityFont16 = tenacityFont.size(16),
            tenacityFont18 = tenacityFont.size(18),
            tenacityFont20 = tenacityFont.size(20),
            tenacityFont22 = tenacityFont.size(22),
            tenacityFont24 = tenacityFont.size(24),
            tenacityFont26 = tenacityFont.size(26),
            tenacityFont28 = tenacityFont.size(28),
            tenacityFont32 = tenacityFont.size(32),
            tenacityFont40 = tenacityFont.size(40),
            tenacityFont50 = tenacityFont.size(50),
            tenacityFont80 = tenacityFont.size(80);

    //Bold Fonts
    CustomFont tenacityBoldFont12 = tenacityFont12.getBoldFont(),
            tenacityBoldFont14 = tenacityFont14.getBoldFont(),
            tenacityBoldFont16 = tenacityFont16.getBoldFont(),

            tenacityBoldFont18 = tenacityFont18.getBoldFont(),
            tenacityBoldFont20 = tenacityFont20.getBoldFont(),
            tenacityBoldFont22 = tenacityFont22.getBoldFont(),
            tenacityBoldFont24 = tenacityFont24.getBoldFont(),
            tenacityBoldFont26 = tenacityFont26.getBoldFont(),
            tenacityBoldFont28 = tenacityFont28.getBoldFont(),
            tenacityBoldFont32 = tenacityFont32.getBoldFont(),
            tenacityBoldFont40 = tenacityFont40.getBoldFont(),
            tenacityBoldFont50 = tenacityFont50.getBoldFont(),
            tenacityBoldFont80 = tenacityFont80.getBoldFont();

    //Icon Fontsor i
    CustomFont iconFont16 = iconFont.size(16),
            iconFont18 = iconFont.size(18),
            iconFont20 = iconFont.size(20),
            iconFont22 = iconFont.size(20),
            iconFont26 = iconFont.size(26),
            iconFont35 = iconFont.size(35),
            iconFont40 = iconFont.size(40),
            iconFont68 = iconFont.size(68);

    CustomFont fluxIcon16 = fluxIcon.size(16),
            fluxIcon18 = fluxIcon.size(18),
            fluxIcon28 = fluxIcon.size(28);



}
