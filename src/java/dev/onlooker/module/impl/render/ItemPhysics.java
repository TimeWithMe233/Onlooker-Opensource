package dev.onlooker.module.impl.render;

import dev.onlooker.module.Category;
import dev.onlooker.module.Module;

/**
 * @author cedo
 * @since 03/24/2022
 */
public class ItemPhysics extends Module {

    public ItemPhysics() {
        super("ItemPhysics", Category.RENDER, "Makes items have physics");
        if (!enabled) this.toggleSilent();
    }

}
