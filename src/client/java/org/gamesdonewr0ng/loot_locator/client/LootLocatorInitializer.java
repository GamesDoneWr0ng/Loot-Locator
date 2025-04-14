package org.gamesdonewr0ng.loot_locator.client;

import net.fabricmc.api.ClientModInitializer;

public class LootLocatorInitializer implements ClientModInitializer {
    private boolean initialized;

    @Override
    public void onInitializeClient() {
        if(initialized)
            throw new RuntimeException(
                    "WurstInitializer.onInitialize() ran twice!");

        LootLocatorClient.INSTANCE.initialize();
        initialized = true;
    }
}
