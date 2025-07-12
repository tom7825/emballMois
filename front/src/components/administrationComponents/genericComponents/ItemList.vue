<template>
  <div class="flex flex-col justify-start p-2 w-full text-center text-center">
    <div>
      <!-- Display the section title -->
      <a :class="textClass">{{ title }}</a>

      <!-- Display error message if present -->
      <div v-if="errorMessage" class="text-red-500 mt-2">
        <p>{{ errorMessage }}</p>
      </div>

      <!-- Render the list of items with pagination -->
      <ul class="m-2">
        <li v-for="item in paginatedItems" :key="item.id">
          <!-- Each item is passed to the reusable <Item> component -->
          <Item :item="item" :buttonClass="buttonClass" @modify="modifyItem" />
        </li>
      </ul>
    </div>

    <!-- Pagination component at the bottom of the list -->
    <div class="mt-auto">
      <Pagination 
        :currentPage="currentPage" 
        :totalItems="items.length" 
        :itemsPerPage="itemsPerPage"
        @pageChange="setPage" 
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { defineProps, defineEmits } from 'vue';
import { EMIT_TYPE } from '@/utils/constants/generalText';
import Item from './ItemInList.vue'; // Reusable item component
import Pagination from './PaginationList.vue'; // Pagination logic component

// Props received from the parent
const props = defineProps({
  title: {
    type: String,       // Title for the list section
    required: true
  },
  items: {
    type: Array,        // Array of items to display
    required: true
  },
  color: {
    type: String,       // Used to dynamically color buttons and text
    required: true
  },
  errorMessage: {
    type: String        // Optional error message
  }
});

// Dynamic styling classes using Tailwind and the color prop
const buttonClass = `ml-2 rounded-md bg-${props.color}-500 text-white hover:bg-${props.color}-700`;
const textClass = `text-${props.color}-500`;

// Events emitted to the parent
const emit = defineEmits([EMIT_TYPE.MODIFY]);

// Current pagination page
const currentPage = ref(1);

// Number of items displayed per page
const itemsPerPage = 8;

// Compute the current slice of items to display
const paginatedItems = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return props.items.slice(start, end);
});

// Handler to change page in the pagination
const setPage = (newPage) => {
  currentPage.value = newPage;
};

// Emit the modified item back to the parent
const modifyItem = (item) => {
  emit(EMIT_TYPE.MODIFY, item);
};
</script>