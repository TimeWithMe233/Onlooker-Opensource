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

public class Animation_Riding extends Animation
{
    public String getName() {
        return "riding";
    }
    
    public void animate(final EntityLivingBase argEntity, final ModelBase argModel, final EntityData argData) {
        final ModelBendsPlayer model = (ModelBendsPlayer)argModel;
        final Data_Player data = (Data_Player)argData;
        final EntityPlayer player = (EntityPlayer)argEntity;
        model.renderOffset.setSmoothY(1.5f, 0.3f);
        ((ModelRendererBends)model.bipedBody).rotation.setSmoothY(0.0f, 0.3f);
        ((ModelRendererBends)model.bipedBody).rotation.setSmoothZ(0.0f, 0.3f);
        ((ModelRendererBends)model.bipedRightLeg).rotation.setSmoothX(-85.0f, 0.3f);
        ((ModelRendererBends)model.bipedRightLeg).rotation.setSmoothY(45.0f, 0.3f);
        ((ModelRendererBends)model.bipedLeftLeg).rotation.setSmoothX(-85.0f, 0.3f);
        ((ModelRendererBends)model.bipedLeftLeg).rotation.setSmoothY(-45.0f, 0.3f);
        model.bipedRightForeLeg.rotation.setSmoothX(60.0f);
        model.bipedLeftForeLeg.rotation.setSmoothX(60.0f);
        if (argData.motion.x == 0.0f & argData.motion.z == 0.0f) {
            ((ModelRendererBends)model.bipedRightArm).rotation.setSmoothX(-10.0f, 0.3f);
            ((ModelRendererBends)model.bipedLeftArm).rotation.setSmoothX(-10.0f, 0.3f);
            ((ModelRendererBends)model.bipedRightArm).rotation.setSmoothY(-10.0f, 0.3f);
            ((ModelRendererBends)model.bipedLeftArm).rotation.setSmoothY(10.0f, 0.3f);
            ((ModelRendererBends)model.bipedBody).rotation.setSmoothX(0.0f, 0.3f);
            model.bipedRightForeArm.rotation.setSmoothX(-20.0f, 0.3f);
            model.bipedLeftForeArm.rotation.setSmoothX(-20.0f, 0.3f);
        }
        else {
            final float jiggle = MathHelper.cos(player.ticksExisted * 0.6f) * model.armSwingAmount;
            float jiggle_hard = MathHelper.cos(player.ticksExisted * 0.3f) * model.armSwingAmount;
            if (jiggle_hard < 0.0f) {
                jiggle_hard = -jiggle_hard;
            }
            model.renderOffset.setSmoothY(1.5f + jiggle_hard * 20.0f, 0.7f);
            ((ModelRendererBends)model.bipedBody).rotation.setSmoothX(40.0f + jiggle * 300.0f, 0.3f);
            ((ModelRendererBends)model.bipedRightArm).rotation.setSmoothX(-45.0f, 0.3f);
            ((ModelRendererBends)model.bipedLeftArm).rotation.setSmoothX(-45.0f, 0.3f);
            ((ModelRendererBends)model.bipedRightArm).rotation.setSmoothY(-10.0f, 0.3f);
            ((ModelRendererBends)model.bipedLeftArm).rotation.setSmoothY(10.0f, 0.3f);
            model.bipedRightForeArm.rotation.setSmoothX(-30.0f, 0.3f);
            model.bipedLeftForeArm.rotation.setSmoothX(-30.0f, 0.3f);
        }
        ((ModelRendererBends)model.bipedHead).rotation.setSmoothY(model.headRotationY, 0.3f);
        ((ModelRendererBends)model.bipedHead).rotation.setSmoothX(model.headRotationX - model.bipedBody.rotateAngleX / 3.1415927f * 180.0f, 0.3f);
    }
}
