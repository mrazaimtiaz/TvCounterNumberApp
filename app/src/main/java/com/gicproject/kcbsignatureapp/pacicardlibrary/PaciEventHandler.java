package com.gicproject.kcbsignatureapp.pacicardlibrary;

public interface PaciEventHandler {
    void ReaderChangeEvent();

    void CardConnectionEvent(int var1);

    void CardDisconnectionEvent(int var1);
}
