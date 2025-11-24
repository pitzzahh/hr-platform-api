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
	let {
		employees,
		pagination,
		timestamp,
		isLoading,
		isPolling,
		error,
		isCreateModalOpen,
		isEditModalOpen,
		isViewModalOpen,
		isDeleteModalOpen,
		selectedEmployee,
		isSubmitting
	} = $state({
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
			isLoading = true;
			error = null;
			const response: EmployeeListResponse = await employeeService.getEmployees(
				pagination.page,
				pagination.size
			);
			employees = response.data;
			pagination = response.pagination;
			timestamp = response.timestamp;
		} catch (error) {
			error = error instanceof Error ? error.message : 'Failed to fetch employees';
			console.error('Error fetching employees:', error);
		} finally {
			isLoading = false;
		}
	}

	function startPolling() {
		if (isPolling) return;
		isPolling = true;
		fetchEmployees();
		pollingInterval = setInterval(() => fetchEmployees(), 5000);
	}

	function stopPolling() {
		if (pollingInterval) {
			clearInterval(pollingInterval);
			pollingInterval = null;
		}
		isPolling = false;
	}

	function handlePageChange(newPage: number) {
		pagination.page = newPage;
		fetchEmployees();
	}

	// CRUD Operations
	function openCreateModal() {
		selectedEmployee = null;
		isCreateModalOpen = true;
	}

	function openEditModal(employee: Employee) {
		selectedEmployee = employee;
		isEditModalOpen = true;
	}

	function openViewModal(employee: Employee) {
		selectedEmployee = employee;
		isViewModalOpen = true;
	}

	function openDeleteModal(employee: Employee) {
		selectedEmployee = employee;
		isDeleteModalOpen = true;
	}

	function closeAllModals() {
		isCreateModalOpen = false;
		isEditModalOpen = false;
		isViewModalOpen = false;
		isDeleteModalOpen = false;
		selectedEmployee = null;
	}

	async function handleCreate(formData: EmployeeCreateRequest) {
		try {
			isSubmitting = true;
			await employeeService.createEmployee(formData);
			closeAllModals();
			await fetchEmployees();
		} catch (error) {
			error = error instanceof Error ? error.message : 'Failed to create employee';
			console.error('Error creating employee:', error);
		} finally {
			isSubmitting = false;
		}
	}

	async function handleUpdate(formData: EmployeeCreateRequest) {
		if (!selectedEmployee) return;
		try {
			isSubmitting = true;
			await employeeService.updateEmployee(selectedEmployee.id, formData);
			closeAllModals();
			await fetchEmployees();
		} catch (error) {
			error = error instanceof Error ? error.message : 'Failed to update employee';
			console.error('Error updating employee:', error);
		} finally {
			isSubmitting = false;
		}
	}

	async function handleDelete() {
		if (!selectedEmployee) return;
		try {
			isSubmitting = true;
			await employeeService.deleteEmployee(selectedEmployee.id);
			closeAllModals();
			await fetchEmployees();
		} catch (error) {
			error = error instanceof Error ? error.message : 'Failed to delete employee';
			console.error('Error deleting employee:', error);
		} finally {
			isSubmitting = false;
		}
	}

	onMount(() => {
		startPolling();

		// Set header height for scan line positioning
		const header = document.getElementById('header');
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
	class="min-h-screen bg-gradient-to-br from-slate-950 via-blue-950 to-slate-900 relative overflow-hidden"
>
	<!-- Animated Background Grid -->
	<div class="cyber-grid absolute inset-0 opacity-30"></div>

	<!-- Header Section -->
	<SystemHeader {timestamp} {pagination} {isPolling} />

	<!-- Scan Line Effect -->
	<div class="scan-line"></div>

	<!-- Main Content -->
	<div class="container mx-auto px-4 sm:px-6 lg:px-8 py-12 relative z-10">
		<!-- Error Message -->
		{#if error}
			<div
				class="mb-6 p-4 bg-red-500/20 border border-red-500/50 rounded-lg text-red-300 text-sm flex items-center justify-between"
			>
				<span>{error}</span>
				<button
					aria-label="Error"
					onclick={() => (error = null)}
					class="text-red-300 hover:text-red-100 transition-colors"
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
			</div>
		{/if}

		<!-- Action Bar -->
		<div class="mb-8 flex justify-between items-center">
			<h2 class="text-2xl font-bold text-cyan-400 uppercase tracking-wider">
				[ Personnel Database ]
			</h2>
			<button
				onclick={openCreateModal}
				class="px-6 py-3 bg-emerald-500/20 text-emerald-300 border border-emerald-500/50 rounded-lg hover:bg-emerald-500/30 hover:shadow-lg hover:shadow-emerald-500/20 transition-all duration-300 font-semibold uppercase tracking-wider text-sm flex items-center gap-2"
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
		</div>

		{#if isLoading && employees.length === 0}
			<div class="flex flex-col items-center justify-center py-32">
				<LoadingSpinner message="[ INITIALIZING DATABASE ACCESS ]" />
			</div>
		{:else if employees.length === 0}
			<div class="flex flex-col items-center justify-center py-32">
				<div class="text-center p-12 bg-slate-800/40 border border-cyan-500/30 rounded-lg max-w-md">
					<svg
						class="w-16 h-16 text-cyan-400/50 mx-auto mb-4"
						fill="none"
						stroke="currentColor"
						viewBox="0 0 24 24"
					>
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"
						/>
					</svg>
					<p class="text-cyan-400 text-lg font-semibold mb-2">NO PERSONNEL RECORDS FOUND</p>
					<p class="text-cyan-300/60 text-sm">Add your first employee to get started</p>
				</div>
			</div>
		{:else}
			<div class="grid gap-8 md:grid-cols-2 lg:grid-cols-3">
				{#each employees as employee, index (employee.id)}
					<EmployeeCard
						{employee}
						{index}
						onEdit={openEditModal}
						onDelete={openDeleteModal}
						onView={openViewModal}
					/>
				{/each}
			</div>

			<!-- Pagination -->
			{#if pagination.totalPages > 1}
				<div class="mt-12">
					<Pagination {pagination} onPageChange={handlePageChange} {isLoading} />
				</div>
			{/if}
		{/if}

		<!-- System Controls -->
		<div class="mt-12 flex justify-center gap-4">
			<button
				disabled={isPolling}
				class="px-6 py-3 bg-emerald-500/20 text-emerald-300 border border-emerald-500/50 rounded-lg hover:bg-emerald-500/30 hover:shadow-lg hover:shadow-emerald-500/20 transition-all duration-300 font-semibold uppercase tracking-wider text-sm disabled:opacity-30 disabled:cursor-not-allowed disabled:hover:bg-emerald-500/20 disabled:shadow-none"
				onclick={startPolling}
			>
				<span class="inline-flex items-center gap-2">
					<span
						class="w-2 h-2 rounded-full {isPolling
							? 'bg-emerald-400 animate-pulse'
							: 'bg-emerald-400'}"
					></span>
					{isPolling ? 'POLLING ACTIVE' : 'START POLLING'}
				</span>
			</button>
			<button
				disabled={!isPolling}
				class="px-6 py-3 bg-red-500/20 text-red-300 border border-red-500/50 rounded-lg hover:bg-red-500/30 hover:shadow-lg hover:shadow-red-500/20 transition-all duration-300 font-semibold uppercase tracking-wider text-sm disabled:opacity-30 disabled:cursor-not-allowed disabled:hover:bg-red-500/20 disabled:shadow-none"
				onclick={stopPolling}
			>
				<span class="inline-flex items-center gap-2">
					<span class="w-2 h-2 rounded-full {!isPolling ? 'bg-slate-500' : 'bg-red-400'}"></span>
					{isPolling ? 'STOP POLLING' : 'POLLING STOPPED'}
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
					<p class="text-cyan-400/50 text-xs mt-4">© 2025 HR Platform API - All Rights Reserved</p>
				</div>
			</div>
		</div>
	</div>
</div>

<!-- Create Employee Modal -->
<Modal
	bind:isOpen={isCreateModalOpen}
	title="Create New Employee"
	onClose={closeAllModals}
	size="xl"
>
	<EmployeeForm onSubmit={handleCreate} onCancel={closeAllModals} isLoading={isSubmitting} />
</Modal>

<!-- Edit Employee Modal -->
<Modal bind:isOpen={isEditModalOpen} title="Edit Employee" onClose={closeAllModals} size="xl">
	<EmployeeForm
		employee={selectedEmployee}
		onSubmit={handleUpdate}
		onCancel={closeAllModals}
		isLoading={isSubmitting}
	/>
</Modal>

<!-- View Employee Modal -->
<Modal bind:isOpen={isViewModalOpen} title="Employee Details" onClose={closeAllModals}>
	{#if selectedEmployee}
		{@const employee = selectedEmployee}
		<div class="space-y-6">
			<!-- Photo and Basic Info -->
			<div class="flex items-center gap-6 pb-6 border-b border-cyan-500/30">
				{#if employee.photo}
					<img
						src={employee.photo}
						alt="{employee.firstName} {employee.lastName}"
						class="w-32 h-32 rounded-lg border-2 border-cyan-500/50 object-cover shadow-lg shadow-cyan-500/20"
					/>
				{:else}
					<div
						class="w-32 h-32 rounded-lg border-2 border-cyan-500/50 bg-gradient-to-br from-cyan-500/20 to-blue-500/20 flex items-center justify-center text-cyan-300 font-bold text-4xl shadow-lg shadow-cyan-500/20"
					>
						{employee.firstName.charAt(0)}{employee.lastName.charAt(0)}
					</div>
				{/if}
				<div>
					<h3 class="text-2xl font-bold text-cyan-100 mb-2">
						{employee.firstName}
						{employee.middleName ? employee.middleName : ''}
						{employee.lastName}
					</h3>
					<p class="text-cyan-400 font-mono">#{employee.employeeNumber}</p>
					<p class="text-cyan-400/70 text-sm font-mono mt-1">{employee.itemNumber}</p>
				</div>
			</div>

			<!-- Details Grid -->
			<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
				<div class="info-field">
					<label for="email">Email</label>
					<p id="email">{employee.email}</p>
				</div>
				<div class="info-field">
					<label for="phoneNumber">Phone</label>
					<p id="phoneNumber">{employee.phoneNumber || 'N/A'}</p>
				</div>
				<div class="info-field">
					<label for="dob">Date of Birth</label>
					<p id="dob">{employee.dateOfBirth}</p>
				</div>
				<div class="info-field">
					<label for="gender">Gender</label>
					<p id="gender">{employee.gender}</p>
				</div>
				<div class="info-field">
					<label for="civilStatus">Civil Status</label>
					<p id="civilStatus">{employee.civilStatus}</p>
				</div>
				<div class="info-field">
					<label for="status">Status</label>
					<p id="status">{employee.archived ? 'Archived' : 'Active'}</p>
				</div>
				<div class="info-field md:col-span-2">
					<label for="tin">Tax ID Number</label>
					<p id="tin" class="font-mono">{employee.taxPayerIdentificationNumber}</p>
				</div>
				{#if employee.bankAccountNumber}
					<div class="info-field md:col-span-2">
						<label for="bankAccountNumber">Bank Account</label>
						<p id="bankAccountNumber" class="font-mono">{employee.bankAccountNumber}</p>
					</div>
				{/if}
			</div>
		</div>
	{/if}
</Modal>

<!-- Delete Confirmation Modal -->
<Modal bind:isOpen={isDeleteModalOpen} title="Confirm Deletion" onClose={closeAllModals} size="sm">
	{#if selectedEmployee}
		<div class="space-y-6">
			<div class="text-center">
				<div
					class="mx-auto flex items-center justify-center h-16 w-16 rounded-full bg-red-500/20 mb-4"
				>
					<svg class="h-8 w-8 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
						/>
					</svg>
				</div>
				<h3 class="text-lg font-semibold text-red-300 mb-2">Are you sure?</h3>
				<p class="text-cyan-300/70 text-sm">
					You are about to delete the employee record for
					<span class="font-semibold text-cyan-300">
						{selectedEmployee.firstName}
						{selectedEmployee.lastName}
					</span>. This action cannot be undone.
				</p>
			</div>

			<div class="flex gap-4 justify-end">
				<button
					onclick={closeAllModals}
					disabled={isSubmitting}
					class="px-6 py-3 bg-slate-500/20 text-slate-300 border border-slate-500/50 rounded-lg hover:bg-slate-500/30 transition-all duration-300 font-semibold uppercase tracking-wider text-sm disabled:opacity-30"
				>
					Cancel
				</button>
				<button
					onclick={handleDelete}
					disabled={isSubmitting}
					class="px-6 py-3 bg-red-500/20 text-red-300 border border-red-500/50 rounded-lg hover:bg-red-500/30 hover:shadow-lg hover:shadow-red-500/20 transition-all duration-300 font-semibold uppercase tracking-wider text-sm disabled:opacity-30 flex items-center gap-2"
				>
					{#if isSubmitting}
						<div
							class="w-4 h-4 border-2 border-red-300 border-t-transparent rounded-full animate-spin"
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

	.cyber-grid {
		background-image:
			linear-gradient(rgba(0, 255, 255, 0.1) 1px, transparent 1px),
			linear-gradient(90deg, rgba(0, 255, 255, 0.1) 1px, transparent 1px);
		background-size: 40px 40px;
		animation: grid-move 2s linear infinite;
	}

	.scan-line {
		position: fixed;
		left: 0;
		right: 0;
		top: var(--header-height, 200px);
		height: 200px;
		pointer-events: none;
		z-index: 5;
		animation: scan-line 8s linear infinite;
		background: linear-gradient(
			to bottom,
			transparent,
			rgba(0, 255, 255, 0.1),
			rgba(0, 255, 255, 0.3),
			rgba(0, 255, 255, 0.1),
			transparent
		);
	}

	@keyframes scan-line {
		0% {
			transform: translateY(0);
		}
		100% {
			transform: translateY(calc(100vh - var(--header-height, 200px)));
		}
	}

	.hologram-card {
		position: relative;
		background: linear-gradient(135deg, rgba(10, 25, 47, 0.9) 0%, rgba(20, 35, 57, 0.9) 100%);
		backdrop-filter: blur(10px);
		box-shadow:
			0 8px 32px rgba(0, 0, 0, 0.4),
			0 0 40px rgba(0, 255, 255, 0.1);
	}

	.info-field {
		background: rgba(15, 23, 42, 0.6);
		border: 1px solid rgba(100, 116, 139, 0.5);
		border-radius: 0.5rem;
		padding: 1rem;
	}

	.info-field label {
		display: block;
		font-size: 0.75rem;
		font-weight: 600;
		color: rgba(103, 232, 249, 0.7);
		text-transform: uppercase;
		letter-spacing: 0.05em;
		margin-bottom: 0.5rem;
		font-family: 'Courier New', monospace;
	}

	.info-field p {
		color: rgba(203, 213, 225, 1);
		font-size: 0.875rem;
	}
</style>
