package com.pulsar.somatogenesis.block.vessel_network;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class VesselNetworkStorageEntity extends BlockEntity implements VesselNetworkStorage {
    protected int blood = 0;
    protected final int maxBlood;
    public VesselNetworkStorageEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, int maxBlood) {
        super(blockEntityType, blockPos, blockState);
        this.maxBlood = maxBlood;
    }

    @Override
    public int addBlood(int amount) {
        int toAdd = Math.min(maxBlood - blood, amount);
        blood += toAdd;
        return amount - toAdd;
    }

    @Override
    public int takeBlood(int amount) {
        int toTake = Math.min(blood, amount);
        blood -= toTake;
        return toTake;
    }

    @Override
    public int getBlood() {
        return blood;
    }

    @Override
    public int getMaxBlood() {
        return maxBlood;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("blood", blood);
        return tag;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        blood = compoundTag.getInt("blood");
        super.load(compoundTag);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        compoundTag.putInt("blood", blood);
        super.saveAdditional(compoundTag);
    }
}
