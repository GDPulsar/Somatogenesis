package com.pulsar.somatogenesis.teaching;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.accessor.TeachingAccessor;
import com.pulsar.somatogenesis.entity.creatures.ModularCreatureEntity;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Objects;

public class TeachingStats {
    public HashMap<Item, ItemTypeStats> itemTypeStats = new HashMap<>();
    public HashMap<Block, BlockTypeStats> blockTypeStats = new HashMap<>();
    public HashMap<EntityType<?>, EntityTypeStats> entityTypeStats = new HashMap<>();

    public static class ItemTypeStats {
        public int collectedCount = 0;
        public int droppedCount = 0;
        public int craftedCount = 0;
        public HashMap<BlockPos, ItemBlockStats> blockStats = new HashMap<>();

        public CompoundTag writeNbt(CompoundTag nbt) {
            if (collectedCount != 0) nbt.putInt("collected", collectedCount);
            if (droppedCount != 0) nbt.putInt("dropped", droppedCount);
            if (craftedCount != 0) nbt.putInt("crafted", craftedCount);
            CompoundTag blockStatsNbt = new CompoundTag();
            for (BlockPos pos : blockStats.keySet()) {
                CompoundTag statsNbt = blockStats.get(pos).writeNbt(new CompoundTag());
                if (!statsNbt.isEmpty()) blockStatsNbt.put(pos.toShortString(), statsNbt);
            }
            if (!blockStatsNbt.isEmpty()) nbt.put("BlockStats", blockStatsNbt);
            return nbt;
        }

        public void readNbt(CompoundTag nbt) {
            if (nbt.contains("collected")) collectedCount = nbt.getInt("collected");
            if (nbt.contains("dropped")) droppedCount = nbt.getInt("collected");
            if (nbt.contains("crafted")) craftedCount = nbt.getInt("collected");
            CompoundTag blockStatsNbt = new CompoundTag();
            for (String key : blockStatsNbt.getAllKeys()) {
                int x = Integer.parseInt(key.split(", ")[0]);
                int y = Integer.parseInt(key.split(", ")[1]);
                int z = Integer.parseInt(key.split(", ")[2]);
                ItemBlockStats stats = new ItemBlockStats();
                stats.readNbt(blockStatsNbt.getCompound(key));
                blockStats.put(new BlockPos(x, y, z), stats);
            }
        }

        public static class ItemBlockStats {
            public int collectedCount = 0;
            public int depositedCount = 0;

            public CompoundTag writeNbt(CompoundTag nbt) {
                if (collectedCount != 0) nbt.putInt("collected", collectedCount);
                if (depositedCount != 0) nbt.putInt("deposited", depositedCount);
                return nbt;
            }

            public void readNbt(CompoundTag nbt) {
                if (nbt.contains("collected")) collectedCount = nbt.getInt("collected");
                if (nbt.contains("deposited")) depositedCount = nbt.getInt("deposited");
            }
        }
    }

    public static class BlockTypeStats {
        public int minedCount = 0;
        public HashMap<Item, Integer> toolMinedMap = new HashMap<>();

        public CompoundTag writeNbt(CompoundTag nbt) {
            if (minedCount != 0) nbt.putInt("mined", minedCount);
            CompoundTag toolMinedNbt = new CompoundTag();
            for (Item item : toolMinedMap.keySet()) {
                toolMinedNbt.putInt(Objects.requireNonNull(item.arch$registryName()).toString(), toolMinedMap.get(item));
            }
            nbt.put("toolMined", toolMinedNbt);
            return nbt;
        }

