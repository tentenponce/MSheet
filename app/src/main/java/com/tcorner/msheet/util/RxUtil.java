package com.tcorner.msheet.util;

import io.reactivex.disposables.Disposable;

public class RxUtil {

    public static void unsubscribe(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
