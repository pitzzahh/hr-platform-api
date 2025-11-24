<script lang="ts">
	interface Props {
		isOpen: boolean;
		title: string;
		onClose: () => void;
		size?: 'sm' | 'md' | 'lg' | 'xl';
		children?: import('svelte').Snippet;
	}

	let { isOpen = $bindable(false), title, onClose, size = 'lg', children }: Props = $props();

	const sizeClasses = {
		sm: 'max-w-md',
		md: 'max-w-2xl',
		lg: 'max-w-4xl',
		xl: 'max-w-6xl'
	};

	function handleBackdropClick(e: MouseEvent) {
		if (e.target === e.currentTarget) {
			onClose();
		}
	}

	function handleKeydown(e: KeyboardEvent) {
		if (e.key === 'Escape') {
			onClose();
		}
	}
</script>

{#if isOpen}
	<div
		class="modal-backdrop fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm"
		onclick={handleBackdropClick}
		onkeydown={handleKeydown}
		role="dialog"
		aria-modal="true"
		aria-labelledby="modal-title"
	>
		<div
			class="modal-container {sizeClasses[
				size
			]} w-full bg-gradient-to-br from-slate-900 to-blue-950 border-2 border-cyan-500/50 rounded-lg shadow-2xl shadow-cyan-500/20 overflow-hidden transform transition-all duration-300 scale-100 opacity-100"
		>
			<!-- Modal Header -->
			<div
				class="modal-header flex items-center justify-between px-6 py-4 bg-cyan-900/30 border-b border-cyan-500/30"
			>
				<h2 id="modal-title" class="text-xl font-bold text-cyan-300 uppercase tracking-wider">
					[ {title} ]
				</h2>
				<button
					onclick={onClose}
					class="p-2 hover:bg-cyan-500/20 rounded-lg transition-colors duration-300 group"
					aria-label="Close modal"
				>
					<svg
						class="w-6 h-6 text-cyan-400 group-hover:text-cyan-300 transition-colors"
						fill="none"
						stroke="currentColor"
						viewBox="0 0 24 24"
					>
						<path
							stroke-linecap="round"
							stroke-linejoin="round"
							stroke-width="2"
							d="M6 18L18 6M6 6l12 12"
						/>
					</svg>
				</button>
			</div>

			<!-- Modal Content -->
			<div class="modal-content p-6 max-h-[calc(100vh-200px)] overflow-y-auto custom-scrollbar">
				{#if children}
					{@render children()}
				{/if}
			</div>
		</div>
	</div>
{/if}

<style>
	.modal-backdrop {
		animation: fadeIn 0.2s ease-out;
	}

	@keyframes fadeIn {
		from {
			opacity: 0;
		}
		to {
			opacity: 1;
		}
	}

	.modal-container {
		animation: slideIn 0.3s ease-out;
	}

	@keyframes slideIn {
		from {
			transform: translateY(-50px) scale(0.95);
			opacity: 0;
		}
		to {
			transform: translateY(0) scale(1);
			opacity: 1;
		}
	}

	.custom-scrollbar::-webkit-scrollbar {
		width: 8px;
	}

	.custom-scrollbar::-webkit-scrollbar-track {
		background: rgba(15, 23, 42, 0.5);
		border-radius: 4px;
	}

	.custom-scrollbar::-webkit-scrollbar-thumb {
		background: rgba(6, 182, 212, 0.5);
		border-radius: 4px;
	}

	.custom-scrollbar::-webkit-scrollbar-thumb:hover {
		background: rgba(6, 182, 212, 0.7);
	}
</style>
