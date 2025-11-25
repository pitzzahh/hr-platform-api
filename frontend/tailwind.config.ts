import type { Config } from 'tailwindcss';

export default {
	content: ['./src/**/*.{html,js,svelte,ts}'],
	darkMode: 'media',
	theme: {
		extend: {
			fontFamily: {
				mono: ['"Courier New"', 'Courier', 'monospace']
			},
			colors: {
				primary: {
					light: '#3b82f6',
					dark: '#06b6d4'
				}
			},
			backgroundImage: {
				'cyber-grid-light':
					'linear-gradient(rgba(59, 130, 246, 0.08) 1px, transparent 1px), linear-gradient(90deg, rgba(59, 130, 246, 0.08) 1px, transparent 1px)',
				'cyber-grid-dark':
					'linear-gradient(rgba(0, 255, 255, 0.1) 1px, transparent 1px), linear-gradient(90deg, rgba(0, 255, 255, 0.1) 1px, transparent 1px)'
			},
			backgroundSize: {
				grid: '40px 40px'
			},
			animation: {
				'grid-move': 'grid-move 2s linear infinite',
				'scan-line': 'scan-line 8s linear infinite'
			},
			keyframes: {
				'grid-move': {
					'0%': { backgroundPosition: '0 0' },
					'100%': { backgroundPosition: '40px 40px' }
				},
				'scan-line': {
					'0%': { transform: 'translateY(0)' },
					'100%': { transform: 'translateY(calc(100vh - var(--header-height, 200px)))' }
				}
			}
		}
	},
	plugins: []
} satisfies Config;
