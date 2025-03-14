package net.daichang.dclib.window;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface BlueScreenAPI extends Library{
    BlueScreenAPI API = Native.load("bluescreen-win-jna.dll", BlueScreenAPI.class);

    void BlueScreen(boolean paramBoolean);
}
