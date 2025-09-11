package ace.actually.ccdrones;

import ace.actually.ccdrones.entities.DroneEntityModel;
import ace.actually.ccdrones.entities.DroneEntityModelNew;
import ace.actually.ccdrones.entities.DroneEntityRenderer;
import ace.actually.ccdrones.entities.nanodrone.NanodroneEntityModel;
import ace.actually.ccdrones.entities.nanodrone.NanodroneEntityRenderer;
import ace.actually.ccdrones.menu.DroneMenu;
import ace.actually.ccdrones.menu.DroneScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ClientInit implements ClientModInitializer {
    public static final ModelLayerLocation MODEL_DRONE_LAYER = new ModelLayerLocation(new ResourceLocation("ccdrones", "drone"), "main");
    public static final ModelLayerLocation MODEL_NANODRONE_LAYER = new ModelLayerLocation(new ResourceLocation("ccdrones", "nanodrone"), "main");
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(CCDrones.DRONE, DroneEntityRenderer::new);
        EntityRendererRegistry.INSTANCE.register(CCDrones.NANODRONE, NanodroneEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_DRONE_LAYER, DroneEntityModelNew::getTexturedData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_NANODRONE_LAYER, NanodroneEntityModel::getTexturedData);
        MenuScreens.register(CCDrones.DRONE_MENU.get(), DroneScreen::new);
    }
}
