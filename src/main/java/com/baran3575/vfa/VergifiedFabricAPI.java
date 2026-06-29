package com.baran3575.vfa;

import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("vfa")
public class VergifiedFabricAPI {
    public static final Logger LOGGER = LoggerFactory.getLogger(VergifiedFabricAPI.class);

    public VergifiedFabricAPI() {
        LOGGER.info("Vergified Fabric API (VFA) is initializing!");
    }
}
