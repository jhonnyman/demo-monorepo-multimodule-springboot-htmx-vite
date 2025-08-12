/*
 * Copyright 2025 jhonnyman. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import Logger from './logger';

export const onWindowErrorHandler: OnErrorEventHandlerNonNull = (eventOrMessage, source, lineno, colno, error) => {
    let eventMessage = '';
    if (eventOrMessage instanceof Event) {
        eventMessage = JSON.stringify(eventOrMessage);
    } else {
        eventMessage = eventOrMessage;
    }

    Logger.error(`Global error caught: ${eventMessage}`, {
        error: {
            message: eventMessage,
            source: source,
            lineno: lineno,
            colno: colno,
        },
    });
};

export const autoConfiguration = () => {
    window.onerror = onWindowErrorHandler;
};

//  Auto configures the environment on script load
autoConfiguration();
