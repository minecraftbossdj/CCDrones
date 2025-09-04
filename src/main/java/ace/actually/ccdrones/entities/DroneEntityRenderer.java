package ace.actually.ccdrones.entities;

import ace.actually.ccdrones.ClientInit;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DroneEntityRenderer extends MobRenderer<DroneEntity,DroneEntityModelNew> {
    public DroneEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DroneEntityModelNew(context.bakeLayer(ClientInit.MODEL_DRONE_LAYER)), 0.5f);
    }

    @Override
    public void render(DroneEntity mob, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        super.render(mob, f, g, poseStack, multiBufferSource, i);



        if(mob.isCarryingBlock())
        {
            BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(),mob.getEntityData().get(DroneEntity.EXTRA).getCompound("carryingState"));

            poseStack.pushPose();
            poseStack.translate(-0.5,-1,-0.5);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.CHAIN.defaultBlockState(),poseStack,multiBufferSource,i, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(-0.5,-2,-0.5);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state,poseStack,multiBufferSource,i, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

        this.model.modem_upgrade.visible = mob.hasUpgrade("modem");
        this.model.survey_upgrade.visible = mob.hasUpgrade("survey");

        if(!mob.getPassengers().isEmpty())
        {
            poseStack.pushPose();
            poseStack.translate(-0.5,-1,-0.5);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.CHAIN.defaultBlockState(),poseStack,multiBufferSource,i, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }


    }

    @Override
    public ResourceLocation getTextureLocation(DroneEntity entity) {
        return new ResourceLocation("ccdrones","textures/entity/drone.png");
    }
}
