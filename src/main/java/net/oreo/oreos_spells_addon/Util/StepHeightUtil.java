package net.oreo.oreos_spells_addon.Util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;

public class StepHeightUtil {
    private static final Field maxUpStepField;

    static {
        Field field = null;
        try {
            field = Entity.class.getDeclaredField("maxUpStep");
            field.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        maxUpStepField = field;
    }

    public static void setStepHeight(LivingEntity entity, float value) {
        if (maxUpStepField != null) {
            try {
                maxUpStepField.setFloat(entity, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static float getStepHeight(LivingEntity entity) {
        if (maxUpStepField != null) {
            try {
                return maxUpStepField.getFloat(entity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return 0.6f; // default step height
    }
}

