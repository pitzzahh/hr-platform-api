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

<header
	class="relative border-b border-blue-200 dark:border-cyan-500/30 bg-white dark:bg-slate-900"
	id="system-header"
>
	<div class="container mx-auto px-4 sm:px-6 lg:px-8 py-8">
		<div class="text-center space-y-6">
			<!-- Title -->
			<h1 class="text-5xl font-bold text-cyan-500 dark:text-cyan-400 tracking-wider uppercase">
				HR PLATFORM
			</h1>

			<!-- Subtitle -->
			<p class="text-lg text-blue-600 dark:text-cyan-400 tracking-wide font-mono">
				[ EMPLOYEE MANAGEMENT SYSTEM ]
			</p>

			<!-- Status Indicators -->
			<div class="flex flex-col sm:flex-row gap-4 justify-center items-center" role="status">
				{#if timestamp}
					<div
						class="flex items-center gap-3 px-4 py-2 bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-500/30 shadow-sm"
					>
						<div class="relative w-2 h-2">
							{#if isPolling}
								<div
									class="w-2 h-2 bg-blue-500 dark:bg-cyan-400 animate-ping absolute"
									aria-hidden="true"
								></div>
							{/if}
							<div
								class="w-2 h-2 {isPolling
									? 'bg-blue-600 dark:bg-cyan-400'
									: 'bg-slate-400 dark:bg-slate-500'}"
							></div>
						</div>
						<div class="text-blue-700 dark:text-cyan-300 text-sm font-mono">
							<span class="font-semibold">SYNC:</span>
							{formatDateTime(new Date(timestamp))}
						</div>
					</div>
				{/if}

				<div
					class="flex items-center gap-3 px-4 py-2 bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-500/30 shadow-sm"
				>
					<svg
						class="w-5 h-5 text-blue-600 dark:text-blue-400 flex-shrink-0"
						fill="currentColor"
						viewBox="0 0 20 20"
						aria-hidden="true"
					>
						<path
							d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z"
						/>
					</svg>
					<div class="text-blue-700 dark:text-blue-300 text-sm font-mono">
						<span class="font-semibold">PERSONNEL:</span>
						{pagination.totalElements}
					</div>
				</div>

				<div
					class="flex items-center gap-3 px-4 py-2 shadow-sm border {isPolling
						? 'bg-green-50 dark:bg-green-900/20 border-green-200 dark:border-green-500/30'
						: 'bg-slate-50 dark:bg-slate-800/20 border-slate-200 dark:border-slate-600/30'}"
				>
					<div class="relative w-2 h-2">
						<div
							class="w-2 h-2 {isPolling
								? 'bg-green-600 dark:bg-green-400 animate-pulse'
								: 'bg-slate-400 dark:bg-slate-500'}"
							aria-hidden="true"
						></div>
					</div>
					<div
						class="text-sm font-mono {isPolling
							? 'text-green-700 dark:text-green-300'
							: 'text-slate-700 dark:text-slate-400'}"
					>
						<span class="font-semibold">STATUS:</span>
						{isPolling ? 'ACTIVE' : 'STANDBY'}
					</div>
				</div>
			</div>
		</div>
	</div>
</header>
