package net.daichang.dclib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;

/**
 *
 * 此库是最基础的
 * 我其它的所有库都继承此
 * <p>
 * This library is the most basic
 * All my other libraries inherit this
 * <p>
 * */
public interface BaseLib {
    Minecraft mc = Minecraft.getInstance();

    GameRenderer gameRenderer = mc.gameRenderer;

    LevelRenderer levelRenderer = mc.levelRenderer;

    Screen screen = mc.screen;

    LocalPlayer localPlayer = mc.player;

    User minecraftUser = mc.getUser();

    float maxFloat = Float.MAX_VALUE;

    float minFloat = Float.MIN_VALUE;

    int maxInt = Integer.MAX_VALUE;

    int minInt = Integer.MIN_VALUE;

    double maxDouble = Double.MAX_VALUE;

    double minDouble = Double.MIN_VALUE;

    float infiniteFloat = Float.POSITIVE_INFINITY;

    double infiniteDouble = Double.POSITIVE_INFINITY;
}
