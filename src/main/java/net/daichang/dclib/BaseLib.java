package net.daichang.dclib;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.User;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Optional;

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

    default Optional<? extends ModContainer> modContainer(String modId) {
        return ModList.get().getModContainerById(modId);
    }

    default void setStaticFinalField(Class<?> clazz, String fieldName, Object value) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            long offset = DCUnsafeAccess.UNSAFE.staticFieldOffset(field);
            DCUnsafeAccess.UNSAFE.putBoolean(clazz, offset, (boolean) value);
        } catch (NoSuchFieldException e) {
            System.err.println("Field " + fieldName + " does not exist in class " + clazz.getName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set static final field: " + fieldName, e);
        }
    }

    GameRenderer gameRenderer = mc.gameRenderer;

    Options options = mc.options;

    Entity cameraEntity = mc.getCameraEntity();

    Window window = mc.getWindow();

    String gameDir = mc.gameDirectory.getPath();

    LevelRenderer levelRenderer = mc.levelRenderer;

    Screen screen = mc.screen;

    LocalPlayer localPlayer = mc.player;

    User minecraftUser = mc.getUser();

    Font font = mc.font;

    FontManager fontManager = mc.fontManager;

    default void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    default void endRender() {
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    HashMap<Integer, Integer> shadowCache = new HashMap<>();

    float maxFloat = Float.MAX_VALUE;

    float minFloat = Float.MIN_VALUE;

    int maxInt = Integer.MAX_VALUE;

    int minInt = Integer.MIN_VALUE;

    double maxDouble = Double.MAX_VALUE;

    double minDouble = Double.MIN_VALUE;

    float infiniteFloat = Float.POSITIVE_INFINITY;

    double infiniteDouble = Double.POSITIVE_INFINITY;

    // 随机数生成
    default double random(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    default float random(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }

    // 四舍五入到小数点后两位
    default float round2(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    // 计算两个 Vec3 之间的角度
    default double angle(Vec3 vec3, Vec3 other) {
        double lengthSq = vec3.lengthSqr() * other.lengthSqr();

        if (lengthSq < 1.0E-4D) {
            return 0.0;
        }

        double dot = vec3.dot(other);
        double arg = dot / lengthSq;

        if (arg > 1) {
            return 0.0;
        } else if (arg < -1) {
            return 180.0;
        }

        return Math.acos(arg) * 180.0f / Math.PI;
    }

    /**
     * 获取Float为两位小数
     * @param value
     * @return
     */
    default float getTwoFloat(float value){
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        return Float.parseFloat(format.format(value));
    }

    // 从一个 Vec3 到另一个点的向量
    default Vec3 fromTo(Vec3 from, double x, double y, double z) {
        return fromTo(from.x, from.y, from.z, x, y, z);
    }

    default Vec3 fromTo(double x, double y, double z, double x2, double y2, double z2) {
        return new Vec3(x2 - x, y2 - y, z2 - z);
    }

    // 线性插值
    default float lerp(float f, float st, float en) {
        return st + f * (en - st);
    }

    // 将角度转换为弧度
    default float rad(float angle) {
        return (float) (angle * Math.PI / 180);
    }

    // 数值限制
    default int clamp(int num, int min, int max) {
        return num < min ? min : Math.min(num, max);
    }

    default float clamp(float num, float min, float max) {
        return num < min ? min : Math.min(num, max);
    }

    default double clamp(double num, double min, double max) {
        return num < min ? min : Math.min(num, max);
    }

    // 数学函数
    default double square(double input) {
        return input * input;
    }

    default double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.FLOOR);
        return bd.doubleValue();
    }

    default float round(float value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    default double degToRad(double deg) {
        return deg * 0.01745329238474369;
    }

    // 计算角度
    default float[] calcAngle(Vec3 from, Vec3 to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = Math.sqrt(difX * difX + difZ * difZ);
        return new float[]{
                (float) Math.toDegrees(Math.atan2(difZ, difX)) - 90.0f,
                (float) Math.toDegrees(Math.atan2(difY, dist))
        };
    }

    // 将数值四舍五入到最近的一个数
    default double roundToClosest(double num, double low, double high) {
        double d1 = num - low;
        double d2 = high - num;

        return d2 > d1 ? low : high;
    }
}
