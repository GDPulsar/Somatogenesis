package com.pulsar.somatogenesis.registry;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.pulsar.somatogenesis.accessor.ProgressionAccessor;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class SomatogenesisCommands {
    public static void register() {
        CommandRegistrationEvent.EVENT.register((dispatcher, buildContext, selection) -> {
            dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("progression")
                    .then(LiteralArgumentBuilder.<CommandSourceStack>literal("clear")
                            .requires(CommandSourceStack::isPlayer)
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayer();
                                ((ProgressionAccessor)player).somatogenesis$getProgression().clear();
                                NetworkManager.sendToPlayer(player, SomatogenesisNetworking.UPDATE_PROGRESSION,
                                        new FriendlyByteBuf(Unpooled.buffer()).writeNbt(((ProgressionAccessor)player).somatogenesis$getProgression().writeNbt()));
                                return 1;
                            })
                    )
            );
        });
    }
}
