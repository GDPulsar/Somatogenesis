package com.pulsar.somatogenesis.progression;

import com.pulsar.somatogenesis.accessor.ProgressionAccessor;
import com.pulsar.somatogenesis.progression.requirements.ProgressionRequirement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.Arrays;

public record ProgressionUnlock(ResourceLocation id, Item icon, int x, int y, ResourceLocation[] parents,
                                ProgressionRequirement[] requirements) {

    public MutableComponent getTitle() {
        return Component.translatable("progression." + id.getNamespace() + "." + id.getPath() + ".title");
    }

    public MutableComponent getTooltip() {
        return Component.translatable("progression." + id.getNamespace() + "." + id.getPath() + ".tooltip");
    }

    public MutableComponent getDescription() {
        return Component.translatable("progression." + id.getNamespace() + "." + id.getPath() + ".description");
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ProgressionUnlock unlock && unlock.id == id;
    }

    public boolean completed(Player player) {
        return Arrays.stream(this.requirements).allMatch(requirement -> requirement.completed(player));
    }

    public boolean accessible(Player player) {
        return Arrays.stream(this.parents).allMatch(pid -> ((ProgressionAccessor) player).somatogenesis$getProgression().unlocked(pid));
    }

    public double progress(Player player) {
        double value = 0;
        for (ProgressionRequirement requirement : this.requirements) value += requirement.progress(player);
        return value / this.requirements.length;
    }

    public void submit(Player player) {
        for (ProgressionRequirement requirement : this.requirements) {
            requirement.submit(player);
        }
    }

    public void clear() {
        for (ProgressionRequirement requirement : this.requirements) {
            requirement.clear();
        }
    }

    public CompoundTag writeNbt() {
        CompoundTag nbt = new CompoundTag();
        ListTag requirementsNbt = new ListTag();
        for (ProgressionRequirement requirement : this.requirements) {
            requirementsNbt.add(IntTag.valueOf(requirement.submittedCount()));
        }
        nbt.put("requirements", requirementsNbt);
        return nbt;
    }

    public void readNbt(CompoundTag nbt) {
        ListTag requirementsNbt = nbt.getList("requirements", ListTag.TAG_INT);
        for (int i = 0; i < this.requirements.length; i++) {
            this.requirements[i].fromSubmittedCount(requirementsNbt.getInt(i));
        }
    }
}
