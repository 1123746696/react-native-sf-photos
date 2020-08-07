package com.sf.sfimagepicker;

import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.sf.sfimagepicker.utils.Util;

public class SFBaseActivity extends AppCompatActivity {
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (Util.isFastDoubleClick()) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
