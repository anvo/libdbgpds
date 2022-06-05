/*
 * Copyright (c) 2022 anvo
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.github.anvo.libdbgpds.live;

public interface TradingActivityPublisher {

    void subscribe(TradingActivityReceiver receiver);

    void unsubscribe(TradingActivityReceiver receiver);

    void startBlocking();

    void stop();
}
