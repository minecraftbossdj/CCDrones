package ace.actually.ccdrones;

import ace.actually.ccdrones.entities.DroneEntityModel;
import ace.actually.ccdrones.entities.DroneEntityModelNew;
import ace.actually.ccdrones.entities.DroneEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ClientInit implements ClientModInitializer {
    public static final ModelLayerLocation MODEL_DRONE_LAYER = new ModelLayerLocation(new ResourceLocation("ccdrones", "drone"), "main");
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(CCDrones.DRONE, DroneEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_DRONE_LAYER, DroneEntityModelNew::getTexturedData);
    }
}
