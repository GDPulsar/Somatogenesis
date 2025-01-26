package com.pulsar.somatogenesis.item;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class BloodGauntletItem extends Item {
    public BloodGauntletItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (player.isCrouching()) {
            this.clearShapes(itemStack);
            return InteractionResultHolder.success(itemStack);
        }
        player.startUsingItem(interactionHand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 72000;
    }

    public void addShape(ItemStack stack, Shape shape) {
        if (!stack.getOrCreateTag().contains("Shapes")) stack.getOrCreateTag().put("Shapes", new ListTag());
        stack.getOrCreateTag().getList("Shapes", Tag.TAG_STRING).add(StringTag.valueOf(shape.name()));
    }

    public List<Shape> getShapes(ItemStack stack) {
        List<Shape> shapes = new ArrayList<>();
        if (!stack.getOrCreateTag().contains("Shapes")) stack.getOrCreateTag().put("Shapes", new ListTag());
        ListTag shapesNbt = stack.getOrCreateTag().getList("Shapes", Tag.TAG_STRING);
        for (int i = 0; i < shapesNbt.size(); i++) {
            shapes.add(Shape.valueOf(shapesNbt.getString(i)));
        }
        return shapes;
    }

    public void clearShapes(ItemStack stack) {
        stack.getOrCreateTag().remove("Shapes");
    }

    public enum Shape {
        CIRCLE(10),
        CROSS(15),
        TRIANGLE(20),
        SQUARE(25),
        PENTAGON(50),
        FIVESTAR(50),
        HEXAGON(75),
        SIXSTAR(75),
        OCTAGON(90),
        SPLITFIVESTAR(100);

        private final int cost;
        Shape(int cost) {
            this.cost = cost;
        }

        public int getCost() {
            return cost;
        }
    }
}
