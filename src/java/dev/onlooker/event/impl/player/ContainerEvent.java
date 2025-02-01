package dev.onlooker.event.impl.player;

import dev.onlooker.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.inventory.GuiContainer;

@Getter
@AllArgsConstructor
public class ContainerEvent extends Event {
    private final GuiContainer screen;
}
