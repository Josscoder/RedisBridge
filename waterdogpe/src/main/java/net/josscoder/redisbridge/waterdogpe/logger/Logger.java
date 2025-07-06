package net.josscoder.redisbridge.waterdogpe.logger;

import dev.waterdog.waterdogpe.logger.Color;
import net.josscoder.redisbridge.core.logger.ILogger;
import net.josscoder.redisbridge.waterdogpe.RedisBridgePlugin;

public class Logger implements ILogger {

    private final org.apache.logging.log4j.Logger pluginLogger = RedisBridgePlugin.getInstance().getLogger();

    @Override
    public void info(String message) {
        pluginLogger.info("{}{}", Color.GREEN, message);
    }

    @Override
    public void warn(String message) {
        pluginLogger.warn("{}{}", Color.AQUA, message);
    }

    @Override
    public void debug(String message) {
        pluginLogger.info("{}[DEBUG] {}{}", Color.DARK_BLUE, Color.WHITE, message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        pluginLogger.error(message, throwable);
    }
}
