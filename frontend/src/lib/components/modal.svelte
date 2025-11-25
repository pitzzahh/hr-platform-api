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
		class="modal-backdrop fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 dark:bg-black/70 backdrop-blur-sm"
		onclick={handleBackdropClick}
		onkeydown={handleKeydown}
		role="dialog"
		aria-modal="true"
		aria-labelledby="modal-title"
		tabindex="-1"
	>
		<div
			class="modal-container {sizeClasses[
				size
			]} w-full bg-gradient-to-br from-white/98 to-blue-50/95 dark:from-slate-950 dark:to-slate-900 border-2 border-blue-400/50 dark:border-cyan-500/60 shadow-2xl overflow-hidden transform transition-all duration-300 scale-100 opacity-100 backdrop-blur-lg"
		>
			<!-- Modal Header -->
			<div
				class="modal-header flex items-center justify-between px-6 py-4 bg-blue-100/80 dark:bg-slate-900/40 border-b border-blue-300/50 dark:border-cyan-500/40"
			>
				<h2
					id="modal-title"
					class="text-xl font-bold text-blue-700 dark:text-cyan-300 uppercase tracking-wider font-mono"
					style="text-shadow: 0 0 10px rgba(6, 182, 212, 0.3);"
				>
					[ {title} ]
				</h2>
				<button
					onclick={onClose}
					class="p-2 hover:bg-blue-200 dark:hover:bg-cyan-500/20 transition-colors duration-300 group"
					aria-label="Close modal"
				>
					<svg
						class="w-6 h-6 text-blue-600 dark:text-cyan-400 group-hover:text-blue-700 dark:group-hover:text-cyan-300 transition-colors"
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
			transform: translateY(-50px) scale(0.96);
			opacity: 0;
		}
		to {
			transform: translateY(0) scale(1);
			opacity: 1;
		}
	}

	.modal-container {
		box-shadow:
			0 20px 50px rgba(0, 0, 0, 0.3),
			0 0 0 1px rgba(59, 130, 246, 0.2) inset;
	}

	@media (prefers-color-scheme: dark) {
		.modal-container {
			box-shadow:
				0 20px 50px rgba(0, 0, 0, 0.6),
				0 0 60px rgba(6, 182, 212, 0.2),
				0 0 0 1px rgba(6, 182, 212, 0.3) inset;
		}
	}

	.custom-scrollbar::-webkit-scrollbar {
		width: 8px;
	}

	.custom-scrollbar::-webkit-scrollbar-track {
		background: rgba(226, 232, 240, 0.5);
		border-radius: 4px;
	}

	.custom-scrollbar::-webkit-scrollbar-thumb {
		background: rgba(59, 130, 246, 0.5);
		border-radius: 4px;
	}

	.custom-scrollbar::-webkit-scrollbar-thumb:hover {
		background: rgba(59, 130, 246, 0.7);
	}

	@media (prefers-color-scheme: dark) {
		.custom-scrollbar::-webkit-scrollbar-track {
			background: rgba(15, 23, 42, 0.5);
		}

		.custom-scrollbar::-webkit-scrollbar-thumb {
			background: rgba(6, 182, 212, 0.5);
		}

		.custom-scrollbar::-webkit-scrollbar-thumb:hover {
			background: rgba(6, 182, 212, 0.7);
		}
	}
</style>
