package ace.actually.ccdrones.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class DroneEntityModelNew extends EntityModel<DroneEntity> {
    private final ModelPart topleft;
    private final ModelPart topright;
    private final ModelPart bottomright;
    private final ModelPart bottomleft;
    public final ModelPart modem_upgrade;
    private final ModelPart body;
    public final ModelPart survey_upgrade;

    public DroneEntityModelNew(ModelPart root) {
        this.topleft = root.getChild("topleft");
        this.topright = root.getChild("topright");
        this.bottomright = root.getChild("bottomright");
        this.bottomleft = root.getChild("bottomleft");
        this.modem_upgrade = root.getChild("modem_upgrade");
        this.body = root.getChild("body");
        this.survey_upgrade = root.getChild("survey_upgrade");
    }

    public static LayerDefinition getTexturedData() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition topleft = partdefinition.addOrReplaceChild("topleft", CubeListBuilder.create().texOffs(12, 22).addBox(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 20).addBox(-2.0F, -2.0F, 2.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(12, 16).mirror().addBox(2.0F, -2.0F, -2.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(12, 20).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(12, 16).addBox(-2.0F, -2.0F, -2.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 3).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 24.0F, -6.0F));

        PartDefinition connector_r1 = topleft.addOrReplaceChild("connector_r1", CubeListBuilder.create().texOffs(10, 23).addBox(-4.5F, 0.01F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.3927F, 0.0F));

        PartDefinition topright = partdefinition.addOrReplaceChild("topright", CubeListBuilder.create().texOffs(4, 22).mirror().addBox(-4.5F, -2.0F, 3.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 0).addBox(-5.5F, -1.0F, 2.5F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(12, 16).mirror().addBox(-2.0F, -2.0F, 2.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(12, 16).addBox(-6.0F, -2.0F, 2.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(12, 20).addBox(-6.0F, -2.0F, 2.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(12, 20).addBox(-6.0F, -2.0F, 6.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 24.0F, -10.0F));

        PartDefinition connector_r2 = topright.addOrReplaceChild("connector_r2", CubeListBuilder.create().texOffs(10, 23).addBox(-0.5F, 0.01F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, 4.0F, 0.0F, -0.3927F, 0.0F));

        PartDefinition bottomright = partdefinition.addOrReplaceChild("bottomright", CubeListBuilder.create().texOffs(0, 22).addBox(-4.5F, -2.0F, 15.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 6).addBox(-5.5F, -1.0F, 14.5F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(12, 16).addBox(-2.0F, -2.0F, 14.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(12, 16).addBox(-6.0F, -2.0F, 14.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(12, 20).addBox(-6.0F, -2.0F, 14.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(12, 20).addBox(-6.0F, -2.0F, 18.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 24.0F, -10.0F));

        PartDefinition connector_r3 = bottomright.addOrReplaceChild("connector_r3", CubeListBuilder.create().texOffs(10, 23).addBox(-0.5F, 0.01F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, 16.0F, 0.0F, 0.3927F, 0.0F));

        PartDefinition bottomleft = partdefinition.addOrReplaceChild("bottomleft", CubeListBuilder.create().texOffs(8, 22).addBox(5.5F, -2.0F, 15.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 9).addBox(4.5F, -1.0F, 14.5F, 3.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(12, 16).mirror().addBox(8.0F, -2.0F, 14.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(12, 20).addBox(4.0F, -2.0F, 14.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(12, 20).addBox(4.0F, -2.0F, 14.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(12, 20).addBox(4.0F, -2.0F, 18.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 24.0F, -10.0F));

        PartDefinition connector_r4 = bottomleft.addOrReplaceChild("connector_r4", CubeListBuilder.create().texOffs(10, 23).addBox(-4.5F, 0.01F, -0.5F, 5.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 0.0F, 16.0F, 0.0F, -0.3927F, 0.0F));

        PartDefinition modem_upgrade = partdefinition.addOrReplaceChild("modem_upgrade", CubeListBuilder.create().texOffs(23, 28).addBox(-1.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(27, 29).addBox(-1.25F, 0.0F, 0.0F, 1.5F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 22.0F, 6.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, -2.0F, 2.0F, 4.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 24.0F, -8.0F));

        PartDefinition survey_upgrade = partdefinition.addOrReplaceChild("survey_upgrade", CubeListBuilder.create().texOffs(21, 30).addBox(3.75F, -1.75F, -0.01F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(20, 29).addBox(5.25F, -1.75F, -0.01F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(16, 21).addBox(4.0F, -1.5F, -0.03F, 0.5F, 0.5F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(16, 21).addBox(5.5F, -1.5F, -0.03F, 0.5F, 0.5F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 24.0F, -6.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        topleft.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        topright.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        bottomright.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        bottomleft.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        modem_upgrade.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        survey_upgrade.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

	@Override
    public void setupAnim(DroneEntity entity, float f, float g, float h, float i, float j) {

    }
}