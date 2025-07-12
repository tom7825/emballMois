<template>
  <div ref="dropdownContainer" class="inline-block w-full">
    <div class="relative w-full" id="StorageAreaMultiSelect">
      <!-- Toggle -->
      <div @click="toggleDropdown" class="bg-gray-200 px-3 py-2 rounded cursor-pointer " :class="selectClass"
        tabindex="0">
        <a class="flex items-center justify-between w-full">
          <span class="truncate block w-full">
            {{ selectedLabelsDisplay }}
          </span>
          <DownArrow height="15" />
        </a>
      </div>

      <!-- Dropdown with transition -->
      <transition name="fade">
        <div v-if="isDropdownOpen"
          class="absolute z-10 mt-2 bg-white max-h-40 overflow-y-auto border rounded-md shadow-lg w-full"
          role="listbox">
          <!-- Empty -->
          <div v-if="!filteredOptions.length" class="p-2 text-sm text-gray-400">
            Aucune zone disponible
          </div>

          <!-- Options -->
          <div v-for="option in filteredOptions" :key="option.id ?? option.name"
            class="flex items-center p-2 hover:bg-gray-100">
            <input type="checkbox" :id="`area-${option.id ?? option.name}`"
              :value="option.name" v-model="selectedArea" class="mr-2" />
            <label :for="`area-${option.id ?? option.name}`" class="text-sm">
              {{ option.name }}
            </label>
          </div>

          <!-- Clear -->
          <div class="p-2 border-t">
            <button type="button" @click.stop="clearSelection" class="text-sm text-red-500">
              Effacer la sélection
            </button>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ChevronDown as DownArrow } from 'lucide-vue-next';
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue';

const props = defineProps({
  options: {
    type: Array,
    required: true,
    default: () => []
  },
  modelValue: {
    type: Array,
    default: () => []
  },
  placeholder: {
    type: String,
    default: 'Zone de stockage'
  },
  selectClass: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['update:modelValue']);

const isDropdownOpen = ref(false);
const selectedArea = ref([...props.modelValue]);

// Ne garder que les options avec un nom défini
const filteredOptions = computed(() =>
  props.options.filter(opt => !!opt.name)
);

// Labels des zones sélectionnées
const selectedLabels = computed(() => {
  return filteredOptions.value
    .filter(opt => selectedArea.value.includes(opt.name))
    .map(opt => opt.name);
});

// Affichage des labels avec fallback sur le nombre
const selectedLabelsDisplay = computed(() => {
  const labels = selectedLabels.value;
  if (!labels.length) return props.placeholder;
  if (labels.length <= 3) return labels.join(', ');
  return `${labels.length} zones sélectionnées`;
});

// Toggle
const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value;
};

// Reset
const clearSelection = () => {
  selectedArea.value = [];
  emit('update:modelValue', []);
};

// Clic extérieur
const dropdownContainer = ref(null);
const handleClickOutside = (event) => {
  if (dropdownContainer.value && !dropdownContainer.value.contains(event.target)) {
    isDropdownOpen.value = false;
  }
};

onMounted(() => document.addEventListener('click', handleClickOutside));
onBeforeUnmount(() => document.removeEventListener('click', handleClickOutside));

watch(selectedArea, (newVal) => {
  if (!arraysEqual(newVal, props.modelValue)) {
    emit('update:modelValue', newVal);
  }
});

watch(() => props.modelValue, (newVal) => {
  if (!arraysEqual(newVal, selectedArea.value)) {
    selectedArea.value = [...newVal];
  }
});

const arraysEqual = (a, b) =>
  Array.isArray(a) && Array.isArray(b) &&
  a.length === b.length &&
  a.every((val, i) => val === b[i]);

</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
