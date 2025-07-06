package net.josscoder.redisbridge.nukkit.logger;

import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.utils.TextFormat;
import net.josscoder.redisbridge.core.logger.ILogger;
import net.josscoder.redisbridge.nukkit.RedisBridgePlugin;

public class Logger implements ILogger {

    private final PluginLogger logger = RedisBridgePlugin.getInstance().getLogger();

    @Override
    public void info(String message) {
        logger.info(TextFormat.GREEN + message);
    }

    @Override
    public void warn(String message) {
        logger.warning(TextFormat.AQUA + message);
    }

    @Override
    public void debug(String message) {
        logger.info(TextFormat.DARK_BLUE + "[DEBUG] " + TextFormat.WHITE + message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }
}
