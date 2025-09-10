package ace.actually.ccdrones.entities.nanodrone;

import ace.actually.ccdrones.CCDrones;
import ace.actually.ccdrones.entities.DroneAPI;
import ace.actually.ccdrones.entities.DroneEntity;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerComputerRegistry;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import dan200.computercraft.shared.util.IDAssigner;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class NanodroneEntity extends Mob {

    private final NanodroneInventory inventory = new NanodroneInventory(1);

    public Container getInventory() {
        return inventory;
    }

    private boolean shouldMakeBoot = false;
    private Vec3 targetPos;

    public static final EntityDataAccessor<CompoundTag> EXTRA = SynchedEntityData.defineId(NanodroneEntity.class, EntityDataSerializers.COMPOUND_TAG);
    public NanodroneEntity(EntityType<NanodroneEntity> e, Level level) {
        super(e, level);
    }

    public static AttributeSupplier.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE, (double)16.0F).add(Attributes.ATTACK_KNOCKBACK).add(Attributes.MAX_HEALTH, (double)4.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.level().isClientSide) {
            if (!this.isDeadOrDying()) {
                ServerComputer computer = createOrUpkeepComputer();
                computer.keepAlive();

                if(tickCount>5 && shouldMakeBoot) {
                    DroneAPI.initDrive(computer);
                    shouldMakeBoot=false;
                }
            }
        }
        if(engineOn()) {
            setDeltaMovement(getForward().multiply(0.1,0.1,0.1));
        }

        if (targetPos != null) {
            Vec3 targetCenter = new Vec3(targetPos.x+0.5,targetPos.y+0.5,targetPos.z+0.5);
            Vec3 dir = targetCenter.subtract(position()).normalize();
            double speed = 0.1;
            setDeltaMovement(getDeltaMovement().scale(0.9).add(dir.scale(speed)));
            this.lookAt(EntityAnchorArgument.Anchor.EYES,targetCenter);

            if (position().closerThan(targetCenter, 0.5)) {
                targetPos = null;
                setDeltaMovement(Vec3.ZERO);

                Object[] eventarg = new Object[1];
                eventarg[0] = "nanodrone_movement";
                ServerComputer computer = getServerComputer(this.getServer(),getComputerUUID());
                if (computer != null) {
                    computer.queueEvent("task_complete",eventarg);
                }
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.put("extra",entityData.get(EXTRA));

        ListTag itemsList = new ListTag();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (!inventory.getItem(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putByte("Slot", (byte) i);
                inventory.getItem(i).save(itemTag);
                itemsList.add(itemTag);
            }
        }
        compoundTag.put("Inventory", itemsList);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        CompoundTag a = new CompoundTag();
        entityData.define(EXTRA,a.copy());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        entityData.set(EXTRA,compoundTag.getCompound("extra"));


        ListTag itemsList = compoundTag.getList("Inventory", 10); // 10 = CompoundTag
        for (int i = 0; i < itemsList.size(); i++) {
            CompoundTag itemTag = itemsList.getCompound(i);
            int slot = itemTag.getByte("Slot");
            if (slot >= 0 && slot < inventory.getContainerSize()) {
                inventory.setItem(slot, ItemStack.of(itemTag));
            }
        }
    }

    public void setComputerID(int computerID) {
        CompoundTag tag = entityData.get(EXTRA);
        tag.putInt("computerID",computerID);
        entityData.set(EXTRA,tag);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        if (!level().isClientSide) {
            if (held.isEmpty()) {
                ServerComputer computer = getServerComputer(player.getServer(), this.getComputerUUID());
                if (computer == null) {return InteractionResult.FAIL;}
                PlatformHelper.get().openMenu(player, player.getItemInHand(hand).getHoverName(), (id, inventory, entity) -> new ComputerMenuWithoutInventory((MenuType) ModRegistry.Menus.COMPUTER.get(), id, inventory, (p) -> true, computer), new ComputerContainerData(computer, ItemStack.EMPTY));
            }
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

    public Container getContainer(int x,int y,int z) {
        if (this.level().getBlockEntity(new BlockPos(x,y,z)) instanceof Container contain) {
            return contain;
        }
        return null;
    }


    public MethodResult moveItemTo(Container from, Container to, int fromSlot, int limit, int toSlot) {
        ItemStack fromItem = from.getItem(Math.max(0,fromSlot - 1));
        ItemStack toItem = to.getItem(Math.max(0,toSlot - 1));

        if (fromItem.isEmpty()) {
            return MethodResult.of(false, "From slot is empty!");
        }

        if (toItem.isEmpty()) {
            if (fromItem.getCount() < limit) {
                return MethodResult.of(false, "From slot has too few items!");
            }

            ItemStack moved = fromItem.copy();
            moved.setCount(limit);

            fromItem.shrink(limit);
            from.setItem(Math.max(0,fromSlot - 1), fromItem);
            to.setItem(Math.max(0,toSlot - 1), moved);

            return MethodResult.of(true, limit);
        }

        if (toItem.getItem() != fromItem.getItem()) {
            return MethodResult.of(false, "To slot doesn't contain the same item!");
        }

        if (!Objects.equals(toItem.getTag(), fromItem.getTag())) {
            return MethodResult.of(false, "Both items' NBT do not match.");
        }

        int transferable = Math.min(limit, fromItem.getCount());
        int space = toItem.getMaxStackSize() - toItem.getCount();
        int movedAmount = Math.min(transferable, space);

        fromItem.shrink(movedAmount);
        toItem.grow(movedAmount);

        from.setItem(Math.max(0,fromSlot - 1), fromItem);
        to.setItem(Math.max(0,toSlot - 1), toItem);

        return MethodResult.of(true, movedAmount);
    }

    public MethodResult pullItemsFromContainer(int x, int y, int z, int slot, int count) {
        Container container = getContainer(x,y,z);
        if (container != null) {
            ItemStack item = container.getItem(Math.max(0,slot-1));
            if (!item.isEmpty()) {
                return moveItemTo(container,getInventory(),slot,count,1);
            } else {
                return MethodResult.of(false,"Item Slot is empty!");
            }
        } else {
            return MethodResult.of(false,"Block isnt a container!");
        }
    }

    public MethodResult pushItemsToContainer(int x, int y, int z, int slot, int count) {
        Container container = getContainer(x,y,z);
        if (container != null) {
            ItemStack item = getInventory().getItem(0);
            if (!item.isEmpty()) {
                return moveItemTo(getInventory(), container, 1, count, slot);
            } else {
                return MethodResult.of(false,"Item Slot is empty!");
            }
        } else {
            return MethodResult.of(false,"Block isnt a container!");
        }
    }

    public void setComputerUUID(UUID computerUUID) {
        CompoundTag tag = entityData.get(EXTRA);
        tag.putUUID("computerUUID",computerUUID);
        entityData.set(EXTRA,tag);
    }

    public void removeUpgrades() {
        CompoundTag oldtag = entityData.get(EXTRA);
        CompoundTag tag = oldtag.copy();


        if(tag.contains("upgrades"))
        {
            ListTag list = (ListTag) tag.get("upgrades");
            for (int i = 0; i < list.size(); i++) {
                String v = list.getString(i);
                ItemStack stack = new ItemStack(CCDrones.UPGRADE_MAP.get(v));
                ItemEntity entity = new ItemEntity(level(),getX(),getY(),getZ(),stack);
                level().addFreshEntity(entity);
            }

            tag.remove("upgrades");
        }

        entityData.set(EXTRA,tag);
    }

    public ListTag getUpgrades()
    {
        CompoundTag tag = entityData.get(EXTRA);
        if(tag.contains("upgrades"))
        {
            return (ListTag) tag.get("upgrades");
        }
        return null;
    }

    public void setEngineOn(boolean on)
    {
        CompoundTag tag = entityData.get(EXTRA);
        tag.putBoolean("engineOn",on);
        entityData.set(EXTRA,tag);
    }
    public boolean engineOn()
    {
        CompoundTag tag = entityData.get(EXTRA);
        if(tag.contains("engineOn"))
        {
            return tag.getBoolean("engineOn");
        }
        return false;
    }

    public int getComputerID() {
        CompoundTag tag = entityData.get(EXTRA);
        if(tag.contains("computerID"))
        {
            return tag.getInt("computerID");
        }
        return -1;
    }
    public UUID getComputerUUID() {
        CompoundTag tag = entityData.get(EXTRA);
        if(tag.contains("computerUUID"))
        {
            return tag.getUUID("computerUUID");
        }
        return null;
    }
    public void setAllData(CompoundTag tag) {
        entityData.set(EXTRA,tag);
    }
    public CompoundTag getAllData() {
        return entityData.get(EXTRA);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new FlyingPathNavigation(this, level);
    }


    public void setTargetPos(Vec3 pos) {
        this.targetPos = pos;
    }

    public MethodResult moveTo(int x, int y, int z) {
        this.getNavigation().moveTo(x,y,z,1);
        return MethodResult.of(true,"Moving To "+x+" "+y+" "+z);
    }

    private ServerComputer createOrUpkeepComputer() {
        ServerContext context = ServerContext.get(this.getServer());
        ServerComputer computer = context.registry().get(getComputerUUID());
        if (computer == null) {
            if (getComputerID() < 0) {
                setComputerID(ComputerCraftAPI.createUniqueNumberedSaveDir(this.getServer(), IDAssigner.COMPUTER));
            }

            computer = new ServerComputer(
                    (ServerLevel) this.level(), this.getOnPos(), ServerComputer.properties(getComputerID(), ComputerFamily.ADVANCED)
                    .terminalSize(20,10)
                    .addComponent(CCDrones.NANODRONEAPI,this)
            );

            //System.out.println("Computer ID: "+computer.getID());
            setComputerUUID(computer.register());

            computer.turnOn();
        }
        return computer;
    }

    @Override
    protected void dropAllDeathLoot(DamageSource damageSource) {
        super.dropAllDeathLoot(damageSource);

        ServerComputer computer = getServerComputer(this.getServer(),getComputerUUID());
        if (computer != null) {
            computer.close();
        }

        CompoundTag tag = entityData.get(EXTRA);
        tag.remove("computerUUID");
        entityData.set(EXTRA,tag);

        ItemStack stack = new ItemStack(CCDrones.NANODRONE_ITEM);
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("extra",getAllData()); //TODO: remove unness stuff from extra
        compoundTag.remove("computerUUID");

        stack.setTag(compoundTag);
        ItemEntity entity = new ItemEntity(level(),getX(),getY(),getZ(),stack);//TODO: drop item inside inventory if not empty
        level().addFreshEntity(entity);

        ItemStack invItem = getInventory().getItem(0);

        if (!invItem.isEmpty()) {
            ItemEntity drop = new ItemEntity(
                    this.level(),
                    getX(),
                    getY(),
                    getZ(),
                    invItem
            );

            level().addFreshEntity(drop);
        }

    }

    @Override
    public double getPassengersRidingOffset() {
        return -2;
    }
}
