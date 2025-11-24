<script lang="ts">
	import type { Employee, EmployeeCreateRequest, Gender, CivilStatus } from '$lib/types/employee';

	interface Props {
		employee?: Employee | null;
		onSubmit: (data: EmployeeCreateRequest) => void;
		onCancel: () => void;
		isLoading?: boolean;
	}

	let { employee = null, onSubmit, onCancel, isLoading = false }: Props = $props();

	let formData = $state<EmployeeCreateRequest>({
		employeeNumber: employee?.employeeNumber || '',
		itemNumber: employee?.itemNumber || '',
		firstName: employee?.firstName || '',
		middleName: employee?.middleName || '',
		lastName: employee?.lastName || '',
		photo: employee?.photo || '',
		dateOfBirth: employee?.dateOfBirth || '',
		email: employee?.email || '',
		phoneNumber: employee?.phoneNumber || '',
		gender: employee?.gender || 'MALE',
		taxPayerIdentificationNumber: employee?.taxPayerIdentificationNumber || '',
		civilStatus: employee?.civilStatus || 'SINGLE',
		bankAccountNumber: employee?.bankAccountNumber || ''
	});

	let errors = $state<Record<string, string>>({});

	function validateForm(): boolean {
		errors = {};

		if (!formData.employeeNumber.trim()) {
			errors.employeeNumber = 'Employee number is required';
		} else if (!/^\d+$/.test(formData.employeeNumber)) {
			errors.employeeNumber = 'Employee number must contain only digits';
		}

		if (!formData.itemNumber.trim()) {
			errors.itemNumber = 'Item number is required';
		}

		if (!formData.firstName.trim()) {
			errors.firstName = 'First name is required';
		}

		if (!formData.lastName.trim()) {
			errors.lastName = 'Last name is required';
		}

		if (!formData.dateOfBirth) {
			errors.dateOfBirth = 'Date of birth is required';
		}

		if (!formData.email.trim()) {
			errors.email = 'Email is required';
		} else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
			errors.email = 'Invalid email format';
		}

		if (!formData.phoneNumber?.trim()) {
			errors.phoneNumber = 'Phone number is required';
		} else if (!/^(09\d{9}|9\d{9}|\+639\d{9})$/.test(formData.phoneNumber)) {
			errors.phoneNumber = 'Invalid phone number format';
		}

		if (!formData.taxPayerIdentificationNumber.trim()) {
			errors.taxPayerIdentificationNumber = 'TIN is required';
		}

		return Object.keys(errors).length === 0;
	}

	function handleSubmit(e: Event) {
		e.preventDefault();
		if (validateForm()) {
			onSubmit(formData);
		}
	}

	const genderOptions: Gender[] = ['MALE', 'FEMALE', 'OTHER'];
	const civilStatusOptions: CivilStatus[] = ['SINGLE', 'MARRIED', 'WIDOWED'];
</script>

