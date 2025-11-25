<script lang="ts">
	import { formatDate, formatDateTime } from '$lib/utils/format';
	import type { Employee } from '$lib/types/employee';

	interface Props {
		employee: Employee;
		index: number;
		onEdit: (employee: Employee) => void;
		onDelete: (employee: Employee) => void;
		onView: (employee: Employee) => void;
	}

	let { employee, index, onEdit, onDelete, onView }: Props = $props();

	function getFullName(emp: Employee): string {
		return [emp.firstName, emp.middleName, emp.lastName]
			.filter(Boolean)
			.join(' ')
			.trim()
			.replace(/\s+/g, ' ');
	}

	function getInitials(emp: Employee): string {
		const first = emp.firstName.charAt(0);
		const last = emp.lastName.charAt(0);
		return `${first}${last}`.toUpperCase();
	}

	function getGenderColor(gender: string): string {
		switch (gender) {
			case 'MALE':
				return 'bg-blue-100/90 dark:bg-cyan-500/20 text-blue-700 dark:text-cyan-300 border-blue-300 dark:border-cyan-500/50';
			case 'FEMALE':
				return 'bg-pink-100/90 dark:bg-pink-500/20 text-pink-700 dark:text-pink-300 border-pink-300 dark:border-pink-500/50';
			default:
				return 'bg-purple-100/90 dark:bg-purple-500/20 text-purple-700 dark:text-purple-300 border-purple-300 dark:border-purple-500/50';
		}
	}

	function getCivilStatusColor(status: string): string {
		switch (status) {
			case 'SINGLE':
				return 'bg-slate-100/90 dark:bg-slate-500/20 text-slate-700 dark:text-slate-300 border-slate-300 dark:border-slate-500/50';
			case 'MARRIED':
				return 'bg-emerald-100/90 dark:bg-emerald-500/20 text-emerald-700 dark:text-emerald-300 border-emerald-300 dark:border-emerald-500/50';
			case 'WIDOWED':
				return 'bg-amber-100/90 dark:bg-amber-500/20 text-amber-700 dark:text-amber-300 border-amber-300 dark:border-amber-500/50';
			default:
				return 'bg-slate-100/90 dark:bg-slate-500/20 text-slate-700 dark:text-slate-300 border-slate-300 dark:border-slate-500/50';
		}
	}
</script>

<article
	class="hologram-card overflow-hidden neon-border corner-accent transform hover:scale-105 transition-all duration-300"
	style="animation-delay: {index * 0.1}s;"
