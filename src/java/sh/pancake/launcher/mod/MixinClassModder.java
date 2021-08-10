/*
 * Created on Wed Sep 30 2020
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.launcher.mod;

import org.spongepowered.asm.mixin.transformer.PancakeMixinTransformer;

public class MixinClassModder implements IClassModder {

	private PancakeMixinTransformer transformer;

    public MixinClassModder() {
		this.transformer = new PancakeMixinTransformer();
	}

	@Override
	public byte[] transformClassData(String name, byte[] data) {
		return transformer.transformClassBytes(name, name, data);
	}

}