package dev.qther.quietus;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Quietus implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("quietus");
    public static final Config CONFIG = new Config();

    @Override
    public void onInitialize() {
        CommandQuietus.register();
        Runtime.getRuntime().addShutdownHook(new Thread(CONFIG::save));
        LOGGER.info("Quietus has been initialized!");
    }
}
