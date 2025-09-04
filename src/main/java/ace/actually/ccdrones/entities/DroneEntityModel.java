package ace.actually.ccdrones.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class DroneEntityModel extends EntityModel<DroneEntity> {
    public final ModelPart bb_main;

    public DroneEntityModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    public static LayerDefinition getTexturedData() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(8, 16).addBox(7.0F, -2.0F, 4.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(12, 22).addBox(4.5F, -2.0F, -6.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 9).addBox(3.5F, -1.0F, -7.5F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 16).addBox(7.0F, -2.0F, -8.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(8, 14).addBox(3.0F, -2.0F, -8.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(16, 20).addBox(3.0F, -2.0F, -8.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(20, 10).addBox(3.0F, -2.0F, -4.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(20, 8).addBox(-7.0F, -2.0F, -4.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(20, 6).addBox(-7.0F, -2.0F, -8.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 14).addBox(-7.0F, -2.0F, -8.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(8, 12).addBox(-3.0F, -2.0F, -8.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 6).addBox(-6.5F, -1.0F, -7.5F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(8, 22).addBox(-5.5F, -2.0F, -6.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(4, 22).addBox(-5.5F, -2.0F, 5.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 3).addBox(-6.5F, -1.0F, 4.5F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 12).addBox(-3.0F, -2.0F, 4.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(8, 10).addBox(-7.0F, -2.0F, 4.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(20, 4).addBox(-7.0F, -2.0F, 4.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(20, 2).addBox(-7.0F, -2.0F, 8.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 22).addBox(4.5F, -2.0F, 5.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(3.5F, -1.0F, 4.5F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 10).addBox(3.0F, -2.0F, 4.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(20, 0).addBox(3.0F, -2.0F, 4.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(16, 18).addBox(3.0F, -2.0F, 8.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, -2.0F, -6.0F, 4.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition connector_r1 = bb_main.addOrReplaceChild("connector_r1", CubeListBuilder.create().texOffs(15, 14).addBox(-4.5F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 0.0F, 6.0F, 0.0F, -0.3927F, 0.0F));

        PartDefinition connector_r2 = bb_main.addOrReplaceChild("connector_r2", CubeListBuilder.create().texOffs(15, 15).addBox(-0.5F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 0.0F, 6.0F, 0.0F, 0.3927F, 0.0F));

        PartDefinition connector_r3 = bb_main.addOrReplaceChild("connector_r3", CubeListBuilder.create().texOffs(15, 16).addBox(-0.5F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 0.0F, -6.0F, 0.0F, -0.3927F, 0.0F));

        PartDefinition connector_r4 = bb_main.addOrReplaceChild("connector_r4", CubeListBuilder.create().texOffs(15, 17).addBox(-4.5F, 0.0F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 0.0F, -6.0F, 0.0F, 0.3927F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }



    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(DroneEntity entity, float f, float g, float h, float i, float j) {

    }
}
