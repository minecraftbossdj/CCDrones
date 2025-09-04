package ace.actually.ccdrones.entities;

import ace.actually.ccdrones.items.DroneUpgradeItem;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.client.ComputerCraftAPIClient;
import dan200.computercraft.api.client.FabricComputerCraftAPIClient;
import dan200.computercraft.api.detail.BlockReference;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.filesystem.MountConstants;
import dan200.computercraft.api.filesystem.WritableMount;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.core.computer.mainthread.MainThread;
import dan200.computercraft.core.filesystem.FileSystemException;
import dan200.computercraft.core.filesystem.FileSystemWrapper;
import dan200.computercraft.core.util.PeripheralHelpers;
import dan200.computercraft.shared.computer.blocks.ComputerBlock;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.peripheral.modem.ModemPeripheral;
import dan200.computercraft.shared.turtle.upgrades.CraftingTablePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DroneAPI implements ILuaAPI {
    DroneEntity drone;
    public DroneAPI(DroneEntity entity)
    {
        this.drone=entity;
    }

    @Override
    public String[] getNames() {
        return new String[] {"drone"};
    }

    @LuaFunction
    public final MethodResult engineOn(boolean on)
    {
        drone.setEngineOn(on);
        if(!on)
        {
            drone.setDeltaMovement(Vec3.ZERO);
        }
        if (on) {
            return MethodResult.of(true, "Turned On Engine!");
        } else {
            return MethodResult.of(true, "Turned Off Engine"); //true since it suceeded turning off
        }
    }
    @LuaFunction
    public final MethodResult hoverOn(boolean on)
    {
        drone.setNoGravity(on);
        if (on) {return MethodResult.of(true, "Hover on!");} else {return MethodResult.of(true, "Hover Off!");}
    }
    @LuaFunction
    public final void right(int deg) {
        drone.turn(deg,0);
    }
    @LuaFunction
    public final void left(int deg) {right(-deg);}

    @LuaFunction
    public final boolean isColliding() {return drone.horizontalCollision;}

    @LuaFunction
    public final void up(int amount)
    {
        drone.addDeltaMovement(Vec3.ZERO.add(0,amount/10D,0));
    }
    @LuaFunction
    public final void down(int amount) {up(-amount);}
    @LuaFunction
    public final MethodResult lookForward()
    {
        ClipContext context = new ClipContext(drone.getOnPos().getCenter(),drone.getOnPos().getCenter().add(drone.getForward().multiply(3,3,3)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY,drone);
        BlockHitResult result = drone.level().clip(context);

        //System.out.println(result.getBlockPos()+" "+drone.level().getBlockState(result.getBlockPos()));
        return MethodResult.of(VanillaDetailRegistries.BLOCK_IN_WORLD.getDetails(new BlockReference(drone.level(),result.getBlockPos())));
    }
    @LuaFunction
    public final float rotation() {return drone.yRotO;}
    @LuaFunction
    public final MethodResult lookBack()
    {
        ClipContext context = new ClipContext(drone.getOnPos().getCenter(),drone.getOnPos().getCenter().add(drone.getForward().multiply(-3,-3,-3)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY,drone);
        BlockHitResult result = drone.level().clip(context);

        return MethodResult.of(VanillaDetailRegistries.BLOCK_IN_WORLD.getDetails(new BlockReference(drone.level(),result.getBlockPos())));
    }

    @LuaFunction
    public final MethodResult breakForward()
    {
        if(drone.hasUpgrade("mine")) {
            ClipContext context = new ClipContext(drone.getOnPos().getCenter(),drone.getOnPos().getCenter().add(drone.getForward().multiply(3,3,3)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY,drone);
            BlockHitResult result = drone.level().clip(context);

            drone.level().destroyBlock(result.getBlockPos(),true,drone);
            return MethodResult.of(true,"Broke Block!");
        } else {
            return MethodResult.of(false, "Mining Upgrade Not Installed!");
        }
    }

    @LuaFunction(mainThread = true)
    public final MethodResult pickupBlock()
    {
        if(drone.hasUpgrade("carry")) {
            ClipContext context = new ClipContext(drone.getOnPos().getCenter(),drone.getOnPos().getCenter().add(0,-2,0), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY,drone);
            BlockHitResult result = drone.level().clip(context);

            drone.setCarrying(result.getBlockPos());
            return MethodResult.of(true, "Picked Up Block!");
        } else {
            return MethodResult.of(false, "Carry Upgrade Not Installed!");
        }

    }
    @LuaFunction(mainThread = true)
    public final void dropBlock()
    {
        ClipContext context = new ClipContext(drone.getOnPos().getCenter(),drone.getOnPos().getCenter().add(0,-2,0), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY,drone);
        BlockHitResult result = drone.level().clip(context);

        drone.dropCarrying(result.getBlockPos().above());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult pickUpEntity()
    {

        if(drone.hasUpgrade("carry"))
        {
            List<Entity> targets = drone.level().getEntitiesOfClass(Entity.class,new AABB(drone.getOnPos().offset(-2,-2,-2),drone.getOnPos().offset(2,0,2)));
            if(!targets.isEmpty()) {
                targets.get(drone.getRandom().nextInt(targets.size())).startRiding(drone);
                return MethodResult.of(true, "Picked up Entity!");
            } else {
                return MethodResult.of(false, "No Entities Nearby!");
            }

        } else {
            return MethodResult.of(false, "Carry Upgrade Not Installed!");
        }

    }


    @LuaFunction(mainThread = true)
    public final void findUpgrades()
    {

        List<ItemEntity> targets = drone.level().getEntitiesOfClass(ItemEntity.class,new AABB(drone.getOnPos().offset(-2,-2,-2),drone.getOnPos().offset(2,2,2)));
        if(!targets.isEmpty())
        {
           for(ItemEntity item: targets)
           {
               if(item.getItem().getItem() instanceof DroneUpgradeItem droneUpgradeItem)
               {
                   drone.addUpgrade(droneUpgradeItem.getUpgrade());
               }
           }
        }

    }

    @LuaFunction(mainThread = true)
    public final MethodResult dropEntity()
    {
        if(!drone.getPassengers().isEmpty())
        {
            drone.ejectPassengers();
            return MethodResult.of(true,"Ejected Passenger!");
        } else {
            return MethodResult.of(false, "No Passengers!");
        }
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getPos() {
        if (drone.hasUpgrade("modem")) {
            Map<String, Object> info = new HashMap<>();
            info.put("x", drone.position().x);
            info.put("y", drone.position().y);
            info.put("z", drone.position().z);
            return MethodResult.of(true,info);
        }
        return MethodResult.of(false,"Modem Upgrade Not Installed!");
    }


    public static void initDrive(ServerComputer computer)
    {

        try {
            WritableMount mount = computer.createRootMount();
            if ((mount == null)) {return;}

            if(!mount.exists("startup/go.lua")) {
                if (!mount.exists("startup/")) {
                    mount.makeDirectory("startup");
                }
                SeekableByteChannel file = computer.createRootMount().openFile("startup/go.lua",MountConstants.WRITE_OPTIONS);
                file.write(ByteBuffer.wrap("--default startup program".getBytes(StandardCharsets.UTF_8)));
                file.close();
                computer.reboot();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
