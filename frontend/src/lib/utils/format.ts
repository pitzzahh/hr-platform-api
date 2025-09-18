/**
 * Formats a number as currency.
 *
 * @param amount - The number to be formatted.
 * @param locale - Optional. The locale to use for formatting. Defaults to 'en-ph'.
 * @returns The formatted currency string.
 */
export function formatCurrency(amount: number, locale: string = "en-ph"): string {
    // Define currency format options
    const options: Intl.NumberFormatOptions = {
        style: 'currency',
        currency: 'PHP' // Change this to any other currency code as needed
    };

    // Create a new Intl.NumberFormat instance with the provided locale and options
    const formatter = new Intl.NumberFormat(locale, options);

    // Format the amount and return it
    return formatter.format(amount);
}

export const formatDate = (date?: Date | string | null, lang: string = 'en-ph') => {
    return date
        ? new Date(date).toLocaleDateString(lang, {year: 'numeric', month: 'long', day: 'numeric'})
        : 'N/A';
};

export function formatDateTime(date?: Date | string | null, lang: string = 'en-ph') {
    return date
        ? new Date(date).toLocaleDateString(lang, {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: 'numeric',
            minute: 'numeric'
        })
        : 'N/A';
}