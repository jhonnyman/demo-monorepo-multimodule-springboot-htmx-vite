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
import fg from 'fast-glob';
import { relative, resolve } from 'path';
import { defineConfig } from 'vite';
import compression from 'vite-plugin-compression';
import { viteStaticCopy } from 'vite-plugin-static-copy';

// Dynamically find all .ts and .js files in src/ recursively
const srcDir = resolve(__dirname, 'src');
const files = fg.sync(['**/*.ts', '**/*.js'], { cwd: srcDir, absolute: true });

const entries = files.reduce((acc, file) => {
    // Use relative path (without extension) as entry name, e.g. 'routes/about'
    const rel = relative(srcDir, file).replace(/\.(ts|js)$/, '');
    acc[rel] = file;
    return acc;
}, {});

export default defineConfig({
    build: {
        outDir: resolve(__dirname, '../resources/static'),
        emptyOutDir: true,
        manifest: true,
        rollupOptions: {
            input: entries,
            treeshake: true,
        },
        minify: 'esbuild',
        sourcemap: true,
        assetsDir: '.',
        copyPublicDir: true,
        cssMinify: 'esbuild',
    },
    plugins: [
        compression({
            algorithm: 'brotliCompress',
            ext: '.br',
            deleteOriginFile: false,
            threshold: 10240,
            filter: /\.(js|css|html|svg)$/i,
        }),
        viteStaticCopy({
            targets: [
                {
                    src: resolve(__dirname, '../../../../node_modules', '@picocss/pico/css/pico.min.css'),
                    dest: 'css/vendors',
                },
            ],
        }),
    ],
});
