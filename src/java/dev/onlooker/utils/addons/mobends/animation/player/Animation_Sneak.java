//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Administrator\Downloads\Minecraft1.12.2 Mappings"!

//Decompiled by Procyon!

package dev.onlooker.utils.addons.mobends.animation.player;

import dev.onlooker.utils.addons.mobends.animation.Animation;
import dev.onlooker.utils.addons.mobends.client.model.ModelRendererBends;
import dev.onlooker.utils.addons.mobends.client.model.entity.ModelBendsPlayer;
import dev.onlooker.utils.addons.mobends.data.EntityData;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class Animation_Sneak extends Animation
{
    public String getName() {
        return "sneak";
    }
    
    public void animate(final EntityLivingBase argEntity, final ModelBase argModel, final EntityData argData) {
        final ModelBendsPlayer model = (ModelBendsPlayer)argModel;
        final float var = (float)(model.armSwing * 0.6662f / 3.141592653589793) % 2.0f;
        ((ModelRendererBends)model.bipedRightLeg).rotation.setSmoothX(-5.0f + 1.1f * (float)(MathHelper.cos(model.armSwing * 0.6662f) * 1.4f * model.armSwingAmount / 3.141592653589793 * 180.0), 1.0f);
        ((ModelRendererBends)model.bipedLeftLeg).rotation.setSmoothX(-5.0f + 1.1f * (float)(MathHelper.cos(model.armSwing * 0.6662f + 3.1415927f) * 1.4f * model.armSwingAmount / 3.141592653589793 * 180.0), 1.0f);
        ((ModelRendererBends)model.bipedRightLeg).rotation.setSmoothZ(10.0f);
        ((ModelRendererBends)model.bipedLeftLeg).rotation.setSmoothZ(-10.0f);
        ((ModelRendererBends)model.bipedRightArm).rotation.setSmoothX(-20.0f + 20.0f * MathHelper.cos(model.armSwing * 0.6662f + 3.1415927f));
        ((ModelRendererBends)model.bipedLeftArm).rotation.setSmoothX(-20.0f + 20.0f * MathHelper.cos(model.armSwing * 0.6662f));
        model.bipedLeftForeLeg.rotation.setSmoothX((float)((var > 1.0f) ? 45 : 10), 0.3f);
        model.bipedRightForeLeg.rotation.setSmoothX((float)((var > 1.0f) ? 10 : 45), 0.3f);
        model.bipedLeftForeArm.rotation.setSmoothX((float)((var > 1.0f) ? -10 : -45), 0.01f);
        model.bipedRightForeArm.rotation.setSmoothX((float)((var > 1.0f) ? -45 : -10), 0.01f);
        final float var2 = 25.0f + (float)Math.cos(model.armSwing * 0.6662f * 2.0f) * 5.0f;
        ((ModelRendererBends)model.bipedBody).rotation.setSmoothX(var2);
        ((ModelRendererBends)model.bipedHead).rotation.setX(model.headRotationX - ((ModelRendererBends)model.bipedBody).rotation.getX());
    }
}
