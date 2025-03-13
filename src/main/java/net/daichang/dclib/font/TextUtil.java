package net.daichang.dclib.font;

import net.daichang.dclib.BaseLib;
import net.minecraft.ChatFormatting;

class TextUtil implements BaseLib {
    public static int bc;

    private static final ChatFormatting[] color = new ChatFormatting[] { ChatFormatting.RED, ChatFormatting.GOLD, ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.AQUA, ChatFormatting.LIGHT_PURPLE };

    public static String formatting(String input, ChatFormatting[] colours, double delay) {
        StringBuilder sb = new StringBuilder(input.length() * 1000);
        if (delay <= 0.2D)
            delay = 0.9999D;
        int offset = (int)Math.floor((System.currentTimeMillis() & 0x3FFFL) / delay) % colours.length;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            sb.append(colours[(colours.length + i - offset) % colours.length].toString());
            sb.append(c);
            bc = i;
        }
        return sb.toString();
    }

    public static String GetColor(String input, double delay) {
        return formatting(input, color, delay);
    }
}
