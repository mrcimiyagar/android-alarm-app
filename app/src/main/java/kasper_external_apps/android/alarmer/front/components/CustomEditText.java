package kasper_external_apps.android.alarmer.front.components;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

public class CustomEditText extends AppCompatEditText {

    public CustomEditText(Context context) {
        super(context);
        this.init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        this.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "fonts/b_koodak.ttf"));
    }
}