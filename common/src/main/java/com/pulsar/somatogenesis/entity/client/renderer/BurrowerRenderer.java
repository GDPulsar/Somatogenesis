package com.pulsar.somatogenesis.entity.client.renderer;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.entity.BurrowerEntity;
import com.pulsar.somatogenesis.entity.client.model.BurrowerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BurrowerRenderer extends MobRenderer<BurrowerEntity, BurrowerModel> {
    public BurrowerRenderer(EntityRendererProvider.Context context) {
        super(context, new BurrowerModel(context.bakeLayer(BurrowerModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(BurrowerEntity entity) {
        return Somatogenesis.reloc("textures/entity/burrower.png");
    }
}
