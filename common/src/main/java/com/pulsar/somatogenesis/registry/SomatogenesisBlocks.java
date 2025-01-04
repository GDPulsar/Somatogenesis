package com.pulsar.somatogenesis.registry;

import com.pulsar.somatogenesis.Somatogenesis;
import com.pulsar.somatogenesis.block.*;
import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static com.pulsar.somatogenesis.registry.SomatogenesisItems.ITEMS;

public class SomatogenesisBlocks {
    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(Somatogenesis.MOD_ID, Registries.BLOCK);
    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Somatogenesis.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static RegistrySupplier<Block> FLESH_BLOCK = BLOCKS.register("flesh_block", () -> new Block(BlockBehaviour.Properties.of().pushReaction(PushReaction.NORMAL)
            .sound(SoundType.WART_BLOCK).strength(2.5f, 5f)));
    public static RegistrySupplier<Item> FLESH_BLOCK_ITEM = ITEMS.register("flesh_block", () -> new BlockItem(FLESH_BLOCK.get(),
            new Item.Properties().rarity(Rarity.COMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));

    public static RegistrySupplier<Block> VESSELED_FLESH_BLOCK = BLOCKS.register("vesseled_flesh_block", () -> new VesseledBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.NORMAL)
            .sound(SoundType.WART_BLOCK).strength(2.5f, 5f)));
    public static RegistrySupplier<Item> VESSELED_FLESH_BLOCK_ITEM = ITEMS.register("vesseled_flesh_block", () -> new BlockItem(VESSELED_FLESH_BLOCK.get(),
            new Item.Properties().rarity(Rarity.COMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));

    public static RegistrySupplier<BloodAltarBlock> BLOOD_ALTAR = BLOCKS.register("blood_altar", () ->
            new BloodAltarBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK).randomTicks()
                    .sound(SoundType.STONE).dynamicShape().noOcclusion().requiresCorrectToolForDrops().strength(15f, 15f)));
    public static RegistrySupplier<Item> TIER_1_BLOOD_ALTAR_ITEM = ITEMS.register("tier_1_blood_altar", () -> new BlockItem(BLOOD_ALTAR.get(),
            new Item.Properties().rarity(Rarity.UNCOMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));
    public static RegistrySupplier<Item> TIER_2_BLOOD_ALTAR_ITEM = ITEMS.register("tier_2_blood_altar", () -> new BlockItem(BLOOD_ALTAR.get(),
            new Item.Properties().rarity(Rarity.UNCOMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));
    public static RegistrySupplier<Item> TIER_3_BLOOD_ALTAR_ITEM = ITEMS.register("tier_3_blood_altar", () -> new BlockItem(BLOOD_ALTAR.get(),
            new Item.Properties().rarity(Rarity.UNCOMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));
    public static RegistrySupplier<Item> TIER_4_BLOOD_ALTAR_ITEM = ITEMS.register("tier_4_blood_altar", () -> new BlockItem(BLOOD_ALTAR.get(),
            new Item.Properties().rarity(Rarity.UNCOMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));
    public static RegistrySupplier<Item> TIER_5_BLOOD_ALTAR_ITEM = ITEMS.register("tier_5_blood_altar", () -> new BlockItem(BLOOD_ALTAR.get(),
            new Item.Properties().rarity(Rarity.UNCOMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));
    public static RegistrySupplier<BlockEntityType<BloodAltarBlockEntity>> BLOOD_ALTAR_ENTITY = BLOCK_ENTITIES.register("blood_altar", () ->
            BlockEntityType.Builder.of(BloodAltarBlockEntity::new, BLOOD_ALTAR.get()).build(null));

    public static RegistrySupplier<SpellRuneBlock> SPELL_RUNE = BLOCKS.register("spell_rune", SpellRuneBlock::new);
    public static RegistrySupplier<BlockEntityType<SpellRuneBlockEntity>> SPELL_RUNE_ENTITY = BLOCK_ENTITIES.register("spell_rune", () ->
            BlockEntityType.Builder.of(SpellRuneBlockEntity::new, SPELL_RUNE.get()).build(null));

    public static RegistrySupplier<EvolutionTankBlock> EVOLUTION_TANK = BLOCKS.register("evolution_tank", () ->
            new EvolutionTankBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK).randomTicks()
                    .sound(SoundType.STONE).dynamicShape().noOcclusion().requiresCorrectToolForDrops().strength(15f, 15f)));
    public static RegistrySupplier<BlockEntityType<EvolutionTankBlockEntity>> EVOLUTION_TANK_ENTITY = BLOCK_ENTITIES.register("evolution_tank", () ->
            BlockEntityType.Builder.of(EvolutionTankBlockEntity::new, EVOLUTION_TANK.get()).build(null));
    public static RegistrySupplier<Item> EVOLUTION_TANK_ITEM = ITEMS.register("evolution_tank", () -> new BlockItem(EVOLUTION_TANK.get(),
            new Item.Properties().rarity(Rarity.UNCOMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));

    public static RegistrySupplier<CruncherBlock> CRUNCHER = BLOCKS.register("cruncher", () ->
            new CruncherBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).randomTicks()
                    .sound(SoundType.WART_BLOCK).dynamicShape().noOcclusion().requiresCorrectToolForDrops().strength(5f, 5f)));
    public static RegistrySupplier<BlockEntityType<CruncherBlockEntity>> CRUNCHER_ENTITY = BLOCK_ENTITIES.register("cruncher", () ->
            BlockEntityType.Builder.of(CruncherBlockEntity::new, CRUNCHER.get()).build(null));
    public static RegistrySupplier<Item> CRUNCHER_ITEM = ITEMS.register("cruncher", () -> new BlockItem(CRUNCHER.get(),
            new Item.Properties().rarity(Rarity.UNCOMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));

    public static RegistrySupplier<DrainerBlock> DRAINER = BLOCKS.register("drainer", () ->
            new DrainerBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).randomTicks()
                    .sound(SoundType.WART_BLOCK).dynamicShape().noOcclusion().requiresCorrectToolForDrops().strength(5f, 5f)));
    public static RegistrySupplier<BlockEntityType<DrainerBlockEntity>> DRAINER_ENTITY = BLOCK_ENTITIES.register("drainer", () ->
            BlockEntityType.Builder.of(DrainerBlockEntity::new, DRAINER.get()).build(null));
    public static RegistrySupplier<Item> DRAINER_ITEM = ITEMS.register("drainer", () -> new BlockItem(DRAINER.get(),
            new Item.Properties().rarity(Rarity.UNCOMMON).arch$tab(SomatogenesisTabs.SOMATOGENESIS_TAB)));

    public static final RegistrySupplier<LiquidBlock> BLOOD_FLUID_BLOCK = BLOCKS.register("blood", () -> new ArchitecturyLiquidBlock(SomatogenesisFluids.BLOOD_SOURCE_FLOWING,
            BlockBehaviour.Properties.copy(Blocks.WATER)));
    public static final RegistrySupplier<LiquidBlock> EVOLUTIONARY_MIXTURE_FLUID_BLOCK = BLOCKS.register("evolutionary_mixture", () -> new ArchitecturyLiquidBlock(SomatogenesisFluids.EVOLUTIONARY_MIXTURE_FLOWING,
            BlockBehaviour.Properties.copy(Blocks.WATER)) {
        @Override
        public ItemStack pickupBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
            return ItemStack.EMPTY;
        }
    });
}
