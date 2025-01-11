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

        public boolean contains(Class<? extends Item> itemclass) {
            return itemclass.isInstance(getA().getItem()) || itemclass.isInstance(getB().getItem());
        }

        public ItemStack getFirst(Item item){
            if(getA().is(item)) return getA();
            if(getB().is(item)) return getB();
            return ItemStack.EMPTY;
        }
    }

    public static ItemStackTuple getHeldItems(Player player){
        return new ItemStackTuple(player.getMainHandItem(), player.getOffhandItem());
    }
}
