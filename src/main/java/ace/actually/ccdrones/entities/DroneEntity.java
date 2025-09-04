package ace.actually.ccdrones.entities;

import ace.actually.ccdrones.CCDrones;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.core.computer.Computer;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.config.Config;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.util.IDAssigner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.stream.Stream;

public class DroneEntity extends Mob {

    private boolean shouldMakeBoot = false;

    public static final EntityDataAccessor<CompoundTag> EXTRA = SynchedEntityData.defineId(DroneEntity.class, EntityDataSerializers.COMPOUND_TAG);
    public DroneEntity(EntityType<DroneEntity> e,Level level) {
        super(e, level);

    }

    @Override
    public void tick() {
        super.tick();
        if(!this.level().isClientSide)
        {

            ServerComputer computer = createOrUpkeepComputer();
            computer.keepAlive();
            if(tickCount>5 && shouldMakeBoot)
            {
                DroneAPI.initDrive(computer);
                shouldMakeBoot=false;
            }


        }
        if(engineOn())
        {
            setDeltaMovement(getForward().multiply(0.1,0.1,0.1));

        }



    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.put("extra",entityData.get(EXTRA));
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
    }

    public void setComputerID(int computerID) {
        CompoundTag tag = entityData.get(EXTRA);
        tag.putInt("computerID",computerID);
        entityData.set(EXTRA,tag);

    }

    public void setCarrying(BlockPos pos) {
        CompoundTag tag = entityData.get(EXTRA);

        System.out.println("picking up!");
        BlockState state = level().getBlockState(pos);
        BlockEntity entity = level().getBlockEntity(pos);

        CompoundTag stateTag = NbtUtils.writeBlockState(state);
        tag.put("carryingState",stateTag);

        if(entity!=null)
        {
            CompoundTag entityTag = entity.saveWithFullMetadata();
            tag.put("carryingEntity",entityTag);
            Clearable.tryClear(entity);
        }


        entityData.set(EXTRA,tag,true);
        level().setBlock(pos,Blocks.AIR.defaultBlockState(),2);
    }

    public void dropCarrying(BlockPos pos)
    {
        CompoundTag tag = entityData.get(EXTRA);
        if(tag.contains("carryingState"))
        {
            System.out.println("dropping!");
            BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(),tag.getCompound("carryingState"));
            level().setBlock(pos,state,2);
            if(tag.contains("carryingEntity"))
            {
                BlockEntity entity = BlockEntity.loadStatic(pos,state,tag.getCompound("carryingEntity"));
                level().setBlockEntity(entity);
                tag.remove("carryingEntity");
            }
            tag.remove("carryingState");
            entityData.set(EXTRA,tag,true);
        }



    }

    public boolean isCarryingBlock()
    {
        CompoundTag tag = entityData.get(EXTRA);
        return tag.contains("carryingState");
    }

    public void setComputerUUID(UUID computerUUID) {
        CompoundTag tag = entityData.get(EXTRA);
        tag.putUUID("computerUUID",computerUUID);
        entityData.set(EXTRA,tag);
    }


    public void addUpgrade(String upgrade) {
        CompoundTag oldtag = entityData.get(EXTRA);
        CompoundTag tag = oldtag.copy();

        ListTag list;
        if(tag.contains("upgrades"))
        {
            list = (ListTag) tag.get("upgrades");
            list.add(StringTag.valueOf(upgrade));
        }
        else
        {
            list = new ListTag();
            list.add(StringTag.valueOf(upgrade));
        }
        tag.put("upgrades",list);

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
    public boolean hasUpgrade(String upgrade)
    {
        CompoundTag tag = entityData.get(EXTRA);
        if(tag.contains("upgrades"))
        {
            ListTag list = (ListTag) tag.get("upgrades");

            for (int i = 0; i < list.size(); i++) {
                if(list.getString(i).equals(upgrade))
                {
                    return true;
                }
            }

        }
        return false;
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

    public int getComputerID()
    {
        CompoundTag tag = entityData.get(EXTRA);
        if(tag.contains("computerID"))
        {
            return tag.getInt("computerID");
        }
        return -1;
    }
    public UUID getComputerUUID()
    {
        CompoundTag tag = entityData.get(EXTRA);
        if(tag.contains("computerUUID"))
        {
            return tag.getUUID("computerUUID");
        }
       return null;
    }
    public void setAllData(CompoundTag tag)
    {
        entityData.set(EXTRA,tag);
    }
    public CompoundTag getAllData()
    {
        return entityData.get(EXTRA);
    }


    private ServerComputer createOrUpkeepComputer()
    {
        ServerContext context = ServerContext.get(this.getServer());
        ServerComputer computer = context.registry().get(getComputerUUID());
        if (computer == null) {
            if (getComputerID() < 0) {
                System.out.println("defining ID");
                setComputerID(ComputerCraftAPI.createUniqueNumberedSaveDir(this.getServer(), IDAssigner.COMPUTER));
            }

            computer = new ServerComputer(
                    (ServerLevel) this.level(), this.getOnPos(), ServerComputer.properties(getComputerID(),ComputerFamily.ADVANCED)
                    .addComponent(CCDrones.DRONEAPI,this)
            );

            System.out.println("Computer ID: "+computer.getID());
            setComputerUUID(computer.register());

            shouldMakeBoot=true;

            computer.turnOn();


        }

        return computer;

    }

    @Override
    protected void dropAllDeathLoot(DamageSource damageSource) {
        super.dropAllDeathLoot(damageSource);
        ItemStack stack = new ItemStack(CCDrones.DRONE_ITEM);
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("extra",getAllData());
        stack.setTag(compoundTag);
        ItemEntity entity = new ItemEntity(level(),getX(),getY(),getZ(),stack);
        level().addFreshEntity(entity);
    }

    @Override
    public double getPassengersRidingOffset() {
        return -2;
    }
}
