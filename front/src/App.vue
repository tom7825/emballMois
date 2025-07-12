<template>
  <!-- Root container with full-screen height and minimum width -->
  <div class="min-h-screen flex flex-col relative min-w-[500px]">

    <!-- Header section with logo and widgets -->
    <header class="flex w-full min-w-[500px] mt-10">
      <!-- Left side: Main navigation menu -->
      <div class="basis-1/3 ml-10 self-center">
        <MainMenu @change-component="changeComponent" />
      </div>

      <!-- Centered logo acting as a navigation button -->
      <div class="flex justify-center w-screen">
        <img src="./assets/logo.png" width="50" height="50" class=" hover:cursor-pointer"
          @click="changeComponent('mainFrame')" />
      </div>

      <!-- Right side: Inventory widget (possibly status or quick access) -->
      <div class="basis-1/3 mr-10 self-center">
        <InventoryWidget @change-component="changeComponent" :key="componentRefreshKey" />
      </div>
    </header>

    <!-- Main content area dynamically rendered based on currentComponent -->
    <main class="flex justify-center w-full min-h-screen">
      <div class="flex-1 flex justify-center w-full">
        <!-- Renders the current selected component -->
        <component :is="components[currentComponent]" @change-component="changeComponent" :key="componentRefreshKey"/>
      </div>
    </main>

    <!-- Connection modal for login or auth handling -->
    <ConnectionModal :isVisible="isLoginVisible" @save="update" />
  </div>
</template>

<script setup>
import { ref } from 'vue';

// Component imports
import ConnectionModal from './components/router/ConnectionModal.vue';
import MainMenu from './components/router/MainMenu.vue';
import AreaAdmin from './components/views/AreaAdmin.vue';
import CategoryAdmin from './components/views/CategoryAdmin.vue';
import InventoryHistory from './components/views/InventoryHistory.vue';
import InventoryProcess from './components/views/InventoryProcess.vue';
import inventoryValidation from './components/views/inventoryValidation.vue';
import InventoryWidget from './components/views/InventoryWidget.vue';
import ReferenceAdmin from './components/views/ReferenceAdmin.vue';
import StockForecast from './components/views/StockForecast.vue';
import WelcomePage from './components/views/WelcomePage.vue';

// Reactive state for controlling the login modal visibility
import { isLoginVisible } from './stores/authModalState.js';


// Handles component switching and refresh on mainFrame
const changeComponent = (component) => {
  if (component == 'mainFrame') {
    componentRefreshKey.value++;
  }
  currentComponent.value = component;
};

// Mapping of component names to their actual Vue components
const components = {
  mainFrame: WelcomePage,
  categoryAdmin: CategoryAdmin,
  areaAdmin: AreaAdmin,
  referenceAdmin: ReferenceAdmin,
  inventoryProcess: InventoryProcess,
  inventoryHistory: InventoryHistory,
  inventoryValidation: inventoryValidation,
  stockForecast : StockForecast
};


// Used to force reloading of components when key is updated
const componentRefreshKey = ref(0);

// Currently displayed component
const currentComponent = ref('mainFrame');

// Triggered when user logs in or reload is needed
const update = () => {
  componentRefreshKey.value++;
}


</script>

<style scoped>
/* Ensures full size and column layout for the whole app */
html,
body,
#app {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
</style>