<form onsubmit={handleSubmit} class="space-y-6">
	<!-- Personal Information Section -->
	<div class="hologram-section">
		<div class="section-header">
			<h3 class="text-cyan-400 font-semibold uppercase tracking-wider text-sm">
				[ Personal Information ]
			</h3>
		</div>

		<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
			<!-- Employee Number -->
			<div class="form-group">
				<label for="employeeNumber" class="form-label">Employee Number *</label>
				<input
					id="employeeNumber"
					type="text"
					bind:value={formData.employeeNumber}
					class="form-input {errors.employeeNumber ? 'border-red-500/50' : ''}"
					placeholder="e.g., 20240001"
					disabled={isLoading}
				/>
				{#if errors.employeeNumber}
					<p class="text-red-400 text-xs mt-1">{errors.employeeNumber}</p>
				{/if}
			</div>

			<!-- Item Number -->
			<div class="form-group">
				<label for="itemNumber" class="form-label">Item Number *</label>
				<input
					id="itemNumber"
					type="text"
					bind:value={formData.itemNumber}
					class="form-input {errors.itemNumber ? 'border-red-500/50' : ''}"
					placeholder="e.g., ITM-2024-001"
					disabled={isLoading}
				/>
				{#if errors.itemNumber}
					<p class="text-red-400 text-xs mt-1">{errors.itemNumber}</p>
				{/if}
			</div>

			<!-- First Name -->
			<div class="form-group">
				<label for="firstName" class="form-label">First Name *</label>
				<input
					id="firstName"
					type="text"
					bind:value={formData.firstName}
					class="form-input {errors.firstName ? 'border-red-500/50' : ''}"
					placeholder="Juan"
					disabled={isLoading}
				/>
				{#if errors.firstName}
					<p class="text-red-400 text-xs mt-1">{errors.firstName}</p>
				{/if}
			</div>

			<!-- Middle Name -->
			<div class="form-group">
				<label for="middleName" class="form-label">Middle Name</label>
				<input
					id="middleName"
					type="text"
					bind:value={formData.middleName}
					class="form-input"
					placeholder="Santos"
					disabled={isLoading}
				/>
			</div>

			<!-- Last Name -->
			<div class="form-group">
				<label for="lastName" class="form-label">Last Name *</label>
				<input
					id="lastName"
					type="text"
					bind:value={formData.lastName}
					class="form-input {errors.lastName ? 'border-red-500/50' : ''}"
					placeholder="Dela Cruz"
					disabled={isLoading}
				/>
				{#if errors.lastName}
					<p class="text-red-400 text-xs mt-1">{errors.lastName}</p>
				{/if}
			</div>

			<!-- Date of Birth -->
			<div class="form-group">
				<label for="dateOfBirth" class="form-label">Date of Birth *</label>
				<input
					id="dateOfBirth"
					type="date"
					bind:value={formData.dateOfBirth}
					class="form-input {errors.dateOfBirth ? 'border-red-500/50' : ''}"
					disabled={isLoading}
				/>
				{#if errors.dateOfBirth}
					<p class="text-red-400 text-xs mt-1">{errors.dateOfBirth}</p>
				{/if}
			</div>

			<!-- Gender -->
			<div class="form-group">
				<label for="gender" class="form-label">Gender *</label>
				<select
					id="gender"
					bind:value={formData.gender}
					class="form-input"
					disabled={isLoading}
				>
					{#each genderOptions as gender}
						<option value={gender}>{gender}</option>
					{/each}
				</select>
			</div>

			<!-- Civil Status -->
			<div class="form-group">
				<label for="civilStatus" class="form-label">Civil Status *</label>
				<select
					id="civilStatus"
					bind:value={formData.civilStatus}
					class="form-input"
					disabled={isLoading}
				>
					{#each civilStatusOptions as status}
						<option value={status}>{status}</option>
					{/each}
				</select>
			</div>
		</div>
	</div>

	<!-- Contact Information Section -->
	<div class="hologram-section">
		<div class="section-header">
			<h3 class="text-cyan-400 font-semibold uppercase tracking-wider text-sm">
				[ Contact Information ]
			</h3>
		</div>

		<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
			<!-- Email -->
			<div class="form-group md:col-span-2">
				<label for="email" class="form-label">Email Address *</label>
				<input
					id="email"
					type="email"
					bind:value={formData.email}
					class="form-input {errors.email ? 'border-red-500/50' : ''}"
					placeholder="juan.delacruz@hrplatform.com"
					disabled={isLoading}
				/>
				{#if errors.email}
					<p class="text-red-400 text-xs mt-1">{errors.email}</p>
				{/if}
			</div>

			<!-- Phone Number -->
			<div class="form-group">
				<label for="phoneNumber" class="form-label">Phone Number *</label>
				<input
					id="phoneNumber"
					type="tel"
					bind:value={formData.phoneNumber}
					class="form-input {errors.phoneNumber ? 'border-red-500/50' : ''}"
					placeholder="09171234567"
					disabled={isLoading}
				/>
				{#if errors.phoneNumber}
					<p class="text-red-400 text-xs mt-1">{errors.phoneNumber}</p>
				{/if}
			</div>

			<!-- Photo URL -->
			<div class="form-group">
				<label for="photo" class="form-label">Photo URL</label>
				<input
					id="photo"
					type="url"
					bind:value={formData.photo}
					class="form-input"
					placeholder="https://example.com/photo.jpg"
					disabled={isLoading}
				/>
			</div>
		</div>
	</div>

	<!-- Financial Information Section -->
	<div class="hologram-section">
		<div class="section-header">
			<h3 class="text-cyan-400 font-semibold uppercase tracking-wider text-sm">
				[ Financial Information ]
			</h3>
		</div>

		<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
			<!-- TIN -->
			<div class="form-group">
				<label for="tin" class="form-label">Tax Identification Number *</label>
				<input
					id="tin"
					type="text"
					bind:value={formData.taxPayerIdentificationNumber}
					class="form-input {errors.taxPayerIdentificationNumber ? 'border-red-500/50' : ''}"
					placeholder="123-456-789-000"
					disabled={isLoading}
				/>
				{#if errors.taxPayerIdentificationNumber}
					<p class="text-red-400 text-xs mt-1">{errors.taxPayerIdentificationNumber}</p>
				{/if}
			</div>

			<!-- Bank Account Number -->
			<div class="form-group">
				<label for="bankAccount" class="form-label">Bank Account Number</label>
				<input
					id="bankAccount"
					type="text"
					bind:value={formData.bankAccountNumber}
					class="form-input"
					placeholder="1234567890123456"
					disabled={isLoading}
				/>
			</div>
		</div>
	</div>

	<!-- Form Actions -->
	<div class="flex gap-4 justify-end pt-4">
		<button
			type="button"
			onclick={onCancel}
			disabled={isLoading}
			class="px-6 py-3 bg-slate-500/20 text-slate-300 border border-slate-500/50 rounded-lg hover:bg-slate-500/30 hover:shadow-lg hover:shadow-slate-500/20 transition-all duration-300 font-semibold uppercase tracking-wider text-sm disabled:opacity-30 disabled:cursor-not-allowed"
		>
			Cancel
		</button>
		<button
			type="submit"
			disabled={isLoading}
			class="px-6 py-3 bg-cyan-500/20 text-cyan-300 border border-cyan-500/50 rounded-lg hover:bg-cyan-500/30 hover:shadow-lg hover:shadow-cyan-500/20 transition-all duration-300 font-semibold uppercase tracking-wider text-sm disabled:opacity-30 disabled:cursor-not-allowed flex items-center gap-2"
		>
			{#if isLoading}
				<div class="w-4 h-4 border-2 border-cyan-300 border-t-transparent rounded-full animate-spin"></div>
			{/if}
			{employee ? 'Update' : 'Create'} Employee
		</button>
	</div>
</form>

<style>
	.hologram-section {
		background: linear-gradient(135deg, rgba(10, 25, 47, 0.9) 0%, rgba(20, 35, 57, 0.9) 100%);
		border: 1px solid rgba(0, 255, 255, 0.3);
		border-radius: 0.5rem;
		padding: 1.5rem;
		box-shadow:
			0 8px 32px rgba(0, 0, 0, 0.4),
			0 0 20px rgba(0, 255, 255, 0.1);
	}

	.section-header {
		margin-bottom: 1.5rem;
		padding-bottom: 0.75rem;
		border-bottom: 1px solid rgba(0, 255, 255, 0.2);
	}

	.form-group {
		display: flex;
		flex-direction: column;
	}

	.form-label {
		font-size: 0.75rem;
		font-weight: 600;
		color: rgba(103, 232, 249, 0.7);
		text-transform: uppercase;
		letter-spacing: 0.05em;
		margin-bottom: 0.5rem;
		font-family: 'Courier New', monospace;
	}

	.form-input {
		background: rgba(15, 23, 42, 0.6);
		border: 1px solid rgba(100, 116, 139, 0.5);
		border-radius: 0.5rem;
		padding: 0.75rem;
		color: rgba(203, 213, 225, 1);
		font-size: 0.875rem;
		transition: all 0.3s;
		font-family: 'Courier New', monospace;
	}

	.form-input:focus {
		outline: none;
		border-color: rgba(6, 182, 212, 0.5);
		box-shadow: 0 0 0 3px rgba(6, 182, 212, 0.1);
		background: rgba(15, 23, 42, 0.8);
	}

	.form-input:disabled {
		opacity: 0.5;
		cursor: not-allowed;
	}

	.form-input::placeholder {
		color: rgba(148, 163, 184, 0.5);
	}

	select.form-input {
		cursor: pointer;
	}

	select.form-input option {
		background: rgba(15, 23, 42, 1);
		color: rgba(203, 213, 225, 1);
	}
</style>
