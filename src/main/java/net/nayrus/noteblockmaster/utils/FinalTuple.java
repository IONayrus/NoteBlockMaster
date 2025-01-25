package net.nayrus.noteblockmaster.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FinalTuple<A, B> {

    private final A a;
    private final B b;

    public FinalTuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public static class ItemStackTuple extends FinalTuple<ItemStack, ItemStack> {

        public ItemStackTuple(ItemStack a, ItemStack b) {
            super(a, b);
        }

        public boolean contains(Item item) {
            return getA().is(item) || getB().is(item);
        }

        @SafeVarargs
        public final boolean contains(Class<? extends Item>... itemclasses) {
            for(Class<? extends Item> itemclass : itemclasses){
                if(itemclass.isInstance(getA().getItem()) || itemclass.isInstance(getB().getItem())) return true;
            }
            return false;
        }

        public ItemStack getFirst(Item item){
            if(getA().is(item)) return getA();
            if(getB().is(item)) return getB();
            return ItemStack.EMPTY;
        }

        public ItemStack getFirst(Class<? extends Item> itemclass) {
            if(itemclass.isInstance(getA().getItem())) return getA();
            if(itemclass.isInstance(getB().getItem())) return getB();
            return ItemStack.EMPTY;
        }
    }

    public static ItemStackTuple getHeldItems(Player player){
        return new ItemStackTuple(player.getMainHandItem(), player.getOffhandItem());
    }
}
