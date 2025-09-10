package ace.actually.ccdrones.entities.nanodrone;

import ace.actually.ccdrones.entities.DroneEntity;
import ace.actually.ccdrones.items.DroneUpgradeItem;
import dan200.computercraft.api.detail.BlockReference;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.filesystem.MountConstants;
import dan200.computercraft.api.filesystem.WritableMount;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NanodroneAPI implements ILuaAPI {
    NanodroneEntity drone;
    public NanodroneAPI(NanodroneEntity entity)
    {
        this.drone=entity;
    }

    @Override
    public String[] getNames() {
        return new String[] {"nanodrone"};
    }

    @LuaFunction
    public final MethodResult engineOn(boolean on) {
        drone.setEngineOn(on);
        if(!on) {
            drone.setDeltaMovement(Vec3.ZERO);
        }
        if (on) {
            return MethodResult.of(true, "Turned On Engine!");
        } else {
            return MethodResult.of(true, "Turned Off Engine"); //true since it suceeded turning off
        }
    }
    @LuaFunction
    public final MethodResult hoverOn(boolean on) {
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
    public final void up(int amount) {
        drone.addDeltaMovement(Vec3.ZERO.add(0,amount/10D,0));
    }

    @LuaFunction
    public final void down(int amount) {up(-amount);}

    @LuaFunction
    public final MethodResult lookForward() {
        ClipContext context = new ClipContext(drone.getOnPos().getCenter(),drone.getOnPos().getCenter().add(drone.getForward().multiply(3,3,3)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY,drone);
        BlockHitResult result = drone.level().clip(context);

        //System.out.println(result.getBlockPos()+" "+drone.level().getBlockState(result.getBlockPos()));
        return MethodResult.of(VanillaDetailRegistries.BLOCK_IN_WORLD.getDetails(new BlockReference(drone.level(),result.getBlockPos())));
    }

    @LuaFunction
    public final float rotation() {
        return drone.yRotO;
    }

    public final BlockPos getDroneOffset(int x, int y, int z) {
        return new BlockPos(drone.getOnPos().getX() + x, drone.getOnPos().getY() + y, drone.getOnPos().getZ() + z);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult moveTo(int x, int y, int z) {
        double newPosX = drone.getOnPos().getX() + x;
        double newPosY = drone.getOnPos().getY() + y;
        double newPosZ = drone.getOnPos().getZ() + z;
        drone.setTargetPos(new Vec3(newPosX,newPosY,newPosZ));
        return MethodResult.of(true,"Moving to new pos!");
    }

    @LuaFunction(mainThread = true)
    public MethodResult pushItemsToContainer(int x, int y, int z, int slot, int count) {
        BlockPos newPos = getDroneOffset(x,y,z);
        return drone.pushItemsToContainer(newPos.getX(), newPos.getY(), newPos.getZ(), slot, count);
    }

    @LuaFunction(mainThread = true)
    public MethodResult pullItemsFromContainer(int x, int y, int z, int slot, int count) {
        BlockPos newPos = getDroneOffset(x,y,z);
        return drone.pullItemsFromContainer(newPos.getX(), newPos.getY(), newPos.getZ(), slot, count);
    }

    @LuaFunction(mainThread = true)
    public MethodResult getItemDetail() {
        var stack = drone.getInventory().getItem(0);
        if (!stack.isEmpty()) {
            return MethodResult.of(true, VanillaDetailRegistries.ITEM_STACK.getBasicDetails(stack));
        } else {
            return MethodResult.of(false, "Inventory empty!");
        }
    }

    @LuaFunction(mainThread = true)
    public MethodResult listContainer(int x, int y, int z) {
        BlockPos newPos = getDroneOffset(x,y,z);
        var inventory = drone.getContainer(newPos.getX(),newPos.getY(),newPos.getZ());
        if (inventory == null) {
            return MethodResult.of(false, "Block isn't a container!");
        }
        Map<Integer, Map<String, ?>> result = new HashMap<>();
        var size = inventory.getContainerSize();
        for (var i = 0; i < size; i++) {
            var stack = inventory.getItem(i);
            if (!stack.isEmpty()) result.put(i + 1, VanillaDetailRegistries.ITEM_STACK.getBasicDetails(stack));
        }
        return MethodResult.of(true, result);
    }

    @LuaFunction(mainThread = true)
    public MethodResult getContainerItemDetail(int x, int y, int z, int slot) {
        BlockPos newPos = getDroneOffset(x,y,z);
        System.out.println(newPos);
        Container inventory = drone.getContainer(newPos.getX(),newPos.getY(),newPos.getZ());
        if (inventory == null) {
            return MethodResult.of(false, "That Block Isn't a Inventory!");
        }
        var stack = inventory.getItem(slot - 1);
        if (!stack.isEmpty()) {
            return MethodResult.of(true, VanillaDetailRegistries.ITEM_STACK.getBasicDetails(stack));
        } else {
            return MethodResult.of(false, "Slot is empty!");
        }

    }

    @LuaFunction(mainThread = true)
    public final MethodResult getPos() {
        Map<String, Object> info = new HashMap<>();
        info.put("x", drone.position().x);
        info.put("y", drone.position().y);
        info.put("z", drone.position().z);
        return MethodResult.of(true,info);
    }

    @LuaFunction
    public final MethodResult lookBack() {
        ClipContext context = new ClipContext(drone.getOnPos().getCenter(),drone.getOnPos().getCenter().add(drone.getForward().multiply(-3,-3,-3)), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY,drone);
        BlockHitResult result = drone.level().clip(context);

        return MethodResult.of(VanillaDetailRegistries.BLOCK_IN_WORLD.getDetails(new BlockReference(drone.level(),result.getBlockPos())));
    }
}
