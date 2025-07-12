<template>
    <!-- Container for pagination controls -->
    <div class="flex justify-between w-full mt-4">
  
      <!-- Previous button (only visible if previous page exists) -->
      <button v-if="canPrev"
        @click="prevPage"
        class="px-4 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-700">
        Précédent
      </button>
  
      <!-- Spacer to align 'Suivant' button when 'Précédent' is hidden -->
      <div v-if="!(canPrev)" class="w-16"></div>
  
      <!-- Next button (only visible if next page exists) -->
      <button v-if="canNext"
        @click="nextPage"
        class="px-4 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-700">
        Suivant
      </button>
  
      <!-- Spacer to align 'Précédent' button when 'Suivant' is hidden -->
      <div v-if="!(canNext)" class="w-16"></div>
  
    </div>
  </template>
  
  <script setup>
  import { defineProps, defineEmits, computed } from 'vue';
  
  // Props received from the parent component
  const props = defineProps({
    currentPage: {
      type: Number,       // Current page number
      required: true
    },
    totalItems: {
      type: Number,       // Total number of items to paginate
      required: true
    },
    itemsPerPage: {
      type: Number,       // Number of items per page
      required: true
    }
  });
  
  // Emit event to notify parent of page change
  const emit = defineEmits(['pageChange']);
  
  // Determine whether there is a previous page
  const canPrev = computed(() => props.currentPage > 1);
  
  // Determine whether there is a next page
  const canNext = computed(() => props.currentPage * props.itemsPerPage < props.totalItems);
  
  // Emit previous page number if applicable
  const prevPage = () => {
    if (canPrev.value) {
      emit('pageChange', props.currentPage - 1);
    }
  };
  
  // Emit next page number if applicable
  const nextPage = () => {
    if (canNext.value) {
      emit('pageChange', props.currentPage + 1);
    }
  };
  </script>
  