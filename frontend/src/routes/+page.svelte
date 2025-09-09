<script module lang="ts">
    type SalaryData = {
        id: string;
        step: number;
        amount: number;
        salaryGradeId: string;
        createdAt: string;
        updatedAt: string;
    };

    type SalaryGrade = {
        id: string;
        legalBasis: string;
        tranche: number;
        effectiveDate: string;
        salaryGrade: number;
        createdAt: string;
        updatedAt: string;
        salaryData: SalaryData[];
    };

    type ApiResponse = {
        timestamp: string;
        data: SalaryGrade[];
        pagination: {
            page: number;
            size: number;
            totalElements: number;
            totalPages: number;
        };
    };

    class ApiPoller {
        response = $state<ApiResponse>({
            timestamp: '',
            data: [],
            pagination: {
                page: 1,
                size: 10,
                totalElements: 0,
                totalPages: 0
            }
        });
        page = $state<number>(1);
        size = $state<number>(10);
        private intervalId: number | null = null;
        private readonly intervalMs: number;
        private readonly apiBase: string;

        constructor(intervalMs: number = 1500) {
            this.intervalMs = intervalMs;
            this.apiBase = import.meta.env.MODE === 'development'
                ? 'http://localhost:8080'
                : import.meta.env.VITE_API_BASE || 'https://your-backend.com'; // Fallback to prod URL
        }

        async fetchData() {
            try {
                const res = await fetch(
                    `${this.apiBase}/api/v1/salary-grades?page=${this.page}&size=${this.size}&includeSalaryData=true`
                );
                const apiResponse = (await res.json()) as ApiResponse;
                this.response = {
                    ...apiResponse,
                    data: apiResponse.data.map(grade => ({
                        ...grade,
                        salaryData: [...grade.salaryData].sort((a, b) => a.step - b.step)
                    }))
                };
                console.log('Fetched data:', this.response);
            } catch (error) {
                console.error('API fetch error:', error);
            }
        }

        setPage(newPage: number) {
            this.page = newPage;
            this.fetchData();
        }

        start() {
            this.fetchData();
            this.intervalId = setInterval(() => this.fetchData(), this.intervalMs);
        }

        stop() {
            if (this.intervalId) clearInterval(this.intervalId);
        }
    }

    const poller = new ApiPoller(5000);
</script>

<script lang="ts">
    import {formatCurrency, formatDate, formatDateTime} from "$lib/utils/format";

    $effect.pre(() => {
        poller.start();
        return () => poller.stop();
    });
</script>

<svelte:head>
    <title>HR Platform - Salary Grades</title>
    <meta
            name="description"
            content="View and manage salary grades and compensation data for HR platform"
    />
</svelte:head>

