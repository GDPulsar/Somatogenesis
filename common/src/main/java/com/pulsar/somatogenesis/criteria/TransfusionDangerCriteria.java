package com.pulsar.somatogenesis.criteria;

import com.google.gson.JsonObject;
import com.pulsar.somatogenesis.Somatogenesis;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

public class TransfusionDangerCriteria extends SimpleCriterionTrigger<TransfusionDangerCriteria.TriggerInstance> {
    public static final ResourceLocation ID = Somatogenesis.reloc("transfusion_danger");

    @Override
    protected TriggerInstance createInstance(JsonObject jsonObject, ContextAwarePredicate contextAwarePredicate, DeserializationContext deserializationContext) {
        return new TriggerInstance(contextAwarePredicate, GsonHelper.getAsDouble(jsonObject, "dangerPercent"));
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayer player, double dangerPercent) {
        this.trigger(player, trigger -> trigger.dangerPercent == dangerPercent);
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        public final double dangerPercent;

        public TriggerInstance(ContextAwarePredicate contextAwarePredicate, double dangerPercent) {
            super(TransfusionDangerCriteria.ID, contextAwarePredicate);
            this.dangerPercent = dangerPercent;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext serializationContext) {
            JsonObject json = super.serializeToJson(serializationContext);
            json.addProperty("dangerPercent", dangerPercent);
            return json;
        }
    }
}