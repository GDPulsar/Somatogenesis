package com.pulsar.somatogenesis.block;

import com.pulsar.somatogenesis.recipe.DigesterRecipe;
import com.pulsar.somatogenesis.registry.SomatogenesisBlocks;
import com.pulsar.somatogenesis.registry.SomatogenesisRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

public class CruncherBlockEntity extends DigesterBlockEntity {
    public CruncherBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SomatogenesisBlocks.CRUNCHER_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public RecipeType<? extends DigesterRecipe> getRecipeType() {
        return SomatogenesisRecipes.CRUNCHER_TYPE.get();
    }
}
