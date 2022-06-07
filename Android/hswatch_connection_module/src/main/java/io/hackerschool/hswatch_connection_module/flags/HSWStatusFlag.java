package io.hackerschool.hswatch_connection_module.flags;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

public class HSWStatusFlag {

    private int currentStatus = 0;
    public HSWStatusFlag(@StatusFlag int statusFlag) {
        this.currentStatus = statusFlag;
    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(@StatusFlag int currentStatus) {
        this.currentStatus = currentStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HSWStatusFlag that = (HSWStatusFlag) o;
        return currentStatus == that.currentStatus;
    }

    @IntDef({NULL_STATE, STATE_CONNECTING, STATE_CONNECTED, OUT_OF_RANGE, RECONNECTING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StatusFlag {}

    public static final int NULL_STATE = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int OUT_OF_RANGE = 3;
    public static final int RECONNECTING = 4;
}
