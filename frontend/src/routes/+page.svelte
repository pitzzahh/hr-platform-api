<script lang="ts">
	import { onMount } from 'svelte';
	import { employeeService } from '$lib/services/employee-service';
	import type { Employee, EmployeeCreateRequest, EmployeeListResponse } from '$lib/types/employee';
	import SystemHeader from '$lib/components/system-header.svelte';
	import EmployeeCard from '$lib/components/employee-card.svelte';
	import LoadingSpinner from '$lib/components/loading-spinner.svelte';
	import Pagination from '$lib/components/pagination.svelte';
	import Modal from '$lib/components/modal.svelte';
	import EmployeeForm from '$lib/components/employee-form.svelte';

	// State management using Svelte 5 runes
	let state = $state({
		employees: [] as Employee[],
		pagination: {
			page: 1,
			size: 9,
			totalElements: 0,
			totalPages: 0
		},
		timestamp: '',
		isLoading: false,
		isPolling: false,
		error: null as string | null,
		// Modal states
		isCreateModalOpen: false,
		isEditModalOpen: false,
		isViewModalOpen: false,
		isDeleteModalOpen: false,
		selectedEmployee: null as Employee | null,
		isSubmitting: false
	});

	let pollingInterval: number | null = null;

	async function fetchEmployees() {
		try {
			state.isLoading = true;
			state.error = null;
			const response: EmployeeListResponse = await employeeService.getEmployees(
				state.pagination.page,
				state.pagination.size
			);
			state.employees = response.data;
			state.pagination = response.pagination;
			state.timestamp = response.timestamp;
		} catch (err) {
			state.error = err instanceof Error ? err.message : 'Failed to fetch employees';
			console.error('Error fetching employees:', err);
		} finally {
			state.isLoading = false;
		}
	}

	function startPolling() {
		if (state.isPolling) return;
		state.isPolling = true;
		fetchEmployees();
		pollingInterval = setInterval(() => fetchEmployees(), 5000);
	}

	function stopPolling() {
		if (pollingInterval) {
			clearInterval(pollingInterval);
			pollingInterval = null;
		}
		state.isPolling = false;
	}

	function handlePageChange(newPage: number) {
		state.pagination.page = newPage;
		fetchEmployees();
	}

	// CRUD Operations
	function openCreateModal() {
		state.selectedEmployee = null;
		state.isCreateModalOpen = true;
	}

	function openEditModal(employee: Employee) {
		state.selectedEmployee = employee;
		state.isEditModalOpen = true;
	}

	function openViewModal(employee: Employee) {
		state.selectedEmployee = employee;
		state.isViewModalOpen = true;
	}

	function openDeleteModal(employee: Employee) {
		state.selectedEmployee = employee;
		state.isDeleteModalOpen = true;
	}

	function closeAllModals() {
		state.isCreateModalOpen = false;
		state.isEditModalOpen = false;
		state.isViewModalOpen = false;
		state.isDeleteModalOpen = false;
		state.selectedEmployee = null;
	}

	async function handleCreate(formData: EmployeeCreateRequest) {
		try {
			state.isSubmitting = true;
			await employeeService.createEmployee(formData);
			closeAllModals();
			await fetchEmployees();
		} catch (err) {
			state.error = err instanceof Error ? err.message : 'Failed to create employee';
			console.error('Error creating employee:', err);
		} finally {
			state.isSubmitting = false;
		}
	}

	async function handleUpdate(formData: EmployeeCreateRequest) {
		if (!state.selectedEmployee) return;
		try {
			state.isSubmitting = true;
			await employeeService.updateEmployee(state.selectedEmployee.id, formData);
			closeAllModals();
			await fetchEmployees();
		} catch (err) {
			state.error = err instanceof Error ? err.message : 'Failed to update employee';
			console.error('Error updating employee:', err);
		} finally {
			state.isSubmitting = false;
		}
	}

	async function handleDelete() {
		if (!state.selectedEmployee) return;
		try {
			state.isSubmitting = true;
			await employeeService.deleteEmployee(state.selectedEmployee.id);
			closeAllModals();
			await fetchEmployees();
		} catch (err) {
			state.error = err instanceof Error ? err.message : 'Failed to delete employee';
			console.error('Error deleting employee:', err);
		} finally {
			state.isSubmitting = false;
		}
	}

	onMount(() => {
		startPolling();

		// Set header height for scan line positioning
		const header = document.getElementById('system-header');
		if (header) {
			const headerHeight = header.offsetHeight;
			document.documentElement.style.setProperty('--header-height', `${headerHeight}px`);
		}

		return () => stopPolling();
	});
