import adapter from '@sveltejs/adapter-static';
import { vitePreprocess } from '@sveltejs/vite-plugin-svelte';

/** @type {import('@sveltejs/kit').Config} */
const config = {
    preprocess: vitePreprocess(),
    build: {
        // inline all imported assets
        assetsInlineLimit: Infinity
    },
    kit: {
        output: {
            bundleStrategy: "inline"
        },
        adapter: adapter({
            pages: '../src/main/resources/static',
            assets: '../src/main/resources/static'
        })
    },
};

export default config;