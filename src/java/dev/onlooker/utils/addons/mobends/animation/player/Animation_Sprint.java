//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Administrator\Downloads\Minecraft1.12.2 Mappings"!

//Decompiled by Procyon!

package dev.onlooker.utils.addons.mobends.animation.player;

import dev.onlooker.utils.addons.mobends.animation.Animation;
import dev.onlooker.utils.addons.mobends.client.model.ModelRendererBends;
import dev.onlooker.utils.addons.mobends.client.model.entity.ModelBendsPlayer;
import dev.onlooker.utils.addons.mobends.data.EntityData;
import dev.onlooker.utils.addons.mobends.util.GUtil;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class Animation_Sprint extends Animation
{
    public String getName() {
        return "sprint";
    }
    
    public void animate(final EntityLivingBase argEntity, final ModelBase argModel, final EntityData argData) {
        final ModelBendsPlayer model = (ModelBendsPlayer)argModel;
        ((ModelRendererBends)model.bipedRightArm).rotation.setSmoothX(1.1f * (float)(MathHelper.cos(model.armSwing * 0.6662f + 3.1415927f) * 2.0f * model.armSwingAmount * 0.5f / 3.141592653589793 * 180.0));
        ((ModelRendererBends)model.bipedLeftArm).rotation.setSmoothX(1.1f * (float)(MathHelper.cos(model.armSwing * 0.6662f) * 2.0f * model.armSwingAmount * 0.5f / 3.141592653589793 * 180.0));
        ((ModelRendererBends)model.bipedRightArm).rotation.setSmoothZ(5.0f, 0.3f);
        ((ModelRendererBends)model.bipedLeftArm).rotation.setSmoothZ(-5.0f, 0.3f);
        ((ModelRendererBends)model.bipedRightLeg).rotation.setSmoothX(-5.0f + 0.9f * (float)(MathHelper.cos(model.armSwing * 0.6662f) * 1.4f * model.armSwingAmount / 3.141592653589793 * 180.0), 1.0f);
        ((ModelRendererBends)model.bipedLeftLeg).rotation.setSmoothX(-5.0f + 0.9f * (float)(MathHelper.cos(model.armSwing * 0.6662f + 3.1415927f) * 1.4f * model.armSwingAmount / 3.141592653589793 * 180.0), 1.0f);
        ((ModelRendererBends)model.bipedRightLeg).rotation.setSmoothY(0.0f);
        ((ModelRendererBends)model.bipedLeftLeg).rotation.setSmoothY(0.0f);
        ((ModelRendererBends)model.bipedRightLeg).rotation.setSmoothZ(2.0f, 0.2f);
        ((ModelRendererBends)model.bipedLeftLeg).rotation.setSmoothZ(-2.0f, 0.2f);
        final float var = (float)(model.armSwing * 0.6662f / 3.141592653589793) % 2.0f;
        model.bipedLeftForeLeg.rotation.setSmoothX((float)((var > 1.0f) ? 45 : 0), 0.3f);
        model.bipedRightForeLeg.rotation.setSmoothX((float)((var > 1.0f) ? 0 : 45), 0.3f);
        model.bipedLeftForeArm.rotation.setSmoothX((float)((var > 1.0f) ? -10 : -45), 0.3f);
        model.bipedRightForeArm.rotation.setSmoothX((float)((var > 1.0f) ? -45 : -10), 0.3f);
        final float var2 = (float)Math.cos(model.armSwing * 0.6662f) * -40.0f;
        final float var3 = (float)(Math.cos(model.armSwing * 0.6662f * 2.0f) * 0.5 + 0.5) * 20.0f;
        ((ModelRendererBends)model.bipedBody).rotation.setSmoothY(var2, 0.5f);
        ((ModelRendererBends)model.bipedBody).rotation.setSmoothX(var3);
        ((ModelRendererBends)model.bipedHead).rotation.setSmoothY(model.headRotationY - var2, 0.5f);
        ((ModelRendererBends)model.bipedHead).rotation.setSmoothX(model.headRotationX - var3);
        float var4 = model.headRotationY * 0.3f;
        var4 = GUtil.max(var4, 20.0f);
        var4 = GUtil.min(var4, -20.0f);
        ((ModelRendererBends)model.bipedBody).rotation.setSmoothZ(-var4, 0.3f);
        model.renderOffset.setSmoothY(var3 * 0.15f, 0.9f);
    }
}