</script>

<svelte:head>
	<title>HR Platform - Employees</title>
	<meta name="description" content="View and manage employees for HR platform" />
</svelte:head>

<div
	class="min-h-screen bg-gradient-to-br from-blue-50 via-slate-50 to-blue-100 dark:from-slate-950 dark:via-slate-950 dark:to-slate-950 relative overflow-hidden"
>
	<!-- Animated Background Grid -->
	<div
		class="cyber-grid absolute inset-0 opacity-30 {state.isPolling ? 'scan-line' : ''}"
		aria-hidden="true"
	></div>

	<!-- System Header -->
	<SystemHeader
		timestamp={state.timestamp}
		pagination={state.pagination}
		isPolling={state.isPolling}
	/>

	<!-- Scan Line Effect -->
	<div
		class="scan-line fixed left-0 right-0 h-48 pointer-events-none z-[5] bg-gradient-to-b from-transparent via-blue-400/15 to-transparent dark:from-transparent dark:via-cyan-400/30 dark:to-transparent {state.isPolling
			? 'polling'
			: ''}"
		style="top: var(--header-height, 200px)"
		aria-hidden="true"
	></div>

	<!-- Main Content -->
	<main class="container mx-auto px-4 sm:px-6 lg:px-8 py-12 relative z-10">
		<!-- Error Message -->
		{#if state.error}
			<aside
				class="mb-6 p-4 bg-red-500/20 border border-red-500/50 text-red-700 dark:text-red-300 text-sm flex items-center justify-between"
				role="feed"
			>
				<span>{state.error}</span>
				<button
					type="button"
					onclick={() => (state.error = null)}
					class="text-red-700 dark:text-red-300 hover:text-red-900 dark:hover:text-red-100 transition-colors"
					aria-label="Dismiss error"
				>
					<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M6 18L18 6M6 6l12 12"
						/>
					</svg>
				</button>
			</aside>
		{/if}

		<!-- Action Bar -->
		<header class="mb-8 flex justify-between items-center">
			<h1
				class="text-2xl font-bold text-blue-700 dark:text-cyan-400 uppercase tracking-wider font-mono"
			>
				[ Personnel Database ]
			</h1>
			<button
				type="button"
				onclick={openCreateModal}
				class="px-6 py-3 bg-emerald-100 dark:bg-emerald-500/20 text-emerald-700 dark:text-emerald-300 border border-emerald-300 dark:border-emerald-500/50 hover:bg-emerald-200 dark:hover:bg-emerald-500/30 hover:shadow-lg hover:shadow-emerald-300/20 dark:hover:shadow-emerald-500/20 transition-all duration-300 font-semibold uppercase tracking-wider text-sm flex items-center gap-2"
			>
				<svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
					<path
						stroke-linecap="round"
						stroke-linejoin="round"
						stroke-width="2"
						d="M12 4v16m8-8H4"
					/>
				</svg>
				Add Employee
			</button>
		</header>

		<!-- Content States -->
		{#if state.isLoading && state.employees.length === 0}
			<div class="flex flex-col items-center justify-center py-32" role="status" aria-live="polite">
				<LoadingSpinner message="[ INITIALIZING DATABASE ACCESS ]" />
			</div>
		{:else if state.employees.length === 0}
			<section class="flex flex-col items-center justify-center py-32">
				<div
					class="text-center p-12 bg-white/80 dark:bg-slate-800/40 border border-blue-300 dark:border-cyan-500/30 max-w-md shadow-lg backdrop-blur-sm"
				>
					<svg
						class="w-16 h-16 text-blue-400 dark:text-cyan-400/50 mx-auto mb-4"
						fill="none"
						stroke="currentColor"
						viewBox="0 0 24 24"
						aria-hidden="true"
					>
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"
						/>
					</svg>
					<p class="text-blue-600 dark:text-cyan-400 text-lg font-semibold mb-2 font-mono">
						NO PERSONNEL RECORDS FOUND
					</p>
					<p class="text-slate-600 dark:text-cyan-300/60 text-sm">
						Add your first employee to get started
					</p>
				</div>
			</section>
		{:else}
			<section class="grid gap-8 md:grid-cols-2 lg:grid-cols-3" aria-label="Employee list">
				{#each state.employees as employee, index (employee.id)}
					<EmployeeCard
						{employee}
						{index}
						onEdit={openEditModal}
						onDelete={openDeleteModal}
						onView={openViewModal}
					/>
				{/each}
			</section>

			<!-- Pagination -->
			{#if state.pagination.totalPages > 1}
				<nav class="mt-12" aria-label="Pagination">
					<Pagination
						pagination={state.pagination}
						onPageChange={handlePageChange}
						isLoading={state.isLoading}
					/>
				</nav>
			{/if}
		{/if}

		<!-- System Controls -->
		<section class="mt-12 flex justify-center gap-4" aria-label="Polling controls">
			<button
				type="button"
				disabled={state.isPolling}
				class="px-6 py-3 bg-emerald-100 dark:bg-emerald-500/20 text-emerald-700 dark:text-emerald-300 border border-emerald-300 dark:border-emerald-500/50 hover:bg-emerald-200 dark:hover:bg-emerald-500/30 hover:shadow-lg hover:shadow-emerald-300/20 dark:hover:shadow-emerald-500/20 transition-all duration-300 font-semibold uppercase tracking-wider text-sm disabled:opacity-30 disabled:cursor-not-allowed disabled:hover:bg-emerald-100 dark:disabled:hover:bg-emerald-500/20 disabled:shadow-none"
				onclick={startPolling}
			>
				<span class="inline-flex items-center gap-2">
					<span
						class="w-2 h-2 bg-emerald-600 dark:bg-emerald-400"
						class:animate-pulse={state.isPolling}
						aria-hidden="true"
					></span>
					{state.isPolling ? 'POLLING ACTIVE' : 'START POLLING'}
				</span>
			</button>
			<button
				type="button"
				disabled={!state.isPolling}
				class="px-6 py-3 bg-red-100 dark:bg-red-500/20 text-red-700 dark:text-red-300 border border-red-300 dark:border-red-500/50 hover:bg-red-200 dark:hover:bg-red-500/30 hover:shadow-lg hover:shadow-red-300/20 dark:hover:shadow-red-500/20 transition-all duration-300 font-semibold uppercase tracking-wider text-sm disabled:opacity-30 disabled:cursor-not-allowed disabled:hover:bg-red-100 dark:disabled:hover:bg-red-500/20 disabled:shadow-none"
				onclick={stopPolling}
			>
				<span class="inline-flex items-center gap-2">
					<span
						class="w-2 h-2"
						class:bg-slate-400={!state.isPolling}
						class:dark:bg-slate-500={!state.isPolling}
						class:bg-red-600={state.isPolling}
						class:dark:bg-red-400={state.isPolling}
						aria-hidden="true"
					></span>
					{state.isPolling ? 'STOP POLLING' : 'POLLING STOPPED'}
				</span>
			</button>
		</section>

		<!-- Footer Terminal -->
		<footer class="mt-16 max-w-2xl mx-auto">
			<div
				class="overflow-hidden border border-blue-300 dark:border-cyan-500/30 shadow-lg bg-gradient-to-br from-white/95 to-blue-50/95 dark:from-slate-900/90 dark:to-blue-950/90 backdrop-blur-lg"
			>
				<div
					class="bg-blue-100 dark:bg-cyan-500/10 px-4 py-2 border-b border-blue-200 dark:border-cyan-500/30 flex items-center gap-2"
				>
					<div class="flex gap-1.5" aria-hidden="true">
						<div class="w-2.5 h-2.5 bg-red-500/70"></div>
						<div class="w-2.5 h-2.5 bg-yellow-500/70"></div>
						<div class="w-2.5 h-2.5 bg-green-500/70"></div>
					</div>
					<span class="text-blue-600 dark:text-cyan-400/70 text-xs font-mono">system.terminal</span>
				</div>
				<div class="p-6 space-y-2 font-mono text-sm">
					<p class="text-slate-700 dark:text-cyan-400/70">
						<span class="text-blue-600 dark:text-cyan-500">$</span> System Status:
						<span class="text-emerald-600 dark:text-emerald-400">OPERATIONAL</span>
					</p>
					<p class="text-slate-700 dark:text-cyan-400/70">
						<span class="text-blue-600 dark:text-cyan-500">$</span> Platform:
						<span class="text-blue-700 dark:text-cyan-300">HR Management Interface v2.0</span>
					</p>
					<p class="text-slate-700 dark:text-cyan-400/70">
						<span class="text-blue-600 dark:text-cyan-500">$</span> Real-time personnel tracking and
						administration
					</p>
					<p class="text-slate-500 dark:text-cyan-400/50 text-xs mt-4">
						© 2025 HR Platform API - All Rights Reserved
					</p>
				</div>
			</div>
		</footer>
	</main>
</div>

<!-- Create Employee Modal -->
<Modal
	bind:isOpen={state.isCreateModalOpen}
	title="Create New Employee"
	onClose={closeAllModals}
	size="xl"
>
	<EmployeeForm onSubmit={handleCreate} onCancel={closeAllModals} isLoading={state.isSubmitting} />
</Modal>

<!-- Edit Employee Modal -->
<Modal bind:isOpen={state.isEditModalOpen} title="Edit Employee" onClose={closeAllModals} size="xl">
	<EmployeeForm
		employee={state.selectedEmployee}
		onSubmit={handleUpdate}
		onCancel={closeAllModals}
		isLoading={state.isSubmitting}
	/>
</Modal>

<!-- View Employee Modal -->
<Modal bind:isOpen={state.isViewModalOpen} title="Employee Details" onClose={closeAllModals}>
	{#if state.selectedEmployee}
		{@const employee = state.selectedEmployee}
		<div class="space-y-6">
			<!-- Photo and Basic Info -->
			<div class="flex items-center gap-6 pb-6 border-b border-blue-200 dark:border-cyan-500/30">
				{#if employee.photo}
					<img
						src={employee.photo}
						alt="{employee.firstName} {employee.lastName}"
						class="w-32 h-32 border-2 border-blue-300 dark:border-cyan-500/50 object-cover shadow-lg"
					/>
				{:else}
					<div
						class="w-32 h-32 border-2 border-blue-300 dark:border-cyan-500/50 bg-gradient-to-br from-cyan-100 to-blue-100 dark:from-cyan-500/20 dark:to-blue-500/20 flex items-center justify-center text-cyan-700 dark:text-cyan-300 font-bold text-4xl shadow-lg"
					>
						{employee.firstName.charAt(0)}{employee.lastName.charAt(0)}
					</div>
				{/if}
				<div>
					<h2 class="text-2xl font-bold text-slate-900 dark:text-cyan-100 mb-2">
						{employee.firstName}
						{employee.middleName ? employee.middleName : ''}
						{employee.lastName}
					</h2>
					<p class="text-cyan-600 dark:text-cyan-400 font-mono">#{employee.employeeNumber}</p>
					<p class="text-slate-600 dark:text-cyan-400/70 text-sm font-mono mt-1">
						{employee.itemNumber}
					</p>
				</div>
			</div>

			<!-- Details Grid -->
			<dl class="grid grid-cols-1 md:grid-cols-2 gap-4">
				<div
					class="bg-white/90 dark:bg-slate-900/70 border border-blue-300/50 dark:border-cyan-500/40 p-4 backdrop-blur-sm shadow-sm dark:shadow-cyan-500/10"
				>
					<dt
						class="block text-xs font-semibold text-blue-700 dark:text-cyan-500/70 uppercase tracking-wide mb-2 font-mono"
					>
						Email
					</dt>
					<dd class="text-slate-900 dark:text-slate-200 text-sm">{employee.email}</dd>
				</div>
				<div
					class="bg-white/90 dark:bg-slate-900/70 border border-blue-300/50 dark:border-cyan-500/40 p-4 backdrop-blur-sm shadow-sm dark:shadow-cyan-500/10"
				>
					<dt
						class="block text-xs font-semibold text-blue-700 dark:text-cyan-500/70 uppercase tracking-wide mb-2 font-mono"
					>
						Phone
					</dt>
					<dd class="text-slate-900 dark:text-slate-200 text-sm">
						{employee.phoneNumber || 'N/A'}
					</dd>
				</div>
				<div
					class="bg-white/90 dark:bg-slate-900/70 border border-blue-300/50 dark:border-cyan-500/40 p-4 backdrop-blur-sm shadow-sm dark:shadow-cyan-500/10"
				>
					<dt
						class="block text-xs font-semibold text-blue-700 dark:text-cyan-500/70 uppercase tracking-wide mb-2 font-mono"
					>
						Date of Birth
					</dt>
					<dd class="text-slate-900 dark:text-slate-200 text-sm">{employee.dateOfBirth}</dd>
				</div>
				<div
					class="bg-white/90 dark:bg-slate-900/70 border border-blue-300/50 dark:border-cyan-500/40 p-4 backdrop-blur-sm shadow-sm dark:shadow-cyan-500/10"
				>
					<dt
						class="block text-xs font-semibold text-blue-700 dark:text-cyan-500/70 uppercase tracking-wide mb-2 font-mono"
					>
						Gender
					</dt>
					<dd class="text-slate-900 dark:text-slate-200 text-sm">{employee.gender}</dd>
				</div>
				<div
					class="bg-white/90 dark:bg-slate-900/70 border border-blue-300/50 dark:border-cyan-500/40 p-4 backdrop-blur-sm shadow-sm dark:shadow-cyan-500/10"
				>
					<dt
						class="block text-xs font-semibold text-blue-700 dark:text-cyan-500/70 uppercase tracking-wide mb-2 font-mono"
					>
						Civil Status
					</dt>
					<dd class="text-slate-900 dark:text-slate-200 text-sm">{employee.civilStatus}</dd>
				</div>
				<div
					class="bg-white/90 dark:bg-slate-900/70 border border-blue-300/50 dark:border-cyan-500/40 p-4 backdrop-blur-sm shadow-sm dark:shadow-cyan-500/10"
				>
					<dt
						class="block text-xs font-semibold text-blue-700 dark:text-cyan-500/70 uppercase tracking-wide mb-2 font-mono"
					>
						Status
					</dt>
					<dd class="text-slate-900 dark:text-slate-200 text-sm">
						{employee.archived ? 'Archived' : 'Active'}
					</dd>
				</div>
				<div
					class="bg-white/90 dark:bg-slate-900/70 border border-blue-300/50 dark:border-cyan-500/40 p-4 md:col-span-2 backdrop-blur-sm shadow-sm dark:shadow-cyan-500/10"
				>
					<dt
						class="block text-xs font-semibold text-blue-700 dark:text-cyan-500/70 uppercase tracking-wide mb-2 font-mono"
					>
						Tax ID Number
					</dt>
					<dd class="text-slate-900 dark:text-slate-200 text-sm font-mono">
						{employee.taxPayerIdentificationNumber}
					</dd>
				</div>
				{#if employee.bankAccountNumber}
					<div
						class="bg-white/90 dark:bg-slate-900/70 border border-blue-300/50 dark:border-cyan-500/40 p-4 md:col-span-2 backdrop-blur-sm shadow-sm dark:shadow-cyan-500/10"
					>
						<dt
							class="block text-xs font-semibold text-blue-700 dark:text-cyan-500/70 uppercase tracking-wide mb-2 font-mono"
						>
							Bank Account
						</dt>
						<dd class="text-slate-900 dark:text-slate-200 text-sm font-mono">
							{employee.bankAccountNumber}
						</dd>
					</div>
				{/if}
			</dl>
		</div>
	{/if}
</Modal>

<!-- Delete Confirmation Modal -->
<Modal
	bind:isOpen={state.isDeleteModalOpen}
	title="Confirm Deletion"
	onClose={closeAllModals}
	size="sm"
>
	{#if state.selectedEmployee}
		<div class="space-y-6">
			<div class="text-center">
				<div
					class="mx-auto flex items-center justify-center h-16 w-16 bg-red-100 dark:bg-red-500/20 mb-4"
					aria-hidden="true"
				>
					<svg
						class="h-8 w-8 text-red-600 dark:text-red-400"
						fill="none"
						stroke="currentColor"
						viewBox="0 0 24 24"
					>
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
						/>
					</svg>
				</div>
				<h2 class="text-lg font-semibold text-red-700 dark:text-red-300 mb-2">Are you sure?</h2>
				<p class="text-slate-700 dark:text-cyan-300/70 text-sm">
					You are about to delete the employee record for
					<span class="font-semibold text-blue-700 dark:text-cyan-300">
						{state.selectedEmployee.firstName}
						{state.selectedEmployee.lastName}
					</span>. This action cannot be undone.
				</p>
			</div>

			<div class="flex gap-4 justify-end">
				<button
					type="button"
					onclick={closeAllModals}
					disabled={state.isSubmitting}
					class="px-6 py-3 bg-slate-100 dark:bg-slate-500/20 text-slate-700 dark:text-slate-300 border border-slate-300 dark:border-slate-500/50 hover:bg-slate-200 dark:hover:bg-slate-500/30 transition-all duration-300 font-semibold uppercase tracking-wider text-sm disabled:opacity-30"
				>
					Cancel
				</button>
				<button
					type="button"
					onclick={handleDelete}
					disabled={state.isSubmitting}
					class="px-6 py-3 bg-red-100 dark:bg-red-500/20 text-red-700 dark:text-red-300 border border-red-300 dark:border-red-500/50 hover:bg-red-200 dark:hover:bg-red-500/30 hover:shadow-lg hover:shadow-red-300/20 dark:hover:shadow-red-500/20 transition-all duration-300 font-semibold uppercase tracking-wider text-sm disabled:opacity-30 flex items-center gap-2"
				>
					{#if state.isSubmitting}
						<div
							class="w-4 h-4 border-2 border-red-700 dark:border-red-300 border-t-transparent animate-spin"
							role="status"
							aria-label="Deleting"
						></div>
					{/if}
					Delete
				</button>
			</div>
		</div>
	{/if}
</Modal>

<style>
	@keyframes grid-move {
		0% {
			background-position: 0 0;
		}
		100% {
			background-position: 40px 40px;
		}
	}

	@keyframes scan-line {
		0% {
			transform: translateY(0);
		}
		100% {
			transform: translateY(calc(100vh - var(--header-height, 200px)));
		}
	}

	.cyber-grid {
		background-image:
			linear-gradient(rgba(59, 130, 246, 0.08) 1px, transparent 1px),
			linear-gradient(90deg, rgba(59, 130, 246, 0.08) 1px, transparent 1px);
		background-size: 40px 40px;
		animation: grid-move 2s linear infinite;
	}

	@media (prefers-color-scheme: dark) {
		.cyber-grid {
			background-image:
				linear-gradient(rgba(0, 255, 255, 0.1) 1px, transparent 1px),
				linear-gradient(90deg, rgba(0, 255, 255, 0.1) 1px, transparent 1px);
		}
	}

	.scan-line {
		animation: scan-line 8s linear infinite;
		animation-play-state: paused;
	}

</style>
