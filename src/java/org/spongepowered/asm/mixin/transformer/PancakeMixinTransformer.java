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

    @Override
    public boolean computeFramesForClass(MixinEnvironment environment, String name, ClassNode classNode) {
        return transformer.computeFramesForClass(environment, name, classNode);
    }

    @Override
    public byte[] transformClass(MixinEnvironment environment, String name, byte[] classBytes) {
        return transformer.transformClass(environment, name, classBytes);
    }

    @Override
    public boolean transformClass(MixinEnvironment environment, String name, ClassNode classNode) {
        return transformer.transformClass(environment, name, classNode);
    }

    @Override
    public byte[] generateClass(MixinEnvironment environment, String name) {
        return transformer.generateClass(environment, name);
    }

    @Override
    public boolean generateClass(MixinEnvironment environment, String name, ClassNode classNode) {
        return transformer.generateClass(environment, name, classNode);
    }

}
