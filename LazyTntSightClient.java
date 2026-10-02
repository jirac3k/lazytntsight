package com.example.lazytntsight;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class LazyTntSightClient implements ClientModInitializer {
    /** On by default. Ctrl+H flips it. */
    public static boolean enabled = true;

    private static KeyMapping toggleKey;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.lazytntsight.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                KeyMapping.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                if (client.hasControlDown()) {
                    enabled = !enabled;
                    if (client.player != null) {
                        client.player.displayClientMessage(Component.translatable(
                                enabled ? "message.lazytntsight.on" : "message.lazytntsight.off"), true);
                    }
                }
            }
        });
    }
}
