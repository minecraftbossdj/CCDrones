package ace.actually.ccdrones.items;

import ace.actually.ccdrones.entities.DroneEntity;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerComputerRegistry;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public class DroneControllerItem extends Item {
    public DroneControllerItem() {
        super(new Properties());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        if(livingEntity instanceof DroneEntity drone && !player.level().isClientSide)
        {
            ServerComputer computer = getServerComputer(player.getServer(),drone.getComputerUUID());
            PlatformHelper.get().openMenu(player,player.getItemInHand(interactionHand).getHoverName(),(id, inventory, entity) -> new ComputerMenuWithoutInventory((MenuType) ModRegistry.Menus.COMPUTER.get(), id, inventory, (p) -> true, computer), new ComputerContainerData(computer,ItemStack.EMPTY));
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    public static ServerComputer getServerComputer(ServerComputerRegistry registry, UUID pcUUID) {
        if (pcUUID != null) {
            return registry.get(pcUUID);
        }
        return null;
    }

    @Nullable
    public static ServerComputer getServerComputer(MinecraftServer server, UUID instanceId) {
        if (server != null) {
            return getServerComputer(ServerContext.get(server).registry(), instanceId);
        } else {
            return null;
        }
    }

}
