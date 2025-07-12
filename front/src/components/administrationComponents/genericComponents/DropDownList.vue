<template>
  <!-- Container for the dropdown component -->
  <div ref="dropdownContainer">
    <!-- Dropdown trigger element -->
    <div @click="toggleDropdown" :class="selectClass" v-bind="$attrs" class="bg-gray-200" tabindex="0">
      <a class="flex items-center justify-between space-x-2 w-full">
        <!-- Display the selected value or a placeholder -->
        <span class="truncate block w-full">{{ selectedValue || placeholder }}</span>
        <DownArrow height="15" />
      </a>
    </div>

    <!-- Dropdown content, shown when open -->
    <div v-if="isDropdownOpen" class="absolute z-10 bg-white border mt-1 max-h-60 overflow-auto">
      <!-- Search input to filter options -->
      <input v-model="searchQuery" type="text" placeholder="Rechercher..." class="p-2 border-b outline-none" />

      <!-- Render matching options -->
      <div v-for="option in filteredOptions" :key="option.name" @click="selectOption(option.name)"
        class="p-2 hover:bg-gray-100 cursor-pointer">
        {{ option.name }}
      </div>

      <!-- Message shown when no results match the query -->
      <div v-if="filteredOptions.length === 0" class="p-2 text-gray-500">
        Aucun résultat trouvé.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ChevronDown as DownArrow } from 'lucide-vue-next';
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue';

// Props received from parent

const props = defineProps({
  options: {
    type: Array,
    required: true,
    default: () => []
  },
  placeholder: String,
  selectClass: {
    type: String,
    default: ""
  },
  reset: Boolean,
  modelValue: String
});



// Declare event emitter for v-model binding
const emit = defineEmits(['update:modelValue']);

// Holds the currently selected value
const selectedValue = ref(props.modelValue || "");

// Controls whether the dropdown is visible
const isDropdownOpen = ref(false);

// Search term for filtering options
const searchQuery = ref("");

// Reference to the dropdown DOM element
const dropdownContainer = ref(null);

// Toggles dropdown visibility, resets search when opened
const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value;
  if (isDropdownOpen.value) {
    searchQuery.value = "";
  }
};

// Filters options based on search input
const filteredOptions = computed(() => {
  if (!searchQuery.value) return props.options;
  return props.options.filter(opt =>
    opt.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

// Selects an option and closes the dropdown
const selectOption = (value) => {
  if (selectedValue.value !== value) {
    selectedValue.value = value;
    emit('update:modelValue', value);
    isDropdownOpen.value = false;

  }
};

// Closes dropdown when user clicks outside of it
const handleClickOutside = (event) => {
  if (dropdownContainer.value && !dropdownContainer.value.contains(event.target)) {
    isDropdownOpen.value = false;
  }
};

// Register click listener on mount
onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

// Remove listener before component is destroyed
onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});

// Watch for external reset and clear value accordingly
watch(() => props.reset, (newReset) => {
  if (newReset) {
    selectedValue.value = "";
    emit('update:modelValue', "");
  }
});

// Sync local state with prop if changed externally
watch(() => props.modelValue, (newVal) => {
  selectedValue.value = newVal;
});
</script>
