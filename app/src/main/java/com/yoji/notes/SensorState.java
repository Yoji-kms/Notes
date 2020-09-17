package com.yoji.notes;

public enum SensorState {
    NOT_SUPPORTED, //функция не поддерживается
    NOT_BLOCKED, //устройство не защищено пином, рисунком или паролем
    NO_FINGERPRINTS, //на устройстве нет отпечатков
    READY
}
