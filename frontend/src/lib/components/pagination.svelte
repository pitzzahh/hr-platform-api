<script lang="ts">
	import type { Pagination as PaginationType } from '$lib/types/employee';

	interface Props {
		pagination: PaginationType;
		onPageChange: (page: number) => void;
		isLoading?: boolean;
	}

	let { pagination, onPageChange, isLoading = false }: Props = $props();
</script>

<div class="flex justify-center items-center gap-4">
	<button
		onclick={() => onPageChange(pagination.page - 1)}
		disabled={pagination.page === 1 || isLoading}
		class="px-6 py-3 bg-cyan-500/20 text-cyan-300 border border-cyan-500/50 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed hover:bg-cyan-500/30 hover:shadow-lg hover:shadow-cyan-500/20 transition-all duration-300 font-semibold uppercase tracking-wider text-sm"
	>
		← PREV
	</button>

	<div
		class="px-6 py-3 bg-slate-800/60 border border-cyan-500/30 rounded-lg text-cyan-300 font-mono"
	>
		<span class="text-cyan-500">PAGE</span>
		{pagination.page}
		<span class="text-cyan-500/50">/</span>
		{pagination.totalPages}
	</div>

	<button
		onclick={() => onPageChange(pagination.page + 1)}
		disabled={pagination.page === pagination.totalPages || isLoading}
		class="px-6 py-3 bg-cyan-500/20 text-cyan-300 border border-cyan-500/50 rounded-lg disabled:opacity-30 disabled:cursor-not-allowed hover:bg-cyan-500/30 hover:shadow-lg hover:shadow-cyan-500/20 transition-all duration-300 font-semibold uppercase tracking-wider text-sm"
	>
		NEXT →
	</button>
</div>
