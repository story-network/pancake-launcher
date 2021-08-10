/*
 * Created on Wed Sep 30 2020
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package org.spongepowered.asm.mixin.transformer;

import java.util.List;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.ext.IExtensionRegistry;

/**
 * MixinTransformer implementation for PancakeLauncher
 */
public class PancakeMixinTransformer implements IMixinTransformer {

    private final MixinTransformer transformer = new MixinTransformer();

    @Override
    public void audit(MixinEnvironment environment) {
        transformer.audit(environment);
    }

    @Override
    public List<String> reload(String mixinClass, ClassNode classNode) {
        return transformer.reload(mixinClass, classNode);
    }

    @Override
    public byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
        byte[] res = transformer.transformClassBytes(name, transformedName, basicClass);

        return res; 
    }

    @Override
    public IExtensionRegistry getExtensions() {
        return transformer.getExtensions();
    }

}
