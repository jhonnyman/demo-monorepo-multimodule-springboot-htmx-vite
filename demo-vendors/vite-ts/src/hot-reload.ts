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
export function enableHotReload() {
    let source = new EventSource('/sse/ping');

    source.onopen = () => {
        console.log('[SSE] Connected');
    };

    source.onerror = () => {
        console.warn('[SSE] Disconnected - will refresh on reconnect');
        // wait for Spring to come back online
        const interval = setInterval(() => {
            fetch('/sse/health')
                .then((res) => {
                    if (res.ok) {
                        console.log('[SSE] Server back - reloading');
                        clearInterval(interval);
                        location.reload();
                    }
                })
                .catch(() => {
                    // still down
                });
        }, 2000);
    };

    return source;
}
