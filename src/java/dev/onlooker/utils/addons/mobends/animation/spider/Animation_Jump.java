//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Administrator\Downloads\Minecraft1.12.2 Mappings"!

//Decompiled by Procyon!

package dev.onlooker.utils.addons.mobends.animation.spider;

import dev.onlooker.utils.addons.mobends.animation.Animation;
import dev.onlooker.utils.addons.mobends.client.model.ModelRendererBends;
import dev.onlooker.utils.addons.mobends.client.model.entity.ModelBendsSpider;
import dev.onlooker.utils.addons.mobends.data.Data_Spider;
import dev.onlooker.utils.addons.mobends.data.EntityData;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.MathHelper;

public class Animation_Jump extends Animation
{
    public String getName() {
        return "jump";
    }
    
    public void animate(final EntityLivingBase argEntity, final ModelBase argModel, final EntityData argData) {
        final EntitySpider spider = (EntitySpider)argEntity;
        final ModelBendsSpider model = (ModelBendsSpider)argModel;
        final Data_Spider data = (Data_Spider)argData;
        final float f9 = -(MathHelper.cos(model.armSwing * 0.6662f * 2.0f + 0.0f) * 0.4f) * model.armSwingAmount;
        final float f10 = -(MathHelper.cos(model.armSwing * 0.6662f * 2.0f + 3.1415927f) * 0.4f) * model.armSwingAmount;
        final float f11 = -(MathHelper.cos(model.armSwing * 0.6662f * 2.0f + 1.5707964f) * 0.4f) * model.armSwingAmount;
        final float f12 = -(MathHelper.cos(model.armSwing * 0.6662f * 2.0f + 4.712389f) * 0.4f) * model.armSwingAmount;
        final float f13 = Math.abs(MathHelper.sin(model.armSwing * 0.6662f + 0.0f) * 0.4f) * model.armSwingAmount;
        final float f14 = Math.abs(MathHelper.sin(model.armSwing * 0.6662f + 3.1415927f) * 0.4f) * model.armSwingAmount;
        final float f15 = Math.abs(MathHelper.sin(model.armSwing * 0.6662f + 1.5707964f) * 0.4f) * model.armSwingAmount;
        final float f16 = Math.abs(MathHelper.sin(model.armSwing * 0.6662f + 4.712389f) * 0.4f) * model.armSwingAmount;
        model.renderOffset.setY(0.0f);
        final float bodyRotation = data.motion.y * -120.0f;
        model.renderRotation.setSmoothX(bodyRotation, 0.3f);
        model.renderOffset.setSmoothX(0.0f, 0.6f);
        model.renderOffset.setSmoothZ(0.0f, 0.6f);
        ((ModelRendererBends)model.spiderHead).rotation.setY(model.headRotationY);
        ((ModelRendererBends)model.spiderHead).rotation.setX(model.headRotationX);
        final float f17 = 0.7853982f;
        final float sm = 40.0f;
        ((ModelRendererBends)model.spiderLeg1).rotation.setZ(sm + f13 / 3.1415927f * 180.0f);
        ((ModelRendererBends)model.spiderLeg2).rotation.setZ(-sm - f13 / 3.1415927f * 180.0f);
        ((ModelRendererBends)model.spiderLeg3).rotation.setZ(sm + f14 / 3.1415927f * 180.0f);
        ((ModelRendererBends)model.spiderLeg4).rotation.setZ(-sm - f14 / 3.1415927f * 180.0f);
        ((ModelRendererBends)model.spiderLeg5).rotation.setZ(sm + f15 / 3.1415927f * 180.0f);
        ((ModelRendererBends)model.spiderLeg6).rotation.setZ(-sm - f15 / 3.1415927f * 180.0f);
        ((ModelRendererBends)model.spiderLeg7).rotation.setZ(sm + f16 / 3.1415927f * 180.0f + 20.0f);
        ((ModelRendererBends)model.spiderLeg8).rotation.setZ(-sm - f16 / 3.1415927f * 180.0f - 20.0f);
        final float f18 = -0.0f;
        final float f19 = 0.3926991f;
        ((ModelRendererBends)model.spiderLeg1).pre_rotation.setY(-70.0f + f9 / 3.1415927f * 180.0f);
        ((ModelRendererBends)model.spiderLeg2).pre_rotation.setY(70.0f - f9 / 3.1415927f * 180.0f);
        ((ModelRendererBends)model.spiderLeg3).pre_rotation.setY(-40.0f + f10 / 3.1415927f * 180.0f);
        ((ModelRendererBends)model.spiderLeg4).pre_rotation.setY(40.0f - f10 / 3.1415927f * 180.0f);
        ((ModelRendererBends)model.spiderLeg5).pre_rotation.setY(40.0f + f11 / 3.1415927f * 180.0f);
        ((ModelRendererBends)model.spiderLeg6).pre_rotation.setY(-40.0f - f11 / 3.1415927f * 180.0f);
        ((ModelRendererBends)model.spiderLeg7).pre_rotation.setY(70.0f + f12 / 3.1415927f * 180.0f);
        ((ModelRendererBends)model.spiderLeg8).pre_rotation.setY(-70.0f - f12 / 3.1415927f * 180.0f);
        final float foreBend = 89.0f;
        model.spiderForeLeg1.rotation.setZ(-foreBend);
        model.spiderForeLeg2.rotation.setZ(foreBend);
        model.spiderForeLeg3.rotation.setZ(-foreBend);
        model.spiderForeLeg4.rotation.setZ(foreBend);
        model.spiderForeLeg5.rotation.setZ(-foreBend);
        model.spiderForeLeg6.rotation.setZ(foreBend);
        model.spiderForeLeg7.rotation.setSmoothZ(-foreBend + 20.0f);
        model.spiderForeLeg8.rotation.setSmoothZ(foreBend - 20.0f);
        ((ModelRendererBends)model.spiderBody).rotation.setX(-30.0f);
    }
}
