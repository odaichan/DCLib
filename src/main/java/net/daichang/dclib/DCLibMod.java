package net.daichang.dclib;

import net.daichang.dclib.window.WinLib;
import net.minecraftforge.fml.common.Mod;

/**
 *
 * 此库由由带长以Mod形式制作
 * 无任何强大的功能
 * 只有基础的操作(指秒杀)
 * <p>
 * This library is created in Mod form by DaiChang
 * Without any powerful features
 * Only basic operations (referring to flash sales)
 * */
@Mod(DCLibMod.MOD_ID)
public class DCLibMod implements WinLib {
    public static final String MOD_ID = "dclib";

    public DCLibMod() {
        while (mc.isRunning()) mc.updateTitle();
    }
}
