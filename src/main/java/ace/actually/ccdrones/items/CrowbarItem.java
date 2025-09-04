package ace.actually.ccdrones.items;

import ace.actually.ccdrones.entities.DroneEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;

public class CrowbarItem extends SwordItem {
    public CrowbarItem() {
        super(Tiers.IRON, 3, -2.4F, new Item.Properties());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        if(livingEntity instanceof DroneEntity drone && !player.level().isClientSide && player.isCrouching())
        {
            drone.removeUpgrades();
        }
        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity attacker) {
        if (target instanceof DroneEntity drone) {
            if (attacker instanceof Player player) {
                DamageSource dmgSource = attacker.level().damageSources().playerAttack(player);
                drone.hurt(dmgSource,drone.getMaxHealth());
            }
            return true;
        } else {
            return super.hurtEnemy(itemStack,target,attacker);
        }
    }
}
