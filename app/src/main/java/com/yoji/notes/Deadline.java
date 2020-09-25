package com.yoji.notes;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({Deadline.CLOSE, Deadline.PASSED, Deadline.OK})
public @interface Deadline {
    int OK = 0;
    int CLOSE = 1;
    int PASSED = 2;
}