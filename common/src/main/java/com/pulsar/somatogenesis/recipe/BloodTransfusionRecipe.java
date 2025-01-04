package com.pulsar.somatogenesis.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.registry.SomatogenesisEntities;
import com.pulsar.somatogenesis.registry.SomatogenesisRecipes;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BloodTransfusionRecipe implements Recipe<Container> {
    public static DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Somatogenesis.MOD_ID, Registries.ATTRIBUTE);

    public final ResourceLocation id;
    public final EntityType<?> entityType;
    public final HashMap<Attribute, AttributeModifier> modifiers;
    public final List<BloodTransfusionEffect> effects;
    public final double problemThreshold;

    public BloodTransfusionRecipe(ResourceLocation id, EntityType<?> entityType, HashMap<Attribute, AttributeModifier> modifiers, List<BloodTransfusionEffect> effects, double problemThreshold) {
        this.id = id;
        this.entityType = entityType;
        this.modifiers = modifiers;
        this.effects = effects;
        this.problemThreshold = problemThreshold;
    }

    public boolean matches(EntityType<?> type) {
        return this.entityType == type;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return this.id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SomatogenesisRecipes.BLOOD_TRANSFUSION_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return SomatogenesisRecipes.BLOOD_TRANSFUSION_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BloodTransfusionRecipe> {
        @Override
        public @NotNull BloodTransfusionRecipe fromJson(ResourceLocation id, JsonObject json) {
            EntityType<?> entityType = SomatogenesisEntities.ENTITY_TYPES.getRegistrar().get(ResourceLocation.tryParse(GsonHelper.getAsString(json, "entityType")));
            HashMap<Attribute, AttributeModifier> modifiers = new HashMap<>();
            List<BloodTransfusionEffect> effects = new ArrayList<>();
            JsonArray modifierArray = GsonHelper.getAsJsonArray(json, "modifiers", new JsonArray());
            JsonArray effectsArray = GsonHelper.getAsJsonArray(json, "effects", new JsonArray());
            for (JsonElement modifier : modifierArray) {
                Attribute attribute = ATTRIBUTES.getRegistrar().get(ResourceLocation.tryParse(GsonHelper.getAsString(modifier.getAsJsonObject(), "attribute")));
                AttributeModifier attributeModifier = new AttributeModifier(UUID.fromString(GsonHelper.getAsString(modifier.getAsJsonObject(), "uuid")), "Blood Transfusion Modifier",
                        GsonHelper.getAsDouble(modifier.getAsJsonObject(), "amount"),
                        AttributeModifier.Operation.fromValue(GsonHelper.getAsInt(modifier.getAsJsonObject(), "operation")));
                modifiers.put(attribute, attributeModifier);
            }
            for (JsonElement effect : effectsArray) {

            }
            return new BloodTransfusionRecipe(id, entityType, modifiers, effects, GsonHelper.getAsDouble(json, "problemThreshold"));
        }

        @Override
        public @NotNull BloodTransfusionRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            EntityType<?> entityType = SomatogenesisEntities.ENTITY_TYPES.getRegistrar().get(ResourceLocation.tryParse(buf.readUtf()));
            HashMap<Attribute, AttributeModifier> modifiers = new HashMap<>();
            int modifierCount = buf.readInt();
            for (int i = 0; i < modifierCount; i++) {
                Attribute attribute = ATTRIBUTES.getRegistrar().get(ResourceLocation.tryParse(buf.readUtf()));
                AttributeModifier modifier = AttributeModifier.load(buf.readNbt());
                modifiers.put(attribute, modifier);
            }
            return new BloodTransfusionRecipe(id, entityType, modifiers, List.of(), buf.readDouble());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, BloodTransfusionRecipe recipe) {
            buf.writeUtf(SomatogenesisEntities.ENTITY_TYPES.getRegistrar().getId(recipe.entityType).toString());
            buf.writeInt(recipe.modifiers.size());
            for (Map.Entry<Attribute, AttributeModifier> modifier : recipe.modifiers.entrySet()) {
                buf.writeUtf(ATTRIBUTES.getRegistrar().getId(modifier.getKey()).toString());
                buf.writeNbt(modifier.getValue().save());
            }
            buf.writeDouble(recipe.problemThreshold);
        }
    }

    public static class Type implements RecipeType<BloodTransfusionRecipe> {}
}
