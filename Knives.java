package com.example.knifeclient;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public final class Knives {
    public static final String[] IDS = {"karambit", "bayonet", "butterfly", "huntsman"};
    public static final String[] NAMES = {"Karambit", "Bayonet", "Butterfly Knife", "Huntsman Knife"};
    private static final ItemStack[] STACKS = new ItemStack[IDS.length];

    public static ItemStack current() {
        int i = Math.floorMod(Config.knife, IDS.length);
        if (STACKS[i] == null) {
            ItemStack stack = new ItemStack(Items.STICK);
            stack.set(DataComponentTypes.ITEM_MODEL, Identifier.of("knifeclient", IDS[i]));
            STACKS[i] = stack;
        }
        return STACKS[i];
    }
}
