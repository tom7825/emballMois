<template>
  <div class="flex flex-col justify-start p-2 w-full text-center text-center">
    <div>
      <!-- Displays the list title -->
      <a :class="textClass">{{ title }}</a>

      <!-- Displays an error message if present -->
      <div v-if="errorMessage" class="text-red-500 mt-2">
        <p>{{ errorMessage }}</p>
      </div>

      <!-- Displays the paginated list of items -->
      <ul class="m-2">
        <li v-for="item in paginatedItems" :key="item.idReference">
          <!-- Each item is rendered via the ReferenceItem component -->
          <ReferenceItem 
            :reference="item" 
            :buttonClass="buttonClass" 
            @modify="modifyReferenceEvent" 
          />
        </li>
      </ul>
    </div>

    <!-- Pagination controls for navigating through pages -->
    <div class="mt-auto">
      <Pagination 
        :currentPage="currentPage" 
        :totalItems="references.length" 
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
import Pagination from '../genericComponents/PaginationList.vue';
import ReferenceItem from './ReferenceItem.vue';

// Component props definition
const props = defineProps({
  title: {
    type: String,
    required: true
  },
  references: {
    type: Array,
    required: true
  },
  errorMessage: {
    type: String,
    required: true
  },
  color: {
    type: String,
    required: true
  }
});

// Event emitters
const emit = defineEmits([EMIT_TYPE.MODIFY]);


// Dynamic classes based on color prop
const buttonClass = `ml-2 text-sm rounded-md bg-${props.color}-500 text-white hover:bg-${props.color}-700`;
const textClass = `text-${props.color}-500`;

// Pagination state
const currentPage = ref(1);


// Number of items to display per page
const itemsPerPage = 8;

// Computes the list of items for the current page
const paginatedItems = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return props.references.slice(start, end);
});

// Sets the current page when pagination is used
const setPage = (newPage) => {
  currentPage.value = newPage;
};

const modifyReferenceEvent = () =>{
  emit(EMIT_TYPE.MODIFY)
}
</script>
