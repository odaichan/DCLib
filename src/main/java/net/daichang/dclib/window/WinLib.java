package net.daichang.dclib.window;

import net.daichang.dclib.BaseLib;
import org.lwjgl.glfw.GLFW;

/**
 * 基于GLFW
 * <P>
 * Based on GLFW
 * */
public interface WinLib extends BaseLib {
    long windows = getWindow.getWindow();

    /**
     * 设置窗口标题
     * <p></p>
     * Set Window Title
     * */
    default void setWindowTitle(String windowTitle) {
        GLFW.glfwSetWindowTitle(windows, windowTitle);
        mc.updateTitle();
    }

    /**
     * 设置窗口透明度
     * <p>
     * Set window transparency
     * */
    default void setWindowOpacity(float opacity) {
        GLFW.glfwSetWindowOpacity(windows, opacity);
    }

    /**
     * 不建议使用
     * */
    default void killWindow(){
        GLFW.glfwDestroyWindow(windows);
    }
}