        public void readNbt(CompoundTag nbt) {
            if (nbt.contains("minedCount")) minedCount = nbt.getInt("mined");
            CompoundTag toolMinedNbt = nbt.getCompound("toolMined");
            for (String key : toolMinedNbt.getAllKeys()) {
                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(key));
                toolMinedMap.put(item, toolMinedNbt.getInt(key));
            }
        }
    }

    public static class EntityTypeStats {
        public int killedCount = 0;
        public int damageCount = 0;
        public int hurtCount = 0;
        public int retaliationDamage = 0;

        public CompoundTag writeNbt(CompoundTag nbt) {
            if (killedCount != 0) nbt.putInt("killed", killedCount);
            if (damageCount != 0) nbt.putInt("damage", damageCount);
            if (hurtCount != 0) nbt.putInt("hurt", hurtCount);
            if (retaliationDamage != 0) nbt.putInt("retaliation", retaliationDamage);
            return nbt;
        }

        public void readNbt(CompoundTag nbt) {
            if (nbt.contains("killed")) killedCount = nbt.getInt("killed");
            if (nbt.contains("damage")) damageCount = nbt.getInt("damage");
            if (nbt.contains("hurt")) hurtCount = nbt.getInt("hurt");
            if (nbt.contains("retaliation")) retaliationDamage = nbt.getInt("retaliation");
        }
    }

    public CompoundTag writeNbt(CompoundTag nbt) {
        CompoundTag itemTypeStatsNbt = new CompoundTag();
        for (Item item : itemTypeStats.keySet()) {
            itemTypeStatsNbt.put(Objects.requireNonNull(item.arch$registryName()).toString(), itemTypeStats.get(item).writeNbt(new CompoundTag()));
        }
        if (!itemTypeStatsNbt.isEmpty()) nbt.put("ItemTypeStats", itemTypeStatsNbt);
        CompoundTag blockTypeStatsNbt = new CompoundTag();
        for (Block block : blockTypeStats.keySet()) {
            blockTypeStatsNbt.put(Objects.requireNonNull(block.arch$registryName()).toString(), blockTypeStats.get(block).writeNbt(new CompoundTag()));
        }
        if (!blockTypeStatsNbt.isEmpty()) nbt.put("BlockTypeStats", blockTypeStatsNbt);
        CompoundTag entityTypeStatsNbt = new CompoundTag();
        for (EntityType<?> entity : entityTypeStats.keySet()) {
            entityTypeStatsNbt.put(Objects.requireNonNull(entity.arch$registryName()).toString(), entityTypeStats.get(entity).writeNbt(new CompoundTag()));
        }
        if (!entityTypeStatsNbt.isEmpty()) nbt.put("EntityTypeStats", entityTypeStatsNbt);
        return nbt;
    }

    public void readNbt(CompoundTag nbt) {
        if (nbt.contains("ItemTypeStats")) {
            CompoundTag itemTypeStatsNbt = nbt.getCompound("ItemTypeStats");
            for (String key : itemTypeStatsNbt.getAllKeys()) {
                Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(key));
                ItemTypeStats stats = new ItemTypeStats();
                stats.readNbt(itemTypeStatsNbt.getCompound(key));
                itemTypeStats.put(item, stats);
            }
        }
        if (nbt.contains("BlockTypeStats")) {
            CompoundTag blockTypeStatsNbt = nbt.getCompound("BlockTypeStats");
            for (String key : blockTypeStatsNbt.getAllKeys()) {
                Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(key));
                BlockTypeStats stats = new BlockTypeStats();
                stats.readNbt(blockTypeStatsNbt.getCompound(key));
                blockTypeStats.put(block, stats);
            }
        }
        if (nbt.contains("EntityTypeStats")) {
            CompoundTag entityTypeStatsNbt = nbt.getCompound("EntityTypeStats");
            for (String key : entityTypeStatsNbt.getAllKeys()) {
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.tryParse(key));
                EntityTypeStats stats = new EntityTypeStats();
                stats.readNbt(entityTypeStatsNbt.getCompound(key));
                entityTypeStats.put(entityType, stats);
            }
        }
    }

    public static void createEvents() {
        PlayerEvent.ATTACK_ENTITY.register((player, level, entity, interactionHand, entityHitResult) -> {
            ModularCreatureEntity creature = ((TeachingAccessor)player).somatogenesis$getTrainingCreature();
            if (creature != null) {

            }
            return EventResult.pass();
        });
    }
}
