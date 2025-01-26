package com.pulsar.somatogenesis;

import com.mojang.blaze3d.platform.InputConstants;
import com.pulsar.somatogenesis.block.client.BloodAltarRenderer;
import com.pulsar.somatogenesis.block.client.EvolutionTankRenderer;
import com.pulsar.somatogenesis.block.client.SpellRuneRenderer;
import com.pulsar.somatogenesis.client.ProgressionTreeScreen;
import com.pulsar.somatogenesis.event.FillBloodContainersEvent;
import com.pulsar.somatogenesis.item.BloodContainer;
import com.pulsar.somatogenesis.item.BloodGauntletItem;
import com.pulsar.somatogenesis.menu.EvolutionTankScreen;
import com.pulsar.somatogenesis.registry.*;
import com.pulsar.somatogenesis.reload.ProgressionUnlockReloadListener;
import com.pulsar.somatogenesis.reload.RuneReloadListener;
import com.pulsar.somatogenesis.util.BloodUtils;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Somatogenesis {
    public static final String MOD_ID = "somatogenesis";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        SomatogenesisFluids.FLUIDS.register();
        SomatogenesisFluids.register();
        SomatogenesisBlocks.BLOCKS.register();
        SomatogenesisBlocks.BLOCK_ENTITIES.register();
        SomatogenesisItems.ITEMS.register();
        SomatogenesisTabs.TABS.register();
        SomatogenesisMenus.MENUS.register();
        SomatogenesisCommands.register();
        SomatogenesisEntities.ENTITY_TYPES.register();
        SomatogenesisEntities.registerCommon();
        SomatogenesisEffects.EFFECTS.register();
        SomatogenesisNetworking.registerCommon();
        SomatogenesisRecipes.TYPES.register();
        SomatogenesisRecipes.SERIALIZERS.register();

        EntityEvent.LIVING_HURT.register((living, source, damage) -> {
            if (source.getEntity() instanceof Player player) {
                if (!player.level().isClientSide) {
                    float multiplier = BloodUtils.getBloodlettingMultiplier(living);
                    int bloodGain = (int) (damage * BloodUtils.getBloodGainMultiplier(player) * multiplier);
                    if (player.getOffhandItem().getItem() instanceof BloodContainer container) {
                        container.addBlood(player.getOffhandItem(), bloodGain);
                    } else {
                        FillBloodContainersEvent.fill(player, bloodGain);
                    }
                }
            }
            return EventResult.pass();
        });

        ReloadListenerRegistry.register(PackType.SERVER_DATA, new ProgressionUnlockReloadListener());
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new RuneReloadListener());
    }

    public static final KeyMapping PROGRESSION = new KeyMapping(
            "key.somatogenesis.progression_key", InputConstants.Type.KEYSYM, InputConstants.KEY_I, "category.somatogenesis"
    );

    public static int selectedShape = 0;
    public static void initClient() {
        SomatogenesisNetworking.registerClient();

        KeyMappingRegistry.register(PROGRESSION);

        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (PROGRESSION.consumeClick()) {
                minecraft.setScreen(new ProgressionTreeScreen(Component.empty()));
            }
        });

        ClientRawInputEvent.MOUSE_SCROLLED.register((minecraft, v) -> {
            if (minecraft.player.isUsingItem()) {
                ItemStack stack = minecraft.player.getUseItem();
                if (stack.is(SomatogenesisItems.BLOOD_GAUNTLET.get())) {
                    if (stack.getItem() instanceof BloodGauntletItem bloodGauntlet) {
                        selectedShape = (((int)v) + selectedShape) % BloodGauntletItem.Shape.values().length;
                        while (selectedShape < 0) selectedShape += BloodGauntletItem.Shape.values().length;
                        return EventResult.interruptTrue();
                    }
                }
            }
            return EventResult.pass();
        });

        ItemPropertiesRegistry.registerGeneric(reloc("blood"), (itemStack, clientLevel, livingEntity, i) -> {
            if (itemStack.getItem() instanceof BloodContainer container) {
                return (float)container.getBlood(itemStack) / container.getMaxBlood();
            }
            return 0;
        });

        ItemPropertiesRegistry.register(SomatogenesisItems.SYRINGE.get(), reloc("filled"), (itemStack, clientLevel, livingEntity, i) -> {
            return itemStack.getOrCreateTag().contains("bloodType") ? 1f : 0f;
        });

        BlockEntityRendererRegistry.register(SomatogenesisBlocks.BLOOD_ALTAR_ENTITY.get(), BloodAltarRenderer::new);
        BlockEntityRendererRegistry.register(SomatogenesisBlocks.EVOLUTION_TANK_ENTITY.get(), EvolutionTankRenderer::new);
        BlockEntityRendererRegistry.register(SomatogenesisBlocks.SPELL_RUNE_ENTITY.get(), SpellRuneRenderer::new);

        SomatogenesisEntities.registerClient();

        MenuRegistry.registerScreenFactory(SomatogenesisMenus.EVOLUTION_TANK_MENU.get(), EvolutionTankScreen::new);
    }

    public static ResourceLocation reloc(String id) {
        return new ResourceLocation(MOD_ID, id);
    }
}
