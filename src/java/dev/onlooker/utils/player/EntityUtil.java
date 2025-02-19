package dev.onlooker.utils.player;

import com.mojang.authlib.GameProfile;
import dev.onlooker.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;


public class EntityUtil implements Utils {
   public static EntityPlayer getCopiedPlayer(EntityPlayer player) {
      final int count = player.getItemInUseCount();
      EntityPlayer copied = new EntityPlayer(mc.theWorld, new GameProfile(UUID.randomUUID(), player.getName())) {
         public boolean isSpectator() {
            return false;
         }

         public boolean isCreative() {
            return false;
         }

         public int getItemInUseCount() {
            return count;
         }
      };
      copied.setSneaking(player.isSneaking());
      copied.swingProgress = player.swingProgress;
      copied.limbSwing = player.limbSwing;
      copied.limbSwingAmount = player.prevLimbSwingAmount;
      copied.inventory.copyInventory(player.inventory);
      copied.ticksExisted = player.ticksExisted;
      copied.setEntityId(player.getEntityId());
      copied.copyLocationAndAnglesFrom(player);
      return copied;
   }
}
