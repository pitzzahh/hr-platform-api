export type Gender = 'MALE' | 'FEMALE' | 'OTHER';

export type CivilStatus = 'SINGLE' | 'MARRIED' | 'WIDOWED';

export interface Employee {
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
	gender: Gender;
	taxPayerIdentificationNumber: string;
	civilStatus: CivilStatus;
	bankAccountNumber: string | null;
	archived: boolean;
	userId: string | null;
	createdAt: string;
	updatedAt: string;
}

export interface EmployeeCreateRequest {
	employeeNumber: string;
	itemNumber: string;
	firstName: string;
	middleName?: string;
	lastName: string;
	photo?: string;
	dateOfBirth: string;
	email: string;
	phoneNumber: string;
	gender: Gender;
	taxPayerIdentificationNumber: string;
	civilStatus: CivilStatus;
	bankAccountNumber?: string;
}

export interface EmployeeUpdateRequest extends EmployeeCreateRequest {
	id: string;
}

export interface Pagination {
	page: number;
	size: number;
	totalElements: number;
	totalPages: number;
}

export interface ApiResponse<T> {
	timestamp: string;
	data: T;
	pagination?: Pagination;
}

export interface EmployeeListResponse {
	timestamp: string;
	data: Employee[];
	pagination: Pagination;
}
