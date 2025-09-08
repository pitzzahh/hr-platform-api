import adapter from '@sveltejs/adapter-static';
import { vitePreprocess } from '@sveltejs/vite-plugin-svelte';

/** @type {import('@sveltejs/kit').Config} */
const config = {
    preprocess: vitePreprocess(),
    kit: {
        embedded: true,
        output: {
            bundleStrategy: "inline",
            assetsInlineLimit: Infinity
        },
        adapter: adapter({
            pages: '../src/main/resources/static',
            assets: '../src/main/resources/static',
            fallback: null,
        })
    },
};

export default config;