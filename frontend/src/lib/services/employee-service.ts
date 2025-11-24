import type {
	Employee,
	EmployeeCreateRequest,
	EmployeeUpdateRequest,
	EmployeeListResponse,
	ApiResponse
} from '$lib/types/employee';

class EmployeeService {
	private apiBase: string;

	constructor() {
		this.apiBase = import.meta.env.VITE_API_BASE || 'http://localhost:8080';
	}

	async getEmployees(page: number = 1, size: number = 10): Promise<EmployeeListResponse> {
		const response = await fetch(
			`${this.apiBase}/api/v1/employees?page=${page}&size=${size}`
		);
		if (!response.ok) {
			throw new Error(`Failed to fetch employees: ${response.statusText}`);
		}
		return response.json();
	}

	async getEmployeeById(id: string): Promise<ApiResponse<Employee>> {
		const response = await fetch(`${this.apiBase}/api/v1/employees/${id}`);
		if (!response.ok) {
			throw new Error(`Failed to fetch employee: ${response.statusText}`);
		}
		return response.json();
	}

	async createEmployee(employee: EmployeeCreateRequest): Promise<ApiResponse<Employee>> {
		const response = await fetch(`${this.apiBase}/api/v1/employees`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(employee)
		});
		if (!response.ok) {
			const error = await response.json().catch(() => ({ message: response.statusText }));
			throw new Error(error.message || 'Failed to create employee');
		}
		return response.json();
	}

	async updateEmployee(id: string, employee: EmployeeCreateRequest): Promise<ApiResponse<Employee>> {
		const response = await fetch(`${this.apiBase}/api/v1/employees/${id}`, {
			method: 'PUT',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(employee)
		});
		if (!response.ok) {
			const error = await response.json().catch(() => ({ message: response.statusText }));
			throw new Error(error.message || 'Failed to update employee');
		}
		return response.json();
	}

	async deleteEmployee(id: string): Promise<void> {
		const response = await fetch(`${this.apiBase}/api/v1/employees/${id}`, {
			method: 'DELETE'
		});
		if (!response.ok) {
			throw new Error(`Failed to delete employee: ${response.statusText}`);
		}
	}

	async archiveEmployee(id: string): Promise<ApiResponse<Employee>> {
		const response = await fetch(`${this.apiBase}/api/v1/employees/${id}/archive`, {
			method: 'PATCH'
		});
		if (!response.ok) {
			throw new Error(`Failed to archive employee: ${response.statusText}`);
		}
		return response.json();
	}
}

export const employeeService = new EmployeeService();
