<template>
  <!-- Main container for the search form -->
  <div class="w-full text-center">
    <!-- Title with dynamic object type and color -->
    <a :class="textClass">Recherche d'une {{ objectType }}</a>
    <!-- Search input field with binding and dynamic styling -->
    <form @submit.prevent="search" class="mt-2">
      <input v-model="searchQuery" :placeholder="searchPlaceholder"
         :class="inputClass" maxlength="20">
    </form>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { getPlaceHolderSearchForm } from '@/utils/constants/administrationText';

// Props received from the parent component
const props = defineProps({
  objectType: {
    type: String,
    required: true
  },
  color: {
    type: String,
    required: true
  },
  activeItems: {
    type: Array,
    required: true
  },
  archivedItems: {
    type: Array,
    required: true
  },
});

// Emit event to notify parent component of search results
const emit = defineEmits(['search']);

// Reactive variable bound to the search input field
const searchQuery = ref('');

// Stores filtered active and archived items
const filteredActiveItems = ref([]);
const filteredArchivedItems = ref([]);

// Placeholder text dynamically built from object type
const searchPlaceholder = getPlaceHolderSearchForm(props.objectType)

// Tailwind CSS classes with dynamic ring color
const inputClass = `px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-${props.color}-500`;
const textClass = `text-${props.color}-500 col-span-12`;

// Search logic: filters active and archived items based on search query
const search = () => {
  const query = searchQuery.value.trim().toLowerCase();

  // Default state: copy full list
  filteredActiveItems.value = [...props.activeItems];
  filteredArchivedItems.value = [...props.archivedItems];

  // If query is non-empty, filter by name
  if (query.length > 0) {
    filteredActiveItems.value = props.activeItems.filter(item =>
      item.name.toLowerCase().includes(query)
    );
    filteredArchivedItems.value = props.archivedItems.filter(item =>
      item.name.toLowerCase().includes(query)
    );
  }

  // Emit the filtered lists to the parent component
  emit('search', filteredActiveItems.value, filteredArchivedItems.value);
};

// Automatically re-run the search function when searchQuery changes
watch(searchQuery, search);

// Expose search function to be called manually by the parent component
defineExpose({
  applyFilters: search
});
</script>