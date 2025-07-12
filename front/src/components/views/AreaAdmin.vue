<template>
  <!-- Invisible green div used to generate styles for Tailwind -->
  <div class="bg-green-500 focus:ring-green-500 text-green-500 hover:bg-green-700"></div>

  <!-- Main container for the entire component layout -->
  <div class="space-y-6 m-10 mx-32 w-full">

    <!-- First row: AddForm and SearchForm side by side -->
    <div class="grid grid-cols-12 w-full">

      <!-- Column for adding a new storage zone -->
      <div class="col-span-12 md:col-span-6">
        <AddForm :objectType="ADMIN_VIEW_TEXT.ZONE_TYPE" :color="COLOR_VIEW.AREA" @submit="submitNewArea" />
      </div>

      <!-- Column for searching among existing storage zones -->
      <div class="col-span-12 md:col-span-6">
        <SearchForm :objectType="ADMIN_VIEW_TEXT.ZONE_TYPE" :color="COLOR_VIEW.AREA" :activeItems="activeAreas"
          :archivedItems="archivedAreas" ref="searchFormRef" @search="updateFilteredAreas" />
      </div>
    </div>

    <!-- Second row: Active and archived zone lists side by side -->
    <div class="grid grid-cols-12 gap-8 p-2 w-full min-h-calc">

      <!-- List of currently active storage zones -->
      <div class="col-span-12 md:col-span-6 w-full">
        <ItemList :title="ADMIN_VIEW_TEXT.ACTIVE_STORAGE_LIST" :color="COLOR_VIEW.AREA" :items="filteredActiveAreas"
          :errorMessage="errorListActive" @modify="modifyArchiveStatus" />
      </div>

      <!-- List of archived storage zones -->
      <div class="col-span-12 md:col-span-6 w-full">
        <ItemList :title="ADMIN_VIEW_TEXT.ARCHIVE_STORAGE_LIST" :color="COLOR_VIEW.AREA" :items="filteredArchivedAreas"
          :errorMessage="errorListArchived" @modify="modifyArchiveStatus" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { addNewArea, getArchivedAreas, getActiveAreas, modifyArea } from '@/controller/areaController';
import { ADMIN_MESSAGES, ADMIN_VIEW_TEXT } from '@/utils/constants/administrationText';
import { COLOR_VIEW, EMIT_TYPE } from '@/utils/constants/generalText';
import { MESSAGE_HANDLER_TYPE } from '@/utils/constants/messageHandlerTypeText';
import { messageHandler } from '../../utils/messageManager/messageHandler';
import AddForm from '../administrationComponents/genericComponents/AddForm.vue';
import ItemList from '../administrationComponents/genericComponents/ItemList.vue';
import SearchForm from '../administrationComponents/genericComponents/SearchForm.vue';

// Error messages for various parts of the interface
const errorListActive = ref("");
const errorListArchived = ref("");

// Raw data from the backend
const activeAreas = ref([]);
const archivedAreas = ref([]);

// Filtered data shown in the UI, based on search criteria
const filteredActiveAreas = ref([]);
const filteredArchivedAreas = ref([]);

// Reference to the SearchForm component (used to call applyFilters)
const searchFormRef = ref(null);

defineEmits([EMIT_TYPE.CHANGE_COMPONENT]);

// Runs when component is mounted - triggers data loading
onMounted(loadData);

// Handles the submission of a new zone from AddForm
const submitNewArea = async (newAreaName) => {
  if (!newAreaName || newAreaName.length === 0) {
    messageHandler(ADMIN_MESSAGES.EMPTY_AREA_NAME, MESSAGE_HANDLER_TYPE.WARNING);
    return;
  }

  try {
    const response = await addNewArea(newAreaName);
    messageHandler(response.message, MESSAGE_HANDLER_TYPE.SUCCESS);
    await loadData();
  } catch (err) {
    messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR);
  }
};

// Called when SearchForm emits a filtered result
const updateFilteredAreas = (filteredActive, filteredArchived) => {
  filteredActiveAreas.value = filteredActive;
  filteredArchivedAreas.value = filteredArchived;
};

// Triggered when the archive status of a zone is changed
const modifyArchiveStatus = async (area) => {
  try {
    const response = await modifyArea(area);
    messageHandler(response.message, MESSAGE_HANDLER_TYPE.SUCCESS);
    await loadData();
  } catch (err) {
    messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR)
  }
}

// Loads all areas (active + archived) and updates filtered lists
async function loadData() {
  // Reset all error messages
  errorListActive.value = "";
  errorListArchived.value = "";

  // Fetch active areas
  try {
    activeAreas.value = await getActiveAreas();
    filteredActiveAreas.value = [...activeAreas.value];
  } catch (err) {
    activeAreas.value = [];
    filteredActiveAreas.value = [];
    errorListActive.value = err.message;
  }

  // Fetch archived areas
  try {
    archivedAreas.value = await getArchivedAreas();
    filteredArchivedAreas.value = [...archivedAreas.value];
  } catch (err) {
    archivedAreas.value = [];
    filteredArchivedAreas.value = [];
    errorListArchived.value = err.message;
  }

  // Wait for DOM updates to complete
  await nextTick();

  // Apply search filters again after reloading data
  if (searchFormRef.value?.applyFilters) {
    searchFormRef.value.applyFilters();
  }
}
</script>
