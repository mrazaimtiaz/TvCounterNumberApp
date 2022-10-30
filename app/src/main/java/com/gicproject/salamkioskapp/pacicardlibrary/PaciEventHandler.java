package com.gicproject.salamkioskapp.pacicardlibrary;

public interface PaciEventHandler {
    void ReaderChangeEvent();

    void CardConnectionEvent(int var1);

    void CardDisconnectionEvent(int var1);
}
