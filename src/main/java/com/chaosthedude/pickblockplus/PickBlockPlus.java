package com.chaosthedude.pickblockplus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(PickBlockPlus.MODID)
public class PickBlockPlus
{
	public Object client;
	
	public static final String MODID = "pickblockplus";

    public PickBlockPlus() {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> new Runnable() {
		@Override
		public void run() {
			client = new PickBlockPlusClient();
		}});
    }
}
