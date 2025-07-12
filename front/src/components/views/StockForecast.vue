<template>
  <div class="mx-6 p-4 w-full">
    <h1 class="text-2xl font-bold mb-4 text-center">État des stocks</h1>

    <div class="w-full flex flex-col gap-4 mb-6">

      <!-- Ligne 1 : Filtres & Tri -->
      <div class="flex flex-col sm:flex-row flex-wrap gap-4 justify-center">
        <!-- Filtre catégorie -->
        <select v-model="selectedCategory" class="px-3 py-2 border rounded shadow-sm w-full sm:w-auto">
          <option value="">Toutes les catégories</option>
          <option v-for="category in uniqueCategories" :key="category" :value="category">
            {{ category }}
          </option>
        </select>

        <!-- Filtre jours restants -->
        <select v-model="selectedDayFilter" class="px-3 py-2 border rounded shadow-sm w-full sm:w-auto">
          <option value="">Tous les jours</option>
          <option value="inf">♾️ (stock stable)</option>
          <option value="lt15">Moins de 15 jours</option>
          <option value="lt30">Moins de 30 jours</option>
          <option value="gte30">30 jours et plus</option>
        </select>

        <!-- Tri -->
        <select v-model="selectedSort" class="px-3 py-2 border rounded shadow-sm w-full sm:w-auto">
          <option value="daysAsc">Tri : Jours croissants</option>
          <option value="daysDesc">Tri : Jours décroissants</option>
          <option value="category">Tri : Catégorie (A → Z)</option>
        </select>
      </div>

      <!-- Ligne 2 : Recherche -->
      <div class="flex justify-center">
        <input v-model="searchTerm" type="text" placeholder="Rechercher une référence..."
          class="w-full md:w-1/2 px-3 py-2 border rounded shadow-sm" />
      </div>
    </div>

    <p v-if="!loading && filteredItems.length === 0">Aucune référence trouvée.</p>

    <div class="grid grid-cols-1 sm:grid-cols-3 lg:grid-cols-4 gap-6" v-if="!loading">
      <div v-for="(item, index) in visibleItems" :key="index"
        class="p-4 border rounded-lg shadow-sm flex flex-col gap-2 hover:shadow-md transition-transform hover:scale-[1.01]"
        :class="getCardColor(item.remainingDays)">
        <div class="flex justify-between items-center">
          <h2 class="text-lg font-semibold text-gray-800">{{ item.referenceName }}</h2>
          <component :is="getStatusIcon(item.remainingDays)" class="w-5 h-5"
            :class="getStatusIconColor(item.remainingDays)" />
        </div>

        <div class="text-sm text-gray-700 space-y-1">
          <p><span class="font-medium">Stock actuel :</span> {{ item.currentStock }}</p>
          <p><span class="font-medium">Consommation / jour : </span> {{ item.dailyConsumption.toFixed(2) }}</p>
          <p class="font-bold">
            <span>Jours restants : </span>
            <span v-if="isInfinite(item.remainingDays)">♾️</span>
            <span v-else>{{ item.remainingDays.toFixed(1) }} jours</span>
          </p>
        </div>
      </div>
    </div>

    <p v-if="loading">Chargement des données...</p>
  </div>
</template>

<script setup>
import { Flame, Hourglass, CheckCircle, Infinity as InfinityIcon } from 'lucide-vue-next';
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { fetchStockForecast } from '@/controller/stockForecastController';
import { messageHandler } from '../../utils/messageManager/messageHandler';

const allItems = ref([]);
const displayedCount = ref(20);
const searchTerm = ref('');
const loading = ref(true);
const selectedCategory = ref('');
const selectedDayFilter = ref('');
const selectedSort = ref('daysAsc');

const isInfinite = (value) => !isFinite(value);

const getStatusIcon = (days) => {
  if (!isFinite(days)) return InfinityIcon;
  if (days < 10) return Flame;
  if (days < 30) return Hourglass;
  return CheckCircle;
};

const uniqueCategories = computed(() => {
  const allCategories = allItems.value.map(item => item.categoryName || 'Inconnue');
  return [...new Set(allCategories)].sort();
});

const getStatusIconColor = (days) => {
  if (!isFinite(days)) return 'text-gray-400';
  if (days < 15) return 'text-red-600';
  if (days < 30) return 'text-yellow-500';
  return 'text-green-600';
};

const getCardColor = (days) => {
  if (!isFinite(days)) return 'bg-gray-50';
  if (days < 15) return 'bg-red-50';
  if (days < 30) return 'bg-yellow-50';
  return 'bg-green-50';
};

const filteredItems = computed(() => {
  let items = allItems.value;

  // Filtre par nom
  if (searchTerm.value) {
    items = items.filter(item =>
      item.referenceName.toLowerCase().includes(searchTerm.value.toLowerCase())
    );
  }

  // Filtre par catégorie
  if (selectedCategory.value) {
    items = items.filter(item => item.categoryName === selectedCategory.value);
  }

  // Filtre par jours restants
  if (selectedDayFilter.value === 'inf') {
    items = items.filter(item => !isFinite(item.remainingDays));
  } else if (selectedDayFilter.value === 'lt15') {
    items = items.filter(item => item.remainingDays < 10);
  } else if (selectedDayFilter.value === 'lt30') {
    items = items.filter(item => item.remainingDays < 30);
  } else if (selectedDayFilter.value === 'gte30') {
    items = items.filter(item => item.remainingDays >= 30);
  }

  // Tri
  if (selectedSort.value === 'daysAsc') {
    items.sort((a, b) => {
      const aInf = !isFinite(a.remainingDays);
      const bInf = !isFinite(b.remainingDays);
      if (aInf && !bInf) return 1;
      if (!aInf && bInf) return -1;
      return a.remainingDays - b.remainingDays;
    });
  } else if (selectedSort.value === 'daysDesc') {
    items.sort((a, b) => {
      const aInf = !isFinite(a.remainingDays);
      const bInf = !isFinite(b.remainingDays);
      if (aInf && !bInf) return -1;
      if (!aInf && bInf) return 1;
      return b.remainingDays - a.remainingDays;
    });
  } else if (selectedSort.value === 'category') {
    items.sort((a, b) => (a.categoryName || '').localeCompare(b.categoryName || ''));
  }

  return items;
});

const visibleItems = computed(() => {
  return filteredItems.value.slice(0, displayedCount.value);
});

const fetchForecast = async () => {
  try {
    allItems.value = await fetchStockForecast();
    messageHandler('Etat des stocks chargés avec succès.', 'success', 4000);
  } catch (err) {
    messageHandler(err.message, 'error', 6000);
  } finally {
    loading.value = false;
  }
};

const handleScroll = () => {
  const scrollPosition = window.innerHeight + window.scrollY;
  const bottom = document.documentElement.offsetHeight - 200;
  if (scrollPosition >= bottom && displayedCount.value < filteredItems.value.length) {
    displayedCount.value += 20;
  }
};

onMounted(() => {
  fetchForecast();
  window.addEventListener('scroll', handleScroll);
});

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>
