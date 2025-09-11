package ace.actually.ccdrones.menu.slots;

import ace.actually.ccdrones.CCDrones;
import com.mojang.datafixers.util.Pair;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.impl.TurtleUpgrades;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class DroneUpgradeSlot extends Slot {
    public static final ResourceLocation LEFT_UPGRADE = new ResourceLocation(ComputerCraftAPI.MOD_ID, "gui/turtle_upgrade_left");
    public static final ResourceLocation RIGHT_UPGRADE = new ResourceLocation(ComputerCraftAPI.MOD_ID, "gui/turtle_upgrade_right");

    private final DroneUpgradeType side;

    public DroneUpgradeSlot(Container container, DroneUpgradeType side, int slot, int xPos, int yPos) {
        super(container, slot, xPos, yPos);
        this.side = side;
    }

    TagKey<Item> FRONT = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("ccdrones", "drone_upgrade_front"));
    TagKey<Item> BACK = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("ccdrones", "drone_upgrade_back"));
    TagKey<Item> TOP = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("ccdrones", "drone_upgrade_top"));
    TagKey<Item> INTERNAL = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("ccdrones", "drone_upgrade_internal"));
    TagKey<Item> BOTTOM = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("ccdrones", "drone_upgrade_bottom"));
    TagKey<Item> LEFT = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("ccdrones", "drone_upgrade_left"));
    TagKey<Item> RIGHT = TagKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation("ccdrones", "drone_upgrade_right"));

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (side == DroneUpgradeType.FRONT) {
            return stack.is(FRONT);
        } else if (side == DroneUpgradeType.BACK) {
            return stack.is(BACK);
        } else if (side == DroneUpgradeType.TOP) {
            return stack.is(TOP);
        } else if (side == DroneUpgradeType.INTERNAL) {
            return stack.is(INTERNAL);
        } else if (side == DroneUpgradeType.BOTTOM) {
            return stack.is(BOTTOM);
        } else if (side == DroneUpgradeType.LEFT) {
            return stack.is(LEFT);
        } else if (side == DroneUpgradeType.RIGHT) {
            return stack.is(RIGHT);
        }
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    /*
    @Nullable
    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return Pair.of(InventoryMenu.BLOCK_ATLAS, side == TurtleSide.LEFT ? LEFT_UPGRADE : RIGHT_UPGRADE);
    }*/
}
