package net.nayrus.betterbeats.item;

import net.nayrus.betterbeats.BetterBeats;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;

public class ItemRegistry
{
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BetterBeats.MOD_ID);

    public static final DeferredItem<Item> BEATWAKER = ITEMS.register("beatwaker",
            () -> new BeatWaker(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
