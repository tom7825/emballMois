// Format a date to display as "Month Year" in French
const mois = ["Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"];
export const getMonthName = (date) => {
    if (!date || isNaN(date.getTime())) return '';
    return `${mois[date.getMonth()]} ${date.getFullYear()}`;
}

export function formatDate(dateString) {
  const date = new Date(dateString);
  return date.toLocaleDateString('fr-FR', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  });
}