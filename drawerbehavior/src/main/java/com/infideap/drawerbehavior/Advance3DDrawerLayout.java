package com.infideap.drawerbehavior;

import android.content.Context;
import android.graphics.Color;
import androidx.core.view.ViewCompat;
import androidx.cardview.widget.CardView;
import android.util.AttributeSet;

/**
 * Created by Shiburagi on 21/09/2017.
 */
public class Advance3DDrawerLayout extends AdvanceDrawerLayout {
    private static final String TAG = Advance3DDrawerLayout.class.getSimpleName();

    public Advance3DDrawerLayout(Context context) {
        super(context);
    }


    public Advance3DDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Advance3DDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    void updateSlideOffset(CardView child, AdvanceDrawerLayout.Setting setting, float width, float slideOffset, boolean isLeftDrawer) {
        updateSlideOffset(child, (Setting) setting, width, slideOffset, isLeftDrawer);

    }

    void updateSlideOffset(CardView child, Setting setting, float width, float slideOffset, boolean isLeftDrawer) {
        if (setting.degree > 0) {
            float percentage = (setting.degree) / 90f;

            child.setX( width * slideOffset - (child.getWidth()/2.0f) * percentage * slideOffset);
            child.setRotationY((isLeftDrawer ? -1 : 1) * setting.degree * slideOffset);

        } else
            super.updateSlideOffset(child, setting, width, slideOffset, isLeftDrawer);


    }

    @Override
    AdvanceDrawerLayout.Setting createSetting() {
        return new Setting();
    }

    public void setViewRotation(int gravity, float degree) {
        int absGravity = getDrawerViewAbsoluteGravity(gravity);
        Setting setting;
        if (!settings.containsKey(absGravity)) {
            setting = (Setting) createSetting();
            settings.put(absGravity, setting);
        } else
            setting = (Setting) settings.get(absGravity);

        assert setting != null;
        setting.degree = degree > 45 ? 45 : degree;

        setting.scrimColor = Color.TRANSPARENT;
        setting.drawerElevation = 0;
    }

    class Setting extends AdvanceDrawerLayout.Setting {
        float degree;
    }
}
