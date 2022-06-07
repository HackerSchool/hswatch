package io.hackerschool.hswatch_connection_module.connection_objects;

import android.app.Notification;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public interface IHSWNotificationFilter {
    List<String> notificationFilter(Notification notification);
}
