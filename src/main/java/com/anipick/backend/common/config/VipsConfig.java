package com.anipick.backend.common.config;

import app.photofox.vipsffm.Vips;
import app.photofox.vipsffm.VipsHelper;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VipsConfig {
    @PostConstruct
    public void init() {
        Vips.init();
        VipsHelper.block_untrusted_set(true);
        Vips.disableOperationCache();
    }
}
