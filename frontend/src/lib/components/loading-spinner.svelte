<script lang="ts">
	interface Props {
		size?: 'sm' | 'md' | 'lg';
		message?: string;
	}

	let { size = 'md', message }: Props = $props();

	const sizeClasses = {
		sm: 'h-8 w-8',
		md: 'h-16 w-16',
		lg: 'h-24 w-24'
	};
</script>

<div class="flex flex-col items-center justify-center">
	<div class="loading-spinner rounded-full {sizeClasses[size]} mb-4"></div>
	{#if message}
		<p class="text-cyan-400 text-xl data-readout tracking-wider">{message}</p>
	{/if}
	<div class="mt-4 flex gap-1">
		<span class="w-2 h-2 bg-cyan-400 rounded-full animate-ping" style="animation-delay: 0s;"
		></span>
		<span class="w-2 h-2 bg-cyan-400 rounded-full animate-ping" style="animation-delay: 0.2s;"
		></span>
		<span class="w-2 h-2 bg-cyan-400 rounded-full animate-ping" style="animation-delay: 0.4s;"
		></span>
	</div>
</div>

<style>
	.loading-spinner {
		border: 3px solid rgba(0, 255, 255, 0.1);
		border-top: 3px solid rgba(0, 255, 255, 0.8);
		animation: spin 1s linear infinite;
	}

	@keyframes spin {
		0% {
			transform: rotate(0deg);
		}
		100% {
			transform: rotate(360deg);
		}
	}

	.data-readout {
		font-family: 'Courier New', monospace;
		letter-spacing: 0.05em;
	}
</style>
