<script module lang="ts">
	type Employee = {
		id: string;
		employeeNumber: string;
		itemNumber: string;
		firstName: string;
		middleName: string | null;
		lastName: string;
		photo: string | null;
		dateOfBirth: string;
		email: string;
		phoneNumber: string | null;
		gender: 'MALE' | 'FEMALE' | 'OTHER';
		taxPayerIdentificationNumber: string;
		civilStatus: 'SINGLE' | 'MARRIED' | 'WIDOWED';
		bankAccountNumber: string | null;
		archived: boolean;
		userId: string | null;
		createdAt: string;
		updatedAt: string;
	};

	type ApiResponse = {
		timestamp: string;
		data: Employee[];
		pagination: {
			page: number;
			size: number;
			totalElements: number;
			totalPages: number;
		};
	};

	class ApiPoller {
		state = $state({
			response: {
				timestamp: '',
				data: [],
				pagination: {
					page: 1,
					size: 10,
					totalElements: 0,
					totalPages: 0
				}
			} as ApiResponse,
			page: 1,
			size: 10,
			isPolling: false,
			isLoading: false
		});

		private intervalId: number | null = null;
		private readonly intervalMs: number;
		private readonly apiBase: string;

		constructor(intervalMs: number = 1500) {
			this.intervalMs = intervalMs;
			this.apiBase = import.meta.env.VITE_API_BASE || 'http://localhost:8080';
		}

		async fetchData() {
			try {
				this.state.isLoading = true;
				console.log('Fetching data from API...');
				const res = await fetch(
					`${this.apiBase}/api/v1/employees?page=${this.state.page}&size=${this.state.size}`
				);
				const apiResponse = (await res.json()) as ApiResponse;
				this.state.response = apiResponse;
			} catch (error) {
				console.error('API fetch error:', error);
			} finally {
				this.state.isLoading = false;
			}
		}

		setPage(newPage: number) {
			this.state.page = newPage;
			this.fetchData();
		}

		start() {
			if (this.state.isPolling) return;
			this.state.isPolling = true;
			this.fetchData();
			this.intervalId = setInterval(() => this.fetchData(), this.intervalMs);
		}

		stop() {
			console.log('Stopping poller', this.intervalId);
			if (this.intervalId) {
				clearInterval(this.intervalId);
				this.intervalId = null;
			}
			this.state.isPolling = false;
		}
	}

	const poller = new ApiPoller(5000);
</script>

<script lang="ts">
	import { formatDate, formatDateTime } from '$lib/utils/format';
	import { onMount } from 'svelte';

	onMount(() => {
		console.log('Mounting component, starting poller');
		poller.start();
		return () => poller.stop();
	});

	function getFullName(employee: Employee): string {
		return [employee.firstName, employee.middleName, employee.lastName]
			.filter(Boolean)
			.join(' ')
			.trim()
			.replace(/\s+/g, ' ');
	}

	function getInitials(employee: Employee): string {
		const first = employee.firstName.charAt(0);
		const last = employee.lastName.charAt(0);
		return `${first}${last}`.toUpperCase();
	}

	function getGenderColor(gender: string): string {
		switch (gender) {
			case 'MALE':
				return 'bg-cyan-500/20 text-cyan-300 border-cyan-500/50';
			case 'FEMALE':
				return 'bg-pink-500/20 text-pink-300 border-pink-500/50';
			default:
				return 'bg-purple-500/20 text-purple-300 border-purple-500/50';
		}
	}

	function getCivilStatusColor(status: string): string {
		switch (status) {
			case 'SINGLE':
				return 'bg-slate-500/20 text-slate-300 border-slate-500/50';
			case 'MARRIED':
				return 'bg-emerald-500/20 text-emerald-300 border-emerald-500/50';
			case 'WIDOWED':
				return 'bg-amber-500/20 text-amber-300 border-amber-500/50';
			default:
				return 'bg-slate-500/20 text-slate-300 border-slate-500/50';
		}
	}
</script>

<svelte:head>
	<title>HR Platform - Employees</title>
	<meta name="description" content="View and manage employees for HR platform" />
</svelte:head>

<div
	class="min-h-screen bg-gradient-to-br from-slate-950 via-blue-950 to-slate-900 relative overflow-hidden"
