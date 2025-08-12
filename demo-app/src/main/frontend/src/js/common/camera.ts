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
import { Logger } from '@demo.com/vite-ts';

function catchAllErrors(error: unknown) {
    if (typeof error === 'string') {
        Logger.error(error, {
            error: {
                message: error,
            },
        });
        return;
    }
    if (error instanceof Error) {
        Logger.error(error.message, {
            error: {
                message: error.message,
                cause: error.cause as string,
            },
        });
        return;
    }
    if (error instanceof Object) {
        const message = JSON.stringify(error);
        Logger.error(message, {
            error: {
                message: message,
            },
        });
        return;
    }
    Logger.error(String(error), {
        error: {
            message: String(error),
        },
    });
}

const RecordingTimeSliceMilis = 6000;

const videoEl = document.querySelector('video');
const startCameraBtn = document.getElementById('start-camera');
const permissionStatus = document.getElementById('permission-status');
const permissionWrapper = document.getElementById('permission-status-wrapper');
const refreshPermissionsBtn = document.getElementById('refresh-permissions');
const audioInputs = document.getElementById('audio-inputs') as HTMLSelectElement | null;
const videoInputs = document.getElementById('video-inputs') as HTMLSelectElement | null;
const colorSchemeSwitcher = document.getElementById('color-scheme-switch');
const startRecordingBtn = document.getElementById('start-recording');
const stopRecordingBtn = document.getElementById('stop-recording');

if (colorSchemeSwitcher) {
    colorSchemeSwitcher.onclick = toggleColorTheme;
}

if (refreshPermissionsBtn) {
    refreshPermissionsBtn.onclick = checkPermissions;
}

if (startCameraBtn) {
    startCameraBtn.onclick = startCamera;
}

if (startRecordingBtn) {
    startRecordingBtn.onclick = startRecording;
}

if (stopRecordingBtn) {
    stopRecordingBtn.onclick = stopRecording;
}

function toggleColorTheme() {
    const htmlRoot = document.querySelector('html');
    if (htmlRoot?.getAttribute('data-theme') === 'dark') {
        htmlRoot.setAttribute('data-theme', 'light');
    } else {
        htmlRoot?.setAttribute('data-theme', 'dark');
    }
}

const constraints: MediaStreamConstraints = {
    audio: true,
    preferCurrentTab: true,
    video: true,
};

export async function checkPermissions() {
    try {
        const cameraPermission = await navigator.permissions.query({
            name: 'camera',
        });
        const microphonePermission = await navigator.permissions.query({
            name: 'microphone',
        });

        if (cameraPermission.state === 'denied' && permissionWrapper && permissionStatus) {
            permissionWrapper.hidden = false;
            permissionWrapper.ariaHidden = null;
            permissionStatus.innerText =
                'We require camera permission. Grant permission to use the camera and try again';

            Logger.debug('User did not grant permissions to access the camera');
        }

        if (microphonePermission.state === 'denied' && permissionWrapper && permissionStatus) {
            permissionWrapper.hidden = false;
            permissionWrapper.ariaHidden = null;
            permissionStatus.innerText =
                'We require microphone permission. Grant permission to use the camera and try again';

            Logger.debug('User did not grant permissions to access the microphone');
        }

        if (microphonePermission.state === 'granted' && cameraPermission.state === 'granted') {
            Logger.debug('All permissions granted');
        }

        if (microphonePermission.state === 'prompt' || cameraPermission.state === 'prompt') {
            Logger.warn('Cannot display any devices because they must be asked first');
            startAndRefresh();
        }
    } catch (error) {
        catchAllErrors(error);
    }
}

async function getDevices() {
    try {
        const mediaDevices = await navigator.mediaDevices.enumerateDevices();

        mediaDevices.forEach((el) => {
            const option = document.createElement('option');
            option.value = el.deviceId;
            option.id = el.deviceId;
            option.text = el.label;

            if (el.kind === 'audioinput') {
                audioInputs?.appendChild(option);
            }

            if (el.kind === 'videoinput') {
                videoInputs?.appendChild(option);
            }
        });

        console.log(mediaDevices);
        Logger.info(`Found ${mediaDevices.length} devices: ${JSON.stringify(mediaDevices)}`);
    } catch (error) {
        catchAllErrors(error);
    }
}

checkPermissions();
getDevices();

async function startAndRefresh() {
    try {
        await navigator.mediaDevices.getUserMedia(constraints);
        getDevices();
    } catch (error) {
        catchAllErrors(error);
    }

    getDevices();
}

let cameraStream: MediaStream | null = null;

async function startCamera() {
    const selectedAudio = audioInputs?.options[audioInputs.selectedIndex]?.value;
    const selectedVideo = videoInputs?.options[videoInputs.selectedIndex]?.value;

    try {
        cameraStream = await navigator.mediaDevices.getUserMedia({
            video: {
                deviceId: selectedVideo,
            },
            audio: {
                deviceId: selectedAudio,
            },
        });

        if (videoEl) {
            videoEl.srcObject = cameraStream;
        }
    } catch (error) {
        if (typeof error === 'string') {
            if (permissionWrapper && permissionStatus) {
                permissionWrapper.hidden = false;
                permissionWrapper.ariaHidden = null;
                permissionStatus.innerText = error;
            }
        } else {
            if (permissionWrapper && permissionStatus) {
                permissionWrapper.hidden = false;
                permissionWrapper.ariaHidden = null;
                permissionStatus.innerText = JSON.stringify(error);
            }
        }
        catchAllErrors(error);
    }
}

let mediaRecorder: MediaRecorder | null = null;
let recordingStarted: {
    id: string;
    userId: string;
    startDate: Date;
} | null = null;

async function startRecording() {
    if (!cameraStream) {
        alert('Camera not ready');
        Logger.debug('Camera not ready');
        return;
    }

    fetch('/api/testimony/recordings', {
        method: 'POST',
        headers: {
            ['Content-Type']: 'application/json',
        },
    })
        .then((res) => {
            if (res.ok) {
                res.json().then((res) => {
                    Logger.info(`User started recording ${res.id}`);

                    recordingStarted = res;

                    mediaRecorder = new MediaRecorder(cameraStream!!, {
                        mimeType: 'video/webm',
                    });

                    mediaRecorder.addEventListener('dataavailable', (e) => {
                        const body = new FormData();
                        body.append('file', e.data);
                        fetch(`/api/testimony/recordings/${recordingStarted?.id}/chunks`, {
                            method: 'POST',
                            body: body,
                        }).then((res) => {
                            if (res.ok) {
                            }
                            res.json().then((body) => {
                                if (res.ok) {
                                    Logger.info(`Persisted new recording chunk`);
                                } else {
                                    Logger.error(body);
                                }
                            });
                        });
                    });

                    mediaRecorder.start(RecordingTimeSliceMilis);

                    videoEl?.parentElement?.classList.add('recording');
                });
            } else {
                console.error(res.body);
                res.json().then(Logger.error).catch(Logger.error);
            }
        })
        .catch((err) => {
            catchAllErrors(err);
        });
}

async function stopRecording() {
    mediaRecorder?.stop();
    videoEl?.parentElement?.classList.remove('recording');
    fetch(`/api/testimony/recordings/${recordingStarted?.id}/process`, {
        method: 'POST',
    })
        .then((res) => {
            if (res.ok) {
                alert(`Recorded stored!`);
                recordingStarted = null;
            } else {
                alert(`Recording Failed! :(`);

                res.text().then(catchAllErrors).catch(catchAllErrors);
            }
        })
        .catch(catchAllErrors);
}

Logger.info(`User accessed the page /`);