<div class="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50">
    <!-- Header Section -->
    <div class="bg-white shadow-sm border-b border-gray-200">
        <div class="container mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <div class="text-center">
                <h1 class="text-4xl font-bold text-gray-900 mb-2">HR Platform</h1>
                <p class="text-xl text-gray-600 mb-4">Salary Grades Management System</p>
                <div
                        class="flex flex-col sm:flex-row gap-4 justify-center items-center text-sm text-gray-500"
                >
                    {#if poller.response.timestamp}
                        <div class="flex items-center gap-2">
                            <svg class="w-4 h-4" stroke="currentColor" viewBox="0 0 24 24">
                                <path
                                        stroke-linecap="round"
                                        stroke-linejoin="round"
                                        stroke-width="2"
                                        d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"
                                />
                            </svg>
                            Last Updated: {formatDateTime(new Date(poller.response.timestamp))}
                        </div>
                    {/if}
                    <div class="flex items-center gap-2">
                        <svg class="w-4 h-4" stroke="currentColor" viewBox="0 0 24 24">
                            <path
                                    stroke-linecap="round"
                                    stroke-linejoin="round"
                                    stroke-width="2"
                                    d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                            />
                        </svg>
                        Total Grades: {poller.response.pagination.totalElements}
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Main Content -->
    <div class="container mx-auto px-4 sm:px-6 lg:px-8 py-8 max-w-6xl">
        {#if poller.response.data.length === 0}
            <div class="flex flex-col items-center justify-center py-16">
                <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mb-4"></div>
                <p class="text-gray-500 text-lg">Loading salary grades...</p>
            </div>
        {:else}
            <div class="grid gap-6 md:gap-8">
                {#each poller.response.data as response}
                    <div
                            class="bg-white shadow-lg rounded-2xl overflow-hidden border border-gray-200 hover:shadow-xl transition-all duration-300 hover:-translate-y-1"
                    >
                        <!-- Card Header -->
                        <div class="bg-gradient-to-r from-blue-600 to-blue-700 px-6 py-4">
                            <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
                                <h2 class="text-2xl font-bold text-white mb-2 sm:mb-0">
                                    Salary Grade {response.salaryGrade}
                                </h2>
                                <div class="flex items-center gap-2 text-blue-100">
                                    <svg class="w-4 h-4" stroke="currentColor" viewBox="0 0 24 24">
                                        <path
                                                stroke-linecap="round"
                                                stroke-linejoin="round"
                                                stroke-width="2"
                                                d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                                        />
                                    </svg>
                                    Effective: {formatDate(response.effectiveDate)}
                                </div>
                            </div>
                        </div>

                        <!-- Card Content -->
                        <div class="p-6">
                            <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
                                <div class="bg-gray-50 rounded-lg p-4">
                                    <h3 class="text-sm font-semibold text-gray-500 uppercase tracking-wide mb-1">
                                        Legal Basis
                                    </h3>
                                    <p class="text-gray-900 font-medium">{response.legalBasis}</p>
                                </div>
                                <div class="bg-gray-50 rounded-lg p-4">
                                    <h3 class="text-sm font-semibold text-gray-500 uppercase tracking-wide mb-1">
                                        Tranche
                                    </h3>
                                    <p class="text-gray-900 font-medium">{response.tranche}</p>
                                </div>
                            </div>

                            <div class="border-t border-gray-200 pt-6">
                                <div class="flex items-center gap-2 mb-4">
                                    <svg class="w-5 h-5 text-green-600" stroke="currentColor" viewBox="0 0 24 24">
                                        <path
                                                stroke-linecap="round"
                                                stroke-linejoin="round"
                                                stroke-width="2"
                                                d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1"
                                        />
                                    </svg>
                                    <h3 class="text-lg font-semibold text-gray-800">Salary Steps</h3>
                                    <span
                                            class="bg-blue-100 text-blue-800 text-xs font-medium px-2.5 py-0.5 rounded-full"
                                    >{response.salaryData.length} steps</span
                                    >
                                </div>
                                <div class="grid gap-3">
                                    {#each response.salaryData as salary}
                                        <div
                                                class="bg-gradient-to-r from-green-50 to-emerald-50 border border-green-200 rounded-lg p-4 hover:from-green-100 hover:to-emerald-100 transition-colors"
                                        >
                                            <div
                                                    class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2"
                                            >
                                                <div class="flex items-center gap-3">
                                                    <div
                                                            class="bg-green-600 text-white rounded-full w-8 h-8 flex items-center justify-center text-sm font-bold"
                                                    >
                                                        {salary.step}
                                                    </div>
                                                    <div>
                                                        <p class="font-semibold text-gray-900 text-lg">
                                                            {formatCurrency(salary.amount)}
                                                        </p>
                                                        <p class="text-sm text-gray-600">Step {salary.step}</p>
                                                    </div>
                                                </div>
                                                <div class="text-sm text-gray-500">
                                                    Created: {formatDateTime((salary.createdAt))}
                                                </div>
                                            </div>
                                        </div>
                                    {:else}
                                        <div class="border-t border-gray-200 pt-6">
                                            <div class="text-center py-8">
                                                <svg
                                                        class="w-12 h-12 text-gray-400 mx-auto mb-3"
                                                        stroke="currentColor"
                                                        viewBox="0 0 24 24"
                                                >
                                                    <path
                                                            stroke-linecap="round"
                                                            stroke-linejoin="round"
                                                            stroke-width="2"
                                                            d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                                                    />
                                                </svg>
                                                <p class="text-gray-500">No salary steps available for this grade</p>
                                            </div>
                                        </div>
                                    {/each}
                                </div>
                            </div>
                        </div>
                    </div>
                {/each}
            </div>

            <!-- Pagination -->
            {#if poller.response.pagination.totalPages > 1}
                <div class="mt-8 flex justify-center items-center gap-2">
                    <button
                            onclick={() => poller.setPage(poller.page - 1)}
                            disabled={poller.page === 1}
                            class="px-4 py-2 bg-blue-600 text-white rounded-lg disabled:bg-gray-300 disabled:cursor-not-allowed"
                    >
                        Previous
                    </button>
                    <span class="text-gray-600"
                    >Page {poller.response.pagination.page} of {poller.response.pagination.totalPages}</span
                    >
                    <button
                            onclick={() => poller.setPage(poller.page + 1)}
                            disabled={poller.page === poller.response.pagination.totalPages}
                            class="px-4 py-2 bg-blue-600 text-white rounded-lg disabled:bg-gray-300 disabled:cursor-not-allowed"
                    >
                        Next
                    </button>
                </div>
            {/if}

            <!-- Footer Info -->
            <div class="mt-12 text-center">
                <div class="bg-white rounded-lg shadow p-6 max-w-md mx-auto">
                    <h3 class="text-lg font-semibold text-gray-900 mb-2">System Information</h3>
                    <div class="space-y-2 text-sm text-gray-600">
                        <p>Platform designed for efficient HR management</p>
                        <p>Real-time salary grade tracking and administration</p>
                        <p class="text-xs text-gray-400 mt-4">© 2025 HR Platform API</p>
                    </div>
                </div>
            </div>
        {/if}
    </div>
</div>
