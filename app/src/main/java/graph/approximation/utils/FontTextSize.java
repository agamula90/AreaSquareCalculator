package graph.approximation.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({FontTextSize.HEADER_TITLE_SIZE, FontTextSize.BIG_TEXT_SIZE, FontTextSize.MEDIUM_TEXT_SIZE, FontTextSize.NORMAL_TEXT_SIZE})
public @interface FontTextSize {
    int HEADER_TITLE_SIZE = 30;
    int BIG_TEXT_SIZE = 24;
    int MEDIUM_TEXT_SIZE = 18;
    int NORMAL_TEXT_SIZE = 14;
}
