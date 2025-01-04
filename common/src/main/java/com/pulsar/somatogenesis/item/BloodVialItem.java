package com.pulsar.somatogenesis.item;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.accessor.ProgressionAccessor;
import com.pulsar.somatogenesis.block.SpellRuneBlockEntity;
import com.pulsar.somatogenesis.registry.SomatogenesisBlocks;
import com.pulsar.somatogenesis.rune.Runes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class BloodVialItem extends SimpleBloodContainerItem {
    public BloodVialItem(Properties properties) {
        super(properties, 20);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext useOnContext) {
        if (((ProgressionAccessor)useOnContext.getPlayer()).somatogenesis$getProgression().unlocked(Somatogenesis.reloc("basic_spells"))) {
            if (!useOnContext.getPlayer().isCrouching()) {
                BlockPos drawPos = getDrawingPos(useOnContext.getItemInHand());
                if (drawPos == null) {
                    drawPos = useOnContext.getClickedPos().relative(useOnContext.getClickedFace());
                    setDrawingPos(useOnContext.getItemInHand(), drawPos);
                }
                if (!useOnContext.getLevel().getBlockState(drawPos).is(SomatogenesisBlocks.SPELL_RUNE.get())) {
                    drawPos = useOnContext.getClickedPos().relative(useOnContext.getClickedFace());
                    setDrawingPos(useOnContext.getItemInHand(), drawPos);
                }
                BlockEntity blockEntity = useOnContext.getLevel().getBlockEntity(drawPos);
                if (blockEntity == null) {
                    useOnContext.getLevel().setBlockAndUpdate(drawPos, SomatogenesisBlocks.SPELL_RUNE.get().defaultBlockState());
                    blockEntity = useOnContext.getLevel().getBlockEntity(drawPos);
                    if (blockEntity instanceof SpellRuneBlockEntity spellRune)
                        spellRune.setFace(useOnContext.getClickedFace());
                    return super.useOn(useOnContext);
                }
                if (blockEntity instanceof SpellRuneBlockEntity spellRune) {
                    spellRune.draw(useOnContext.getClickLocation(), 1);
                }
            } else {
                BlockPos drawPos = getDrawingPos(useOnContext.getItemInHand());
                if (drawPos != null) {
                    BlockEntity blockEntity = useOnContext.getLevel().getBlockEntity(drawPos);
                    if (blockEntity instanceof SpellRuneBlockEntity spellRune) {
                        boolean set = false;
                        Runes.Rune best = Runes.getBestMatch(spellRune.getDrawn().stream().toList(), useOnContext.getPlayer());
                        if (best != null) {
                            double matchPercent = Runes.getMatchPercent(spellRune.getDrawn().stream().toList(), best);
                            if (matchPercent >= 0.7) {
                                spellRune.setRune(best);
                                set = true;
                            }
                        }
                        if (!set) {
                            spellRune.changeHighlight(useOnContext.getPlayer());
                        }
                    }
                }
            }
        }
        return super.useOn(useOnContext);
    }

    private BlockPos getDrawingPos(ItemStack stack) {
        return stack.getOrCreateTag().contains("drawingAt") ? NbtUtils.readBlockPos(stack.getOrCreateTag().getCompound("drawingAt")) : null;
    }

    private void setDrawingPos(ItemStack stack, BlockPos pos) {
        stack.getOrCreateTag().put("drawingAt", NbtUtils.writeBlockPos(pos));
    }
}