>
	<!-- Header with Avatar -->
	<header
		class="relative bg-gradient-to-br from-blue-50 to-cyan-50 dark:from-slate-950 dark:to-slate-900 px-6 py-6 border-b border-blue-300/50 dark:border-cyan-500/30"
	>
		<div class="flex items-center gap-4">
			<div class="relative">
				{#if employee.photo}
					<div class="relative">
						<img
							src={employee.photo}
							alt={getFullName(employee)}
							class="w-20 h-20 border-2 border-blue-400 dark:border-cyan-500/50 object-cover shadow-lg shadow-blue-500/20 dark:shadow-cyan-500/20"
						/>
						<div
							class="absolute inset-0 bg-cyan-400/10 mix-blend-overlay"
							aria-hidden="true"
						></div>
					</div>
				{:else}
					<div
						class="w-20 h-20 border-2 border-blue-400 dark:border-cyan-500/50 bg-gradient-to-br from-blue-200 to-cyan-200 dark:from-slate-900 dark:to-slate-800 flex items-center justify-center text-blue-700 dark:text-cyan-300 font-bold text-2xl shadow-lg shadow-blue-500/20 dark:shadow-cyan-500/20 backdrop-blur-sm"
					>
						{getInitials(employee)}
					</div>
				{/if}
				<div
					class="absolute -top-1 -right-1 w-3 h-3 bg-blue-500 dark:bg-cyan-400 animate-pulse shadow-lg shadow-blue-500/50 dark:shadow-cyan-400/50"
					aria-hidden="true"
				></div>
			</div>

			<div class="flex-1 min-w-0">
				<h2 class="text-lg font-bold text-slate-900 dark:text-cyan-100 truncate tracking-wide">
					{getFullName(employee)}
				</h2>
				<div class="flex items-center gap-2 mt-1">
					<span class="text-blue-600 dark:text-cyan-500 text-xs font-mono">#</span>
					<span class="text-blue-700 dark:text-cyan-400/80 text-sm font-mono"
						>{employee.employeeNumber}</span
					>
				</div>
			</div>

			<!-- Action Buttons -->
			<div class="flex flex-col gap-2">
				<button
					type="button"
					aria-label="View {getFullName(employee)} details"
					onclick={() => onView(employee)}
					class="p-2 bg-blue-100/80 dark:bg-blue-500/20 hover:bg-blue-200 dark:hover:bg-blue-500/30 border border-blue-300 dark:border-blue-500/50 transition-all duration-300 hover:shadow-lg hover:shadow-blue-500/20 dark:hover:shadow-blue-500/30"
					title="View Details"
				>
					<svg
						class="w-4 h-4 text-blue-600 dark:text-blue-300"
						fill="none"
						stroke="currentColor"
						viewBox="0 0 24 24"
					>
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
						/>
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
						/>
					</svg>
				</button>
				<button
					type="button"
					aria-label="Edit {getFullName(employee)}"
					onclick={() => onEdit(employee)}
					class="p-2 bg-cyan-100/80 dark:bg-cyan-500/20 hover:bg-cyan-200 dark:hover:bg-cyan-500/30 border border-cyan-300 dark:border-cyan-500/50 transition-all duration-300 hover:shadow-lg hover:shadow-cyan-500/20 dark:hover:shadow-cyan-500/30"
					title="Edit Employee"
				>
					<svg
						class="w-4 h-4 text-cyan-600 dark:text-cyan-300"
						fill="none"
						stroke="currentColor"
						viewBox="0 0 24 24"
					>
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
						/>
					</svg>
				</button>
				<button
					type="button"
					aria-label="Delete {getFullName(employee)}"
					onclick={() => onDelete(employee)}
					class="p-2 bg-red-100/80 dark:bg-red-500/20 hover:bg-red-200 dark:hover:bg-red-500/30 border border-red-300 dark:border-red-500/50 transition-all duration-300 hover:shadow-lg hover:shadow-red-500/20 dark:hover:shadow-red-500/30"
					title="Delete Employee"
				>
					<svg
						class="w-4 h-4 text-red-600 dark:text-red-300"
						fill="none"
						stroke="currentColor"
						viewBox="0 0 24 24"
					>
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
						/>
					</svg>
				</button>
			</div>
		</div>
	</header>

	<!-- Card Content -->
	<div class="p-6 space-y-4">
		<!-- Contact Information -->
		<section class="space-y-3">
			<div
				class="flex items-center gap-3 text-sm bg-blue-50/80 dark:bg-slate-800/70 rounded px-3 py-2 border border-blue-200 dark:border-cyan-500/30 shadow-sm dark:shadow-cyan-500/10"
			>
				<svg
					class="w-4 h-4 text-blue-600 dark:text-cyan-400 flex-shrink-0"
					fill="none"
					stroke="currentColor"
					viewBox="0 0 24 24"
					aria-hidden="true"
				>
					<path
						stroke-linecap="round"
						stroke-linejoin="round"
						stroke-width="2"
						d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
					/>
				</svg>
				<span class="text-slate-700 dark:text-cyan-100/70 truncate font-mono text-xs"
					>{employee.email}</span
				>
			</div>

			{#if employee.phoneNumber}
				<div
					class="flex items-center gap-3 text-sm bg-blue-50/80 dark:bg-slate-800/70 rounded px-3 py-2 border border-blue-200 dark:border-cyan-500/30 shadow-sm dark:shadow-cyan-500/10"
				>
					<svg
						class="w-4 h-4 text-blue-600 dark:text-cyan-400 flex-shrink-0"
						fill="none"
						stroke="currentColor"
						viewBox="0 0 24 24"
						aria-hidden="true"
					>
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"
						/>
					</svg>
					<span class="text-slate-700 dark:text-cyan-100/70 font-mono text-xs"
						>{employee.phoneNumber}</span
					>
				</div>
			{/if}
		</section>

		<!-- Data Grid -->
		<div class="grid grid-cols-2 gap-3 pt-3">
			<div
				class="bg-blue-50/80 dark:bg-slate-800/70 rounded border border-blue-200 dark:border-cyan-500/30 p-3 hover:border-blue-400 dark:hover:border-cyan-500/50 transition-colors shadow-sm dark:shadow-cyan-500/10"
			>
				<p
					class="text-[10px] text-blue-600 dark:text-cyan-500/70 mb-1 uppercase tracking-wider font-semibold font-mono"
				>
					DOB
				</p>
				<p class="text-xs text-slate-900 dark:text-cyan-100 font-mono">
					{formatDate(employee.dateOfBirth)}
				</p>
			</div>
			<div
				class="bg-blue-50/80 dark:bg-slate-800/70 rounded border border-blue-200 dark:border-cyan-500/30 p-3 hover:border-blue-400 dark:hover:border-cyan-500/50 transition-colors shadow-sm dark:shadow-cyan-500/10"
			>
				<p
					class="text-[10px] text-blue-600 dark:text-cyan-500/70 mb-1 uppercase tracking-wider font-semibold font-mono"
				>
					ITEM NO
				</p>
				<p class="text-xs text-slate-900 dark:text-cyan-100 font-mono">{employee.itemNumber}</p>
			</div>
		</div>

		<!-- Status Indicators -->
		<div class="flex flex-wrap gap-2 pt-3">
			<span
				class="{getGenderColor(
					employee.gender
				)} text-[10px] font-semibold px-3 py-1.5 rounded border uppercase tracking-wider shadow-sm"
			>
				{employee.gender}
			</span>
			<span
				class="{getCivilStatusColor(
					employee.civilStatus
				)} text-[10px] font-semibold px-3 py-1.5 rounded border uppercase tracking-wider shadow-sm"
			>
				{employee.civilStatus}
			</span>
			{#if employee.archived}
				<span
					class="bg-red-100 dark:bg-red-500/20 text-red-700 dark:text-red-300 border-red-300 dark:border-red-500/50 text-[10px] font-semibold px-3 py-1.5 rounded border uppercase tracking-wider animate-pulse shadow-sm"
				>
					ARCHIVED
				</span>
			{/if}
		</div>

		<!-- Classified Data Section -->
		<section class="pt-3 space-y-2">
			<h3
				class="text-[10px] text-blue-600 dark:text-cyan-500/70 uppercase tracking-wider font-semibold mb-2 font-mono"
			>
				[ CLASSIFIED DATA ]
			</h3>
			<div
				class="bg-blue-50/80 dark:bg-slate-800/80 rounded border border-blue-200 dark:border-cyan-500/30 p-3 space-y-2 font-mono shadow-sm dark:shadow-cyan-500/10"
			>
				<div class="flex justify-between items-center">
					<span class="text-[10px] text-blue-600 dark:text-cyan-400/70 uppercase">TIN:</span>
					<span class="text-xs text-slate-900 dark:text-cyan-200"
						>{employee.taxPayerIdentificationNumber}</span
					>
				</div>
				{#if employee.bankAccountNumber}
					<div class="flex justify-between items-center">
						<span class="text-[10px] text-blue-600 dark:text-cyan-400/70 uppercase">BANK:</span>
						<span class="text-xs text-slate-900 dark:text-cyan-200"
							>{employee.bankAccountNumber}</span
						>
					</div>
				{/if}
			</div>
		</section>

		<!-- Timestamps -->
		<footer
			class="pt-3 border-t border-blue-200 dark:border-cyan-500/20 text-[10px] text-slate-500 dark:text-cyan-400/50 space-y-1 font-mono"
		>
			<div class="flex justify-between">
				<span>CREATED:</span>
				<span>{formatDateTime(employee.createdAt)}</span>
			</div>
			<div class="flex justify-between">
				<span>UPDATED:</span>
				<span>{formatDateTime(employee.updatedAt)}</span>
			</div>
		</footer>
	</div>
</article>

<style>
	.hologram-card {
		position: relative;
		background: linear-gradient(
			135deg,
			rgba(255, 255, 255, 0.98) 0%,
			rgba(240, 249, 255, 0.95) 100%
		);
		backdrop-filter: blur(10px);
		border: 1px solid rgba(59, 130, 246, 0.4);
		box-shadow:
			0 8px 32px rgba(0, 0, 0, 0.08),
			0 0 0 1px rgba(59, 130, 246, 0.1) inset;
	}

	@media (prefers-color-scheme: dark) {
		.hologram-card {
			background: linear-gradient(135deg, rgba(0, 5, 15, 0.98) 0%, rgba(0, 15, 25, 0.98) 100%);
			border: 1px solid rgba(6, 182, 212, 0.5);
			box-shadow:
				0 8px 32px rgba(0, 0, 0, 0.5),
				0 0 0 1px rgba(6, 182, 212, 0.1) inset;
		}
	}

	.neon-border {
		position: relative;
		border: 1px solid rgba(59, 130, 246, 0.5);
		box-shadow:
			0 0 10px rgba(59, 130, 246, 0.15),
			inset 0 0 10px rgba(59, 130, 246, 0.05);
	}

	@media (prefers-color-scheme: dark) {
		.neon-border {
			border: 1px solid rgba(6, 182, 212, 0.6);
			box-shadow:
				0 0 15px rgba(6, 182, 212, 0.4),
				inset 0 0 15px rgba(6, 182, 212, 0.15);
		}
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
		border-color: rgba(59, 130, 246, 0.6);
		transition: all 0.3s;
	}

	@media (prefers-color-scheme: dark) {
		.corner-accent::before,
		.corner-accent::after {
			border-color: rgba(6, 182, 212, 0.7);
		}
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

	.hologram-card:hover .corner-accent::before,
	.hologram-card:hover .corner-accent::after {
		width: 25px;
		height: 25px;
	}

	.hologram-card:hover {
		box-shadow:
			0 12px 40px rgba(0, 0, 0, 0.12),
			0 0 30px rgba(59, 130, 246, 0.2),
			0 0 0 1px rgba(59, 130, 246, 0.2) inset;
	}

	@media (prefers-color-scheme: dark) {
		.hologram-card:hover {
			box-shadow:
				0 12px 40px rgba(0, 0, 0, 0.5),
				0 0 50px rgba(6, 182, 212, 0.3),
				0 0 0 1px rgba(6, 182, 212, 0.2) inset;
		}
	}
</style>
