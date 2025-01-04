package com.pulsar.somatogenesis.entity.client.renderer;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.entity.BasicCreatureEntity;
import com.pulsar.somatogenesis.entity.BurrowerEntity;
import com.pulsar.somatogenesis.entity.client.model.BasicCreatureModel;
import com.pulsar.somatogenesis.entity.client.model.BurrowerModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BasicCreatureRenderer extends MobRenderer<BasicCreatureEntity, BasicCreatureModel> {
    public BasicCreatureRenderer(EntityRendererProvider.Context context) {
        super(context, new BasicCreatureModel(context.bakeLayer(BasicCreatureModel.LAYER_LOCATION)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(BasicCreatureEntity entity) {
        return Somatogenesis.reloc("textures/block/flesh_block.png");
    }
}
