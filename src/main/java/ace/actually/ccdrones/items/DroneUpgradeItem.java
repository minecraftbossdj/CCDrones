package ace.actually.ccdrones.items;

import ace.actually.ccdrones.entities.DroneEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DroneUpgradeItem extends Item {

    private final String upgrade;
    public DroneUpgradeItem(String upgrade) {
        super(new Properties());
        this.upgrade=upgrade;
    }

    public String getUpgrade() {
        return upgrade;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        if(itemStack.getItem() instanceof DroneUpgradeItem droneUpgradeItem && !player.level().isClientSide && player.isCrouching())
        {
            if(livingEntity instanceof DroneEntity drone) {
                if (!drone.hasUpgrade(getUpgrade())) {
                    drone.addUpgrade(droneUpgradeItem.getUpgrade());
                    itemStack.setCount(itemStack.getCount() - 1);
                    player.displayClientMessage(Component.literal("Added " + getUpgrade() + " Upgrade To Drone!"), true);
                } else {
                    player.displayClientMessage(Component.literal("Drone already has " + getUpgrade()), true);
                }
            }
        }
        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }
}
