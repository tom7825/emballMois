<template>
  <div class="relative" ref="menuRef">
    <!-- Menu toggle button -->
    <button @click="toggleMenu" class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-700">
      <MenuIcon />
    </button>

    <!-- Dropdown menu (visible only when isMenuOpen is true) -->
    <div v-if="isMenuOpen" class="absolute top-12 left-0 bg-white border rounded shadow-md w-auto min-w-[150px] z-50">
      <ul>

        
        <!-- First-level menu item -->
         <li class="px-4 py-2 hover:bg-gray-200 cursor-pointer" @click="handleMenuItemClick(COMPONENTS.STOCK_FORECAST)">
          État des stocks
        </li>
        <li class="px-4 py-2 hover:bg-gray-200 cursor-pointer" @click="handleMenuItemClick(COMPONENTS.INVENTORY_HISTORY)">
          Historique
        </li>

        <!-- Second-level dropdown menu with submenu -->
        <li
          class="px-4 py-2 hover:bg-gray-200 cursor-pointer relative"
          @mouseenter="toggleSubMenu"
          @mouseleave="toggleSubMenu"
        >
          <a class="flex items-center justify-between space-x-2">
            Administration
            <LeftArrow class="ml-2" width="15" height="15"/>
          </a>

          <!-- Submenu that appears on hover -->
          <div
            v-if="isSubMenuOpen"
            class="absolute top-0 left-full bg-white border rounded shadow-md w-auto min-w-[150px] z-50"
          >
            <ul>
              <!-- Submenu options -->
              <li class="px-4 py-2 hover:bg-gray-200 cursor-pointer" @click="handleMenuItemClick(COMPONENTS.CATEGORY_ADMIN)">
                Catégorie
              </li>
              <li class="px-4 py-2 hover:bg-gray-200 cursor-pointer" @click="handleMenuItemClick(COMPONENTS.AREA_ADMIN)">
                Zone de stockage
              </li>
              <li class="px-4 py-2 hover:bg-gray-200 cursor-pointer" @click="handleMenuItemClick(COMPONENTS.REFERENCE_ADMIN)">
                Références
              </li>
            </ul>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { Menu as MenuIcon, StepForward as LeftArrow } from 'lucide-vue-next';
import { ref, onMounted } from 'vue';
import { defineEmits } from 'vue';
import { COMPONENTS, EMIT_TYPE } from '@/utils/constants/generalText';

// Emits an event when a menu item is selected
const emit = defineEmits([EMIT_TYPE.CHANGE_COMPONENT]);

// Boolean to control the main menu visibility
const isMenuOpen = ref(false);

// Boolean to control the submenu visibility
const isSubMenuOpen = ref(false);

// Reference to the menu container for outside click detection
const menuRef = ref(null);

// Toggles the main dropdown menu
const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value;
};

// Toggles the submenu on hover
const toggleSubMenu = () => {
  isSubMenuOpen.value = !isSubMenuOpen.value;
};

// Closes the menu if a click happens outside of it
const handleClickOutside = (event) => {
  if (menuRef.value && !menuRef.value.contains(event.target)) {
    isMenuOpen.value = false;
    isSubMenuOpen.value = false;
  }
};

// Attach the outside click listener when component is mounted
onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

// Handles menu item selection and emits component change
const handleMenuItemClick = (component) => {
  emit(EMIT_TYPE.CHANGE_COMPONENT, component);
  isMenuOpen.value = false;
  isSubMenuOpen.value = false;
};
</script>
