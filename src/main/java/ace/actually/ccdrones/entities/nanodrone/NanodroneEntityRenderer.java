package ace.actually.ccdrones.entities.nanodrone;

import ace.actually.ccdrones.ClientInit;
import ace.actually.ccdrones.entities.DroneEntity;
import ace.actually.ccdrones.entities.DroneEntityModelNew;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class NanodroneEntityRenderer extends MobRenderer<NanodroneEntity, NanodroneEntityModel> {


    public NanodroneEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new NanodroneEntityModel(context.bakeLayer(ClientInit.MODEL_NANODRONE_LAYER)), 0.5f);
    }

    @Override
    public void render(NanodroneEntity mob, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        super.render(mob, f, g, poseStack, multiBufferSource, i);
    }

    @Override
    public ResourceLocation getTextureLocation(NanodroneEntity entity) {
        return new ResourceLocation("ccdrones","textures/entity/bee.png");
    }
}
