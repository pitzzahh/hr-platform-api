<script lang="ts">
	import { formatDateTime } from '$lib/utils/format';
	import type { Pagination } from '$lib/types/employee';

	interface Props {
		timestamp: string;
		pagination: Pagination;
		isPolling: boolean;
	}

	let { timestamp, pagination, isPolling }: Props = $props();
</script>

<div class="relative border-b border-cyan-500/30 bg-black/40 backdrop-blur-md" id="header">
	<div class="container mx-auto px-4 sm:px-6 lg:px-8 py-8">
		<div class="text-center">
			<!-- Title with Holographic Effect -->
			<div class="inline-block mb-4">
				<h1
					class="text-5xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 via-blue-400 to-cyan-400 glow-text tracking-wider uppercase"
				>
					HR Platform
				</h1>
				<div
					class="h-1 w-full bg-gradient-to-r from-transparent via-cyan-500 to-transparent mt-2"
				></div>
			</div>

			<p class="text-xl text-cyan-300/80 mb-6 tracking-wide data-readout">
				[ EMPLOYEE MANAGEMENT SYSTEM ]
			</p>

			<!-- Status Indicators -->
			<div class="flex flex-col sm:flex-row gap-6 justify-center items-center">
				{#if timestamp}
					<div
						class="flex items-center gap-3 px-4 py-2 bg-cyan-500/10 border border-cyan-500/30 rounded-lg"
					>
						<div class="relative">
							<div
								class="w-2 h-2 bg-cyan-400 rounded-full {isPolling ? 'animate-ping' : ''} absolute"
							></div>
							<div class="w-2 h-2 rounded-full {isPolling ? 'bg-cyan-400' : 'bg-slate-500'}"></div>
						</div>
						<div class="text-cyan-300 text-sm data-readout">
							<span class="text-cyan-500 font-semibold">SYNC:</span>
							{formatDateTime(new Date(timestamp))}
						</div>
					</div>
				{/if}

				<div
					class="flex items-center gap-3 px-4 py-2 bg-blue-500/10 border border-blue-500/30 rounded-lg"
				>
					<svg class="w-5 h-5 text-blue-400" fill="currentColor" viewBox="0 0 20 20">
						<path
							d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z"
						/>
					</svg>
					<div class="text-blue-300 text-sm data-readout">
						<span class="text-blue-500 font-semibold">PERSONNEL:</span>
						{pagination.totalElements}
					</div>
				</div>

				<div
					class="flex items-center gap-3 px-4 py-2 bg-purple-500/10 border border-purple-500/30 rounded-lg"
				>
					<div class="relative">
						<div
							class="w-2 h-2 rounded-full {isPolling ? 'bg-emerald-400 animate-pulse' : 'bg-slate-500'}"
						></div>
					</div>
					<div class="text-purple-300 text-sm data-readout">
						<span class="text-purple-500 font-semibold">STATUS:</span>
						{isPolling ? 'ACTIVE' : 'STANDBY'}
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<style>
	.glow-text {
		text-shadow:
			0 0 10px rgba(0, 255, 255, 0.8),
			0 0 20px rgba(0, 255, 255, 0.4),
			0 0 30px rgba(0, 255, 255, 0.2);
	}

	.data-readout {
		font-family: 'Courier New', monospace;
		letter-spacing: 0.05em;
	}
</style>
