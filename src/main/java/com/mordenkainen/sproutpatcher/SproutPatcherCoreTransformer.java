package com.mordenkainen.sproutpatcher;

import com.mordenkainen.sproutpatcher.patches.BedPatcher;
import com.mordenkainen.sproutpatcher.patches.BotaniaPatcher;
import com.mordenkainen.sproutpatcher.patches.CabinetPatcher;
import com.mordenkainen.sproutpatcher.patches.IPatch;
import com.mordenkainen.sproutpatcher.patches.IceAndFirePatcher;
import com.mordenkainen.sproutpatcher.patches.ReComplexPatcher;
import com.mordenkainen.sproutpatcher.patches.SoundPatcher;

import net.minecraft.launchwrapper.IClassTransformer;

public class SproutPatcherCoreTransformer implements IClassTransformer {
    private static IPatch[] patches= {new CabinetPatcher(), new ReComplexPatcher(), new BotaniaPatcher(), new SoundPatcher(), new BedPatcher(), new IceAndFirePatcher()};
    
    @Override
    public byte[] transform(final String name, final String transformedName, byte[] basicClass) {
        //SproutPatcherCoreLoader.logger.info("Scanning: " + name);
        
        for (IPatch patch : patches) {
            if (patch.shouldLoad()) {
                basicClass = patch.transform(name, transformedName, basicClass);
            }
        }
        
        return basicClass;

    }

}
