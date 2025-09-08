import adapter from '@sveltejs/adapter-static';
import { vitePreprocess } from '@sveltejs/vite-plugin-svelte';

const config = {
    preprocess: vitePreprocess(),
    kit: {
        adapter: adapter({
            pages: '../src/main/resources/static',
            assets: '../src/main/resources/static',
            fallback: null
        })
    }
};

export default config;