package com.replaymod.replay;

import com.replaymod.replaystudio.ILogger;
import org.apache.logging.log4j.Logger;

public class ReplayStudioLogger implements ILogger {
    @Override
    public Logger getLogger() {
        return ReplayModReplay.LOGGER;
    }
}
