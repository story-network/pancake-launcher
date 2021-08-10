/*
 * Created on Sat Sep 26 2020
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.launcher.service;

import org.spongepowered.asm.service.IMixinServiceBootstrap;

public class ServiceBootstrap implements IMixinServiceBootstrap {

    @Override
    public String getName() {
        return "StoryBootstrap";
    }

    @Override
    public String getServiceClassName() {
        return "sh.pancake.server.service.PancakeMixinService";
    }

    @Override
    public void bootstrap() {

    }

}