<template>
  <div class="mt-8 mx-10 w-full">
    <h2 class="my-4 font-bold text-xl">{{ TITLE_TEXT.INVENTORY_HISTORY }}</h2>

    <!-- Grille responsive de tuiles -->
    <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6" v-if="!loading">
      <div
        v-for="(inventory, index) in inventories"
        :key="inventory.idInventory + index"
        class="transition-all duration-300 cursor-pointer"
        :class="openStates[inventory.idInventory] ? 'col-span-full' : ''"
        @click="toggleInventory(inventory.idInventory)"
      >
        <div
          class="p-4 border rounded-lg shadow-sm bg-gray-100 hover:bg-gray-200"
        >
          <div class="flex justify-between items-center mb-2">
            <div>
              <h3 class="text-lg font-semibold">
                {{ getMonthName(new Date(inventory.endDateInventory)) }}
              </h3>
              <p class="text-sm text-gray-600">
                {{ formatDate(inventory.endDateInventory) }}
              </p>
            </div>

            <button
              type="button"
              @click.stop="() => downloadReport(inventory.idInventory)"
              class="p-2 bg-blue-500 text-white text-xs rounded shadow-md hover:bg-blue-700"
            >
              {{ BUTTON_TEXT.GENERATE_REPORT }}
            </button>
          </div>

          <transition name="expand">
            <div
              v-if="openStates[inventory.idInventory]"
              class="mt-4 space-y-2 overflow-hidden"
            >
              <StockRegistrationItem
                v-for="reg in stockRegistrations"
                :key="reg.id"
                :data="reg"
                :permitted-modification="false"
              />
            </div>
          </transition>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { loadInventories } from '@/controller/inventoryController';
import { loadRegistrations } from '@/controller/registrationController';
import { BUTTON_TEXT, TITLE_TEXT } from '@/utils/constants/generalText';
import { MESSAGE_HANDLER_TYPE } from '@/utils/constants/messageHandlerTypeText';
import { getMonthName, formatDate } from '@/utils/dateUtils';
import { downloadReport } from '@/utils/downloadReport';
import { messageHandler } from '../../utils/messageManager/messageHandler';
import StockRegistrationItem from '../mainProcessComponents/StockRegistrationItem.vue';

const inventories = ref([]);
const stockRegistrations = ref([]);
const openStates = ref({});
const loading = ref(true);

onMounted(async () => {
  try {
    const response = await loadInventories();
    inventories.value = response.data;
    loading.value = false;
    messageHandler(response.message, MESSAGE_HANDLER_TYPE.SUCCESS);
  } catch (err) {
    messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR);
    loading.value = false;
  }
});

const toggleInventory = async (inventoryId) => {
  const isOpen = openStates.value[inventoryId];

  if (isOpen) {
    openStates.value[inventoryId] = false;
    return;
  }

  // Ferme toutes les autres tuiles
  Object.keys(openStates.value).forEach((key) => {
    openStates.value[key] = false;
  });

  try {
    const response = await loadRegistrations(inventoryId);
    stockRegistrations.value = response.data;
    openStates.value[inventoryId] = true;
  } catch (err) {
    messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR);
  }
};
</script>

<style scoped>
.expand-enter-active,
.expand-leave-active {
  transition: max-height 0.4s ease, opacity 0.4s ease;
  overflow: hidden;
}
.expand-enter-from,
.expand-leave-to {
  max-height: 0;
  opacity: 0;
}
.expand-enter-to,
.expand-leave-from {
  max-height: 1000px;
  opacity: 1;
}
</style>
