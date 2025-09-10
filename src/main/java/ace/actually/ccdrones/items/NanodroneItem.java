package ace.actually.ccdrones.items;

import ace.actually.ccdrones.CCDrones;
import ace.actually.ccdrones.entities.DroneEntity;
import ace.actually.ccdrones.entities.nanodrone.NanodroneEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NanodroneItem extends Item {
    public NanodroneItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        NanodroneEntity entity = new NanodroneEntity(CCDrones.NANODRONE,useOnContext.getLevel());
        entity.setPos(useOnContext.getClickLocation().x,useOnContext.getClickLocation().y+1,useOnContext.getClickLocation().z);
        if(useOnContext.getItemInHand().hasTag() && useOnContext.getItemInHand().getItem() instanceof NanodroneItem)
        {
            CompoundTag extra = useOnContext.getItemInHand().getTag().getCompound("extra");
            entity.setAllData(extra);
        }
        useOnContext.getLevel().addFreshEntity(entity);
        useOnContext.getItemInHand().grow(-1);
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        if(itemStack.hasTag() && itemStack.getItem() instanceof NanodroneItem)
        {
            CompoundTag tag = itemStack.getTag().getCompound("extra");
            list.add(Component.literal("Computer ID: "+tag.getInt("computerID")));
        }
    }
}
