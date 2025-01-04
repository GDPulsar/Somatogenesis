package com.pulsar.somatogenesis;

import com.pulsar.somatogenesis.registry.SomatogenesisForgeItems;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

@Mod(Somatogenesis.MOD_ID)
public final class SomatogenesisForge {
    public SomatogenesisForge() {
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Somatogenesis.MOD_ID, eventBus);
        eventBus.addListener(this::setup);

        Somatogenesis.init();
        SomatogenesisForgeItems.ITEMS.register(eventBus);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            Somatogenesis.initClient();
        }
    }

    private void setup(final FMLCommonSetupEvent evt) {
        CuriosApi.registerCurio(SomatogenesisForgeItems.BLOOD_PENDANT.get(), new ICurioItem() {
            @Override
            public boolean hasCurioCapability(ItemStack stack) {
                return true;
            }
        });
    }
}