>
	<!-- Animated Background Grid -->
	<div class="cyber-grid absolute inset-0 opacity-30"></div>

	<!-- Scan Line Effect -->
	<div class="scan-line absolute inset-x-0 pointer-events-none"></div>

	<!-- Header Section -->
	<div class="relative border-b border-cyan-500/30 bg-black/40 backdrop-blur-md">
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
					{#if poller.state.response.timestamp}
						<div
							class="flex items-center gap-3 px-4 py-2 bg-cyan-500/10 border border-cyan-500/30 rounded-lg"
						>
							<div class="relative">
								<div
									class="w-2 h-2 bg-cyan-400 rounded-full {poller.state.isPolling
										? 'animate-ping'
										: ''} absolute"
								></div>
								<div
									class="w-2 h-2 rounded-full {poller.state.isPolling
										? 'bg-cyan-400'
										: 'bg-slate-500'}"
								></div>
							</div>
							<div class="text-cyan-300 text-sm data-readout">
								<span class="text-cyan-500 font-semibold">SYNC:</span>
								{formatDateTime(new Date(poller.state.response.timestamp))}
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
							{poller.state.response.pagination.totalElements}
						</div>
					</div>

					<div
						class="flex items-center gap-3 px-4 py-2 bg-purple-500/10 border border-purple-500/30 rounded-lg"
					>
						<div class="relative">
							<div
								class="w-2 h-2 rounded-full {poller.state.isPolling
									? 'bg-emerald-400 animate-pulse'
									: 'bg-slate-500'}"
							></div>
						</div>
						<div class="text-purple-300 text-sm data-readout">
							<span class="text-purple-500 font-semibold">STATUS:</span>
							{poller.state.isPolling ? 'ACTIVE' : 'STANDBY'}
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Main Content -->
	<div class="container mx-auto px-4 sm:px-6 lg:px-8 py-12 relative z-10">
		{#if poller.state.response.data.length === 0}
			<div class="flex flex-col items-center justify-center py-32">
				<div class="loading-spinner rounded-full h-16 w-16 mb-6"></div>
				<p class="text-cyan-400 text-xl data-readout tracking-wider">
					[ INITIALIZING DATABASE ACCESS ]
				</p>
				<div class="mt-4 flex gap-1">
					<span class="w-2 h-2 bg-cyan-400 rounded-full animate-ping" style="animation-delay: 0s;"
					></span>
					<span class="w-2 h-2 bg-cyan-400 rounded-full animate-ping" style="animation-delay: 0.2s;"
					></span>
					<span class="w-2 h-2 bg-cyan-400 rounded-full animate-ping" style="animation-delay: 0.4s;"
					></span>
				</div>
			</div>
		{:else}
			<div class="grid gap-8 md:grid-cols-2 lg:grid-cols-3">
				{#each poller.state.response.data as employee, index}
					<div
						class="hologram-card rounded-lg overflow-hidden neon-border corner-accent transform hover:scale-105 transition-all duration-300"
						style="animation-delay: {index * 0.1}s;"
					>
						<!-- Header with Holographic Avatar -->
						<div
							class="relative bg-gradient-to-br from-cyan-900/50 to-blue-900/50 px-6 py-6 border-b border-cyan-500/30"
						>
							<div class="flex items-center gap-4">
								<div class="relative">
									{#if employee.photo}
										<div class="relative">
											<img
												src={employee.photo}
												alt={getFullName(employee)}
												class="w-20 h-20 rounded-lg border-2 border-cyan-500/50 object-cover shadow-lg shadow-cyan-500/20"
											/>
											<div
												class="absolute inset-0 bg-cyan-400/10 rounded-lg mix-blend-overlay"
											></div>
										</div>
									{:else}
										<div
											class="w-20 h-20 rounded-lg border-2 border-cyan-500/50 bg-gradient-to-br from-cyan-500/20 to-blue-500/20 flex items-center justify-center text-cyan-300 font-bold text-2xl shadow-lg shadow-cyan-500/20 backdrop-blur-sm"
										>
											{getInitials(employee)}
										</div>
									{/if}
									<div
										class="absolute -top-1 -right-1 w-3 h-3 bg-cyan-400 rounded-full animate-pulse"
									></div>
								</div>

								<div class="flex-1 min-w-0">
									<h2 class="text-lg font-bold text-cyan-100 truncate tracking-wide">
										{getFullName(employee)}
									</h2>
									<div class="flex items-center gap-2 mt-1">
										<span class="text-cyan-500 text-xs font-mono">#</span>
										<span class="text-cyan-400/80 text-sm font-mono">{employee.employeeNumber}</span
										>
									</div>
								</div>
							</div>
						</div>

						<!-- Card Content -->
						<div class="p-6 space-y-4">
							<!-- Contact Information -->
							<div class="space-y-3">
								<div
									class="flex items-center gap-3 text-sm bg-slate-800/40 rounded px-3 py-2 border border-slate-700/50"
								>
									<svg
										class="w-4 h-4 text-cyan-400 flex-shrink-0"
										fill="none"
										stroke="currentColor"
										viewBox="0 0 24 24"
									>
										<path
											stroke-linecap="round"
											stroke-linejoin="round"
											stroke-width="2"
											d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
										/>
									</svg>
									<span class="text-cyan-100/70 truncate font-mono text-xs">{employee.email}</span>
								</div>

								{#if employee.phoneNumber}
									<div
										class="flex items-center gap-3 text-sm bg-slate-800/40 rounded px-3 py-2 border border-slate-700/50"
									>
										<svg
											class="w-4 h-4 text-cyan-400 flex-shrink-0"
											fill="none"
											stroke="currentColor"
											viewBox="0 0 24 24"
										>
											<path
												stroke-linecap="round"
												stroke-linejoin="round"
												stroke-width="2"
												d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"
											/>
										</svg>
										<span class="text-cyan-100/70 font-mono text-xs">{employee.phoneNumber}</span>
									</div>
								{/if}
							</div>

							<!-- Data Grid -->
							<div class="grid grid-cols-2 gap-3 pt-3">
								<div
									class="bg-slate-800/40 rounded border border-slate-700/50 p-3 hover:border-cyan-500/30 transition-colors"
								>
									<p
										class="text-[10px] text-cyan-500/70 mb-1 uppercase tracking-wider font-semibold"
									>
										DOB
									</p>
									<p class="text-xs text-cyan-100 font-mono">
										{formatDate(employee.dateOfBirth)}
									</p>
								</div>
								<div
									class="bg-slate-800/40 rounded border border-slate-700/50 p-3 hover:border-cyan-500/30 transition-colors"
								>
									<p
										class="text-[10px] text-cyan-500/70 mb-1 uppercase tracking-wider font-semibold"
									>
										ITEM NO
									</p>
									<p class="text-xs text-cyan-100 font-mono">{employee.itemNumber}</p>
								</div>
							</div>

							<!-- Status Indicators -->
							<div class="flex flex-wrap gap-2 pt-3">
								<span
									class="{getGenderColor(
										employee.gender
									)} text-[10px] font-semibold px-3 py-1.5 rounded border uppercase tracking-wider"
								>
									{employee.gender}
								</span>
								<span
									class="{getCivilStatusColor(
										employee.civilStatus
									)} text-[10px] font-semibold px-3 py-1.5 rounded border uppercase tracking-wider"
								>
									{employee.civilStatus}
								</span>
								{#if employee.archived}
									<span
										class="bg-red-500/20 text-red-300 border-red-500/50 text-[10px] font-semibold px-3 py-1.5 rounded border uppercase tracking-wider animate-pulse"
									>
										ARCHIVED
									</span>
								{/if}
							</div>

							<!-- Classified Data Section -->
							<div class="pt-3 space-y-2">
								<div
									class="text-[10px] text-cyan-500/70 uppercase tracking-wider font-semibold mb-2"
								>
									[ CLASSIFIED DATA ]
								</div>
								<div
									class="bg-slate-900/60 rounded border border-cyan-500/20 p-3 space-y-2 font-mono"
								>
									<div class="flex justify-between items-center">
										<span class="text-[10px] text-cyan-400/70">TIN:</span>
										<span class="text-xs text-cyan-200"
											>{employee.taxPayerIdentificationNumber}</span
										>
									</div>
									{#if employee.bankAccountNumber}
										<div class="flex justify-between items-center">
											<span class="text-[10px] text-cyan-400/70">BANK:</span>
											<span class="text-xs text-cyan-200">{employee.bankAccountNumber}</span>
										</div>
									{/if}
								</div>
							</div>

							<!-- Timestamps -->
							<div
								class="pt-3 border-t border-cyan-500/20 text-[10px] text-cyan-400/50 space-y-1 font-mono"
							>
								<div class="flex justify-between">
									<span>CREATED:</span>
									<span>{formatDateTime(employee.createdAt)}</span>
								</div>
								<div class="flex justify-between">
									<span>UPDATED:</span>
									<span>{formatDateTime(employee.updatedAt)}</span>
								</div>
							</div>
						</div>
					</div>
				{/each}
			</div>

			<!-- Pagination Controls -->
			{#if poller.state.response.pagination.totalPages > 1}
				<div class="mt-12 flex justify-center items-center gap-4">
					<button
						onclick={() => poller.setPage(poller.state.page - 1)}
						disabled={poller.state.page === 1 || poller.state.isLoading}
						class="px-6 py-3 bg-cyan-500/20 text-cyan-300 border border-cyan-500/50 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed hover:bg-cyan-500/30 hover:shadow-lg hover:shadow-cyan-500/20 transition-all duration-300 font-semibold uppercase tracking-wider text-sm"
					>
						← PREV
					</button>

					<div
						class="px-6 py-3 bg-slate-800/60 border border-cyan-500/30 rounded-lg text-cyan-300 font-mono"
					>
						<span class="text-cyan-500">PAGE</span>
						{poller.state.response.pagination.page}
						<span class="text-cyan-500/50">/</span>
						{poller.state.response.pagination.totalPages}
					</div>

					<button
						onclick={() => poller.setPage(poller.state.page + 1)}
						disabled={poller.state.page === poller.state.response.pagination.totalPages ||
							poller.state.isLoading}
						class="px-6 py-3 bg-cyan-500/20 text-cyan-300 border border-cyan-500/50 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed hover:bg-cyan-500/30 hover:shadow-lg hover:shadow-cyan-500/20 transition-all duration-300 font-semibold uppercase tracking-wider text-sm"
					>
						NEXT →
					</button>
				</div>
			{/if}

			<!-- System Controls -->
			<div class="mt-12 flex justify-center gap-4">
				<button
					disabled={poller.state.isPolling}
					class="px-6 py-3 bg-emerald-500/20 text-emerald-300 border border-emerald-500/50 rounded-lg hover:bg-emerald-500/30 hover:shadow-lg hover:shadow-emerald-500/20 transition-all duration-300 font-semibold uppercase tracking-wider text-sm disabled:opacity-30 disabled:cursor-not-allowed disabled:hover:bg-emerald-500/20 disabled:shadow-none"
					onclick={() => poller.start()}
				>
					<span class="inline-flex items-center gap-2">
						<span
							class="w-2 h-2 rounded-full {poller.state.isPolling
								? 'bg-emerald-400 animate-pulse'
								: 'bg-emerald-400'}"
						></span>
						{poller.state.isPolling ? 'POLLING ACTIVE' : 'START POLLING'}
					</span>
				</button>
				<button
					disabled={!poller.state.isPolling}
					class="px-6 py-3 bg-red-500/20 text-red-300 border border-red-500/50 rounded-lg hover:bg-red-500/30 hover:shadow-lg hover:shadow-red-500/20 transition-all duration-300 font-semibold uppercase tracking-wider text-sm disabled:opacity-30 disabled:cursor-not-allowed disabled:hover:bg-red-500/20 disabled:shadow-none"
					onclick={() => poller.stop()}
				>
					<span class="inline-flex items-center gap-2">
						<span
							class="w-2 h-2 rounded-full {!poller.state.isPolling ? 'bg-slate-500' : 'bg-red-400'}"
						></span>
						{poller.state.isPolling ? 'STOP POLLING' : 'POLLING STOPPED'}
					</span>
				</button>
			</div>

			<!-- Footer Terminal -->
			<div class="mt-16 max-w-2xl mx-auto">
				<div class="hologram-card rounded-lg overflow-hidden border border-cyan-500/30">
					<div class="bg-cyan-500/10 px-4 py-2 border-b border-cyan-500/30 flex items-center gap-2">
						<div class="flex gap-1.5">
							<div class="w-2.5 h-2.5 rounded-full bg-red-500/70"></div>
							<div class="w-2.5 h-2.5 rounded-full bg-yellow-500/70"></div>
							<div class="w-2.5 h-2.5 rounded-full bg-green-500/70"></div>
						</div>
						<span class="text-cyan-400/70 text-xs font-mono">system.terminal</span>
					</div>
					<div class="p-6 space-y-2 font-mono text-sm">
						<p class="text-cyan-400/70">
							<span class="text-cyan-500">$</span> System Status:
							<span class="text-emerald-400">OPERATIONAL</span>
						</p>
						<p class="text-cyan-400/70">
							<span class="text-cyan-500">$</span> Platform:
							<span class="text-cyan-300">HR Management Interface v2.0</span>
						</p>
						<p class="text-cyan-400/70">
							<span class="text-cyan-500">$</span> Real-time personnel tracking and administration
						</p>
						<p class="text-cyan-400/50 text-xs mt-4">
							© 2025 HR Platform API - All Rights Reserved
						</p>
					</div>
				</div>
			</div>
		{/if}
	</div>
</div>

<style>
	@keyframes scan-line {
		0% {
			transform: translateY(-100%);
		}
		100% {
			transform: translateY(100vh);
		}
	}

	@keyframes pulse-glow {
		0%,
		100% {
			opacity: 1;
		}
		50% {
			opacity: 0.5;
		}
	}

	@keyframes grid-move {
		0% {
			background-position: 0 0;
		}
		100% {
			background-position: 40px 40px;
		}
	}

	@keyframes flicker {
		0%,
		100% {
			opacity: 1;
		}
		50% {
			opacity: 0.8;
		}
	}

	.cyber-grid {
		background-image:
			linear-gradient(rgba(0, 255, 255, 0.1) 1px, transparent 1px),
			linear-gradient(90deg, rgba(0, 255, 255, 0.1) 1px, transparent 1px);
		background-size: 40px 40px;
		animation: grid-move 2s linear infinite;
	}

	.scan-line {
		animation: scan-line 8s linear infinite;
		background: linear-gradient(
			to bottom,
			transparent,
			rgba(0, 255, 255, 0.1),
			rgba(0, 255, 255, 0.3),
			rgba(0, 255, 255, 0.1),
			transparent
		);
		height: 200px;
	}

	.glow-text {
		text-shadow:
			0 0 10px rgba(0, 255, 255, 0.8),
			0 0 20px rgba(0, 255, 255, 0.4),
			0 0 30px rgba(0, 255, 255, 0.2);
	}

	.glow-border {
		box-shadow:
			0 0 20px rgba(0, 255, 255, 0.3),
			inset 0 0 20px rgba(0, 255, 255, 0.1);
	}

	.hologram-card {
		position: relative;
		background: linear-gradient(135deg, rgba(10, 25, 47, 0.9) 0%, rgba(20, 35, 57, 0.9) 100%);
		backdrop-filter: blur(10px);
		border: 1px solid rgba(0, 255, 255, 0.3);
		box-shadow:
			0 8px 32px rgba(0, 0, 0, 0.4),
			0 0 40px rgba(0, 255, 255, 0.1);
	}

	.hologram-card::before {
		content: '';
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: linear-gradient(
			45deg,
			transparent 30%,
			rgba(0, 255, 255, 0.05) 50%,
			transparent 70%
		);
		animation: flicker 3s ease-in-out infinite;
		pointer-events: none;
	}

	.neon-border {
		position: relative;
		border: 1px solid rgba(0, 255, 255, 0.5);
		box-shadow:
			0 0 10px rgba(0, 255, 255, 0.3),
			inset 0 0 10px rgba(0, 255, 255, 0.1);
	}

	.neon-border::before {
		content: '';
		position: absolute;
		top: -2px;
		left: -2px;
		right: -2px;
		bottom: -2px;
		background: linear-gradient(45deg, #00ffff, #0080ff, #00ffff);
		border-radius: inherit;
		opacity: 0;
		z-index: -1;
		transition: opacity 0.3s;
	}

	.neon-border:hover::before {
		opacity: 0.3;
		animation: pulse-glow 2s ease-in-out infinite;
	}

	.data-readout {
		font-family: 'Courier New', monospace;
		letter-spacing: 0.05em;
	}

	.corner-accent {
		position: relative;
	}

	.corner-accent::before,
	.corner-accent::after {
		content: '';
		position: absolute;
		width: 20px;
		height: 20px;
		border-color: rgba(0, 255, 255, 0.6);
	}

	.corner-accent::before {
		top: -1px;
		left: -1px;
		border-top: 2px solid;
		border-left: 2px solid;
	}

	.corner-accent::after {
		bottom: -1px;
		right: -1px;
		border-bottom: 2px solid;
		border-right: 2px solid;
	}

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
</style>
