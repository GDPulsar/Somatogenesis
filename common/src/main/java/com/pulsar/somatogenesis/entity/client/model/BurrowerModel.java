package com.pulsar.somatogenesis.entity.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pulsar.somatogenesis.entity.BurrowerEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class BurrowerModel extends EntityModel<BurrowerEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "burrower"), "main");
	private final ModelPart creature;
	private final ModelPart bodyBack;
	private final ModelPart armLeft;
	private final ModelPart armLeftLower;
	private final ModelPart armRight;
	private final ModelPart armRightLower;

	public BurrowerModel(ModelPart root) {
		this.creature = root.getChild("creature");
		this.bodyBack = this.creature.getChild("bodyBack");
		this.armLeft = this.creature.getChild("armLeft");
		this.armLeftLower = this.armLeft.getChild("armLeftLower");
		this.armRight = this.creature.getChild("armRight");
		this.armRightLower = this.armRight.getChild("armRightLower");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition creature = partdefinition.addOrReplaceChild("creature", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition cube_r1 = creature.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 12).addBox(-3.0F, 0.0F, -2.0F, 7.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition bodyBack = creature.addOrReplaceChild("bodyBack", CubeListBuilder.create(), PartPose.offsetAndRotation(0.5F, 4.5F, 3.0F, -0.0436F, 0.0F, 0.0F));

		PartDefinition cube_r2 = bodyBack.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, 0.0F, 3.0F, 6.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -3.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition armLeft = creature.addOrReplaceChild("armLeft", CubeListBuilder.create(), PartPose.offset(4.0F, 6.0F, 0.0F));

		PartDefinition cube_r3 = armLeft.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 22).addBox(-1.0F, -0.9171F, -6.5912F, 3.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -0.1695F, -0.6698F, -0.1745F, 0.0F, 0.0F));

		PartDefinition armLeftLower = armLeft.addOrReplaceChild("armLeftLower", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, -6.0F));

		PartDefinition cube_r4 = armLeftLower.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(24, 12).addBox(-1.0F, -6.9171F, -6.5912F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.1695F, 5.5802F, -0.1745F, 0.0F, 0.0F));

		PartDefinition armRight = creature.addOrReplaceChild("armRight", CubeListBuilder.create(), PartPose.offset(-1.0F, 6.0F, 0.0F));

		PartDefinition cube_r5 = armRight.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(20, 22).addBox(-1.0F, -0.9171F, -6.5912F, 3.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -0.1695F, -0.6698F, -0.1745F, 0.0F, 0.0F));

		PartDefinition armRightLower = armRight.addOrReplaceChild("armRightLower", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, -6.0F));

		PartDefinition cube_r6 = armRightLower.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(28, 0).addBox(-1.0F, -6.9171F, -6.5912F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.1695F, 5.5802F, -0.1745F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		creature.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void setupAnim(BurrowerEntity entity, float f, float g, float h, float i, float j) {

	}
}