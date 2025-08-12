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
import { getCsrfToken } from './csrf';
import { EnvironmentVariables } from './env';
import { LogDataBody, TLevelValues } from './types/logger';

export type SimpleMetadata = Pick<LogDataBody, 'error' | 'userId'>;

export const LevelValues: TLevelValues = {
    ERROR: 200,
    WARN: 300,
    INFO: 400,
    DEBUG: 500,
};

const loggerEnabled = Boolean(EnvironmentVariables.LoggingUrl);

const log = (data: LogDataBody) => {
    if (loggerEnabled && LevelValues[data.level] <= LevelValues[EnvironmentVariables.LoggingMinimumLevel]) {
        const csrfToken = getCsrfToken();
        const headers: HeadersInit = {
            'Content-Type': 'application/json',
            Accept: 'application/json',
        };

        if (csrfToken.header && csrfToken.token) {
            headers[csrfToken.header] = csrfToken.token;
        }

        fetch(EnvironmentVariables.LoggingUrl, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(data),
        })
            .then((res) => {
                if (res.ok) {
                    return;
                }

                switch (res.status) {
                    // case 401:
                    //     window.location.href = '/login';
                    //     return res.json().then((body) => {
                    //         if (body?.redirect) {
                    //             window.location.href = body.redirect;
                    //         }
                    //     });

                    default:
                        break;
                }
            })
            .catch((e) => {
                console.error(`Logger: Failed to connect`, e);
            });
    }
};

export const Logger = {
    info: (message: string, metadata?: SimpleMetadata) =>
        log({
            level: 'INFO',
            message,
            ...metadata,
            app: 'demo-app-frontend',
        }),
    warn: (message: string, metadata?: SimpleMetadata) =>
        log({
            level: 'WARN',
            message,
            ...metadata,
            app: 'demo-app-frontend',
        }),
    error: (message: string, metadata?: SimpleMetadata) =>
        log({
            level: 'ERROR',
            message,
            ...metadata,
            app: 'demo-app-frontend',
        }),
    debug: (message: string, metadata?: SimpleMetadata) =>
        log({
            level: 'DEBUG',
            message,
            ...metadata,
            app: 'demo-app-frontend',
        }),
    log: log,
};

export default Logger;
