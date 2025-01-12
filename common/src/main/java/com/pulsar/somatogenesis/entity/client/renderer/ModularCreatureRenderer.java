package com.pulsar.somatogenesis.entity.client.renderer;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.entity.BasicCreatureEntity;
import com.pulsar.somatogenesis.entity.client.model.BasicCreatureModel;
import com.pulsar.somatogenesis.entity.client.model.ModularCreatureModel;
import com.pulsar.somatogenesis.entity.creatures.ModularCreatureEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ModularCreatureRenderer extends MobRenderer<ModularCreatureEntity, ModularCreatureModel> {
    public ModularCreatureRenderer(EntityRendererProvider.Context context) {
        super(context, new ModularCreatureModel(context.bakeLayer(ModularCreatureModel.LAYER_LOCATION)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(ModularCreatureEntity entity) {
        return Somatogenesis.reloc("textures/block/flesh_block.png");
    }
}
