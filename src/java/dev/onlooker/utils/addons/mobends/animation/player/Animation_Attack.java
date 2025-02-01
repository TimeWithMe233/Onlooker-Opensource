//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Administrator\Downloads\Minecraft1.12.2 Mappings"!

//Decompiled by Procyon!

package dev.onlooker.utils.addons.mobends.animation.player;

import dev.onlooker.utils.addons.mobends.animation.Animation;
import dev.onlooker.utils.addons.mobends.client.model.entity.ModelBendsPlayer;
import dev.onlooker.utils.addons.mobends.data.Data_Player;
import dev.onlooker.utils.addons.mobends.data.EntityData;
import dev.onlooker.utils.addons.mobends.pack.BendsPack;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class Animation_Attack extends Animation
{
    public String getName() {
        return "attack";
    }
    
    public void animate(final EntityLivingBase argEntity, final ModelBase argModel, final EntityData argData) {
        final ModelBendsPlayer model = (ModelBendsPlayer)argModel;
        final Data_Player data = (Data_Player)argData;
        final EntityPlayer player = (EntityPlayer)argEntity;
        if (player.getCurrentEquippedItem() != null) {
            if (data.ticksAfterPunch < 10.0f) {
                if (data.currentAttack == 1) {
                    Animation_Attack_Combo0.animate((EntityPlayer)argEntity, model, data);
                    BendsPack.animate(model, "player", "attack");
                    BendsPack.animate(model, "player", "attack_0");
                }
                else if (data.currentAttack == 2) {
                    Animation_Attack_Combo1.animate((EntityPlayer)argEntity, model, data);
                    BendsPack.animate(model, "player", "attack");
                    BendsPack.animate(model, "player", "attack_1");
                }
                else if (data.currentAttack == 3) {
                    Animation_Attack_Combo2.animate((EntityPlayer)argEntity, model, data);
                    BendsPack.animate(model, "player", "attack");
                    BendsPack.animate(model, "player", "attack_2");
                }
            }
            else if (data.ticksAfterPunch < 60.0f) {
                Animation_Attack_Stance.animate((EntityPlayer)argEntity, model, data);
                BendsPack.animate(model, "player", "attack_stance");
            }
        }
        else if (data.ticksAfterPunch < 10.0f) {
            Animation_Attack_Punch.animate((EntityPlayer)argEntity, model, data);
            BendsPack.animate(model, "player", "attack");
            BendsPack.animate(model, "player", "punch");
        }
        else if (data.ticksAfterPunch < 60.0f) {
            Animation_Attack_PunchStance.animate((EntityPlayer)argEntity, model, data);
            BendsPack.animate(model, "player", "punch_stance");
        }
    }
}
