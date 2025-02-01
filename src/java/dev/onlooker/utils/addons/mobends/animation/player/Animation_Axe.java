//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Administrator\Downloads\Minecraft1.12.2 Mappings"!

//Decompiled by Procyon!

package dev.onlooker.utils.addons.mobends.animation.player;

import dev.onlooker.utils.addons.mobends.animation.Animation;
import dev.onlooker.utils.addons.mobends.client.model.ModelRendererBends;
import dev.onlooker.utils.addons.mobends.client.model.entity.ModelBendsPlayer;
import dev.onlooker.utils.addons.mobends.data.Data_Player;
import dev.onlooker.utils.addons.mobends.data.EntityData;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class Animation_Axe extends Animation
{
    public String getName() {
        return "axe";
    }
    
    public void animate(final EntityLivingBase argEntity, final ModelBase argModel, final EntityData argData) {
        final ModelBendsPlayer model = (ModelBendsPlayer)argModel;
        final Data_Player data = (Data_Player)argData;
        final EntityPlayer player = (EntityPlayer)argEntity;
        ((ModelRendererBends)model.bipedRightArm).rotation.setSmoothZero(0.3f);
        ((ModelRendererBends)model.bipedLeftArm).rotation.setSmoothZero(0.3f);
        model.bipedRightForeArm.rotation.setSmoothZero(0.3f);
        model.bipedLeftForeArm.rotation.setSmoothZero(0.3f);
        ((ModelRendererBends)model.bipedRightLeg).rotation.setSmoothZ(10.0f, 0.3f);
        ((ModelRendererBends)model.bipedLeftLeg).rotation.setSmoothZ(-10.0f, 0.3f);
        model.renderOffset.setSmoothY(-1.5f, 0.3f);
        ((ModelRendererBends)model.bipedRightArm).rotation.setSmoothY(90.0f, 0.3f);
        ((ModelRendererBends)model.bipedRightArm).pre_rotation.setSmoothY(0.0f, 0.3f);
        ((ModelRendererBends)model.bipedRightArm).pre_rotation.setSmoothX(-80.0f, 0.3f);
        ((ModelRendererBends)model.bipedLeftArm).rotation.setSmoothY(90.0f, 0.3f);
        ((ModelRendererBends)model.bipedLeftArm).pre_rotation.setSmoothY(-40.0f, 0.3f);
        ((ModelRendererBends)model.bipedLeftArm).pre_rotation.setSmoothX(-70.0f, 0.3f);
        if (player.isSwingInProgress) {
            final float speed = 1.8f;
            final float progress = player.ticksExisted * speed / 20.0f % 1.0f;
            final float progress2 = (player.ticksExisted - 2) * speed / 20.0f % 1.0f;
            float armSwing = (MathHelper.cos(progress * 3.1415927f * 2.0f) + 1.0f) / 2.0f * 2.0f;
            if (armSwing > 1.0f) {
                armSwing = 1.0f;
            }
            ((ModelRendererBends)model.bipedRightArm).rotation.setSmoothY(90.0f, 0.3f);
            ((ModelRendererBends)model.bipedRightArm).pre_rotation.setSmoothY(40.0f - 90.0f * armSwing, 0.7f);
            ((ModelRendererBends)model.bipedRightArm).pre_rotation.setSmoothX(-80.0f, 0.7f);
            ((ModelRendererBends)model.bipedLeftArm).rotation.setSmoothY(90.0f, 0.3f);
            ((ModelRendererBends)model.bipedLeftArm).pre_rotation.setSmoothY(0.0f - 70.0f * armSwing, 0.7f);
            ((ModelRendererBends)model.bipedLeftArm).pre_rotation.setSmoothX(-70.0f, 0.7f);
            ((ModelRendererBends)model.bipedBody).rotation.setSmoothY(armSwing * 20.0f);
            ((ModelRendererBends)model.bipedHead).rotation.setX(model.headRotationX - armSwing * 20.0f);
            ((ModelRendererBends)model.bipedHead).rotation.setY(model.headRotationY - armSwing * 20.0f);
        }
    }
}
