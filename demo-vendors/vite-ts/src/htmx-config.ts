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
import htmx from 'htmx.org';
import { getCsrfToken } from './csrf';
import Logger from './logger';
import { HtmxConfigRequestEvent } from './types/htmx';

export function configureHTMX() {
    document.body.addEventListener('htmx:configRequest', (e) => {
        const configEvent = e as HtmxConfigRequestEvent;
        const htmlToken = getCsrfToken();

        if (htmlToken.header && htmlToken.token) {
            configEvent.detail.headers[htmlToken.header] = htmlToken.token;
        }
    });
}

export function enableDebug() {
    import('htmx-ext-debug').then((e) => {
        Logger.log({
            app: 'infrastructure',
            level: 'INFO',
            message: 'HTMX debug enabled',
        });
    });
}

// Assign globally so consumers can do `window.htmx`
if (typeof window !== 'undefined') {
    (window as any).htmx = htmx;
}

export { htmx }; // optional: still allow local imports
