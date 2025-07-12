<template>
  <!-- Decorative div with Tailwind color classes -->
  <div class="bg-blue-500 focus:ring-blue-500 text-blue-500 hover:bg-blue-700"></div>

  <!-- Main container with vertical spacing, margin, and full width -->
  <div class="space-y-6 mx-32 mt-10 w-full">

    <!-- Grid to divide the layout into 12 columns, the next two sections are part of this grid -->
    <div class="grid grid-cols-12 w-full">

      <!-- Section for adding a new category (spanning 6 columns on medium and larger screens) -->
      <div class="col-span-12 md:col-span-6">
        <!-- AddForm component for creating a new category -->
        <AddForm :objectType="ADMIN_VIEW_TEXT.CATEGORY_TYPE" :color="COLOR_VIEW.CATEGORY" @submit="submitNewCategory" />
      </div>

      <!-- Section for searching a category (spanning 6 columns on medium and larger screens) -->
      <div class="col-span-12 md:col-span-6">
        <!-- SearchForm component to search categories -->
        <SearchForm :objectType="ADMIN_VIEW_TEXT.CATEGORY_TYPE" :color="COLOR_VIEW.CATEGORY"
          :activeItems="activeCategories" :archivedItems="archivedCategories" @search="updateFilteredCategories"
          ref="searchFormRef" />
      </div>
    </div>

    <!-- Container for active and archived categories lists -->
    <div class="grid grid-cols-12 gap-8 p-2 w-full min-h-calc">

      <!-- Active categories list (spanning 6 columns on medium and larger screens) -->
      <div class="col-span-12 md:col-span-6 w-full">
        <!-- ItemList component displaying active categories -->
        <ItemList :title="ADMIN_VIEW_TEXT.ACTIVE_CATEGORY_LIST" :items="filteredActiveCategories"
          :color="COLOR_VIEW.CATEGORY" :errorMessage="errorListActive" @modify="modifyArchiveStatus" />
      </div>

      <!-- Archived categories list (spanning 6 columns on medium and larger screens) -->
      <div class="col-span-12 md:col-span-6 w-full">
        <!-- ItemList component displaying archived categories -->
        <ItemList :title="ADMIN_VIEW_TEXT.ARCHIVE_CATEGORY_LIST" :items="filteredArchivedCategories"
          :color="COLOR_VIEW.CATEGORY" :errorMessage="errorListArchived" @modify="modifyArchiveStatus" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { addCategory, getActiveCategories, modifyCategory, getArchivedCategories } from '@/controller/categoryController';
import { ADMIN_MESSAGES, ADMIN_VIEW_TEXT } from '@/utils/constants/administrationText';
import { COLOR_VIEW, EMIT_TYPE } from '@/utils/constants/generalText';
import { MESSAGE_HANDLER_TYPE } from '@/utils/constants/messageHandlerTypeText';
import { messageHandler } from '../../utils/messageManager/messageHandler';
import AddForm from '../administrationComponents/genericComponents/AddForm.vue';
import ItemList from '../administrationComponents/genericComponents/ItemList.vue';
import SearchForm from '../administrationComponents/genericComponents/SearchForm.vue';

// Error messages for different sections
const errorListActive = ref("");
const errorListArchived = ref("");

// Reactive variables for active and archived categories
const activeCategories = ref([]);
const archivedCategories = ref([]);

// Reactive filtered lists to show after search
const filteredActiveCategories = ref([]);
const filteredArchivedCategories = ref([]);

// Reference for the search form
const searchFormRef = ref(null);

// Emit event to parent component when necessary
defineEmits([EMIT_TYPE.CHANGE_COMPONENT]);

// Load data when the component is mounted
onMounted(loadData);

// Function to submit a new category
const submitNewCategory = async (categoryName) => {
  // Check if category name is empty and set an error message
  if (!categoryName || categoryName.length === 0) {
    messageHandler(ADMIN_MESSAGES.EMPTY_CATEGORY_NAME, MESSAGE_HANDLER_TYPE.WARNING);
    return;
  }

  try {
    // Attempt to add the new category and reload data
    const response = await addCategory(categoryName);
    messageHandler(response.message, MESSAGE_HANDLER_TYPE.SUCCESS);
    await loadData();
  } catch (err) {
    // Set error message if something goes wrong
    messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR);
  }
};

// Function to update the filtered categories based on search input
const updateFilteredCategories = (filteredActive, filteredArchived) => {
  filteredActiveCategories.value = filteredActive;
  filteredArchivedCategories.value = filteredArchived;
};

// Function to modify the archive status of a category
const modifyArchiveStatus = async (category) => {
  try {
    // Attempt to modify the category's archived status and reload data
    const response = await modifyCategory(category);
    messageHandler(response.message, MESSAGE_HANDLER_TYPE.SUCCESS);
    await loadData();

  } catch (err) {
    // Set error message if something goes wrong
    messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR)
  }
}

// Function to load the categories (active and archived) from the backend
async function loadData() {
  // Reset the error messages before loading new data
  errorListActive.value = "";
  errorListArchived.value = "";

  // Load active categories
  try {
    activeCategories.value = await getActiveCategories();
    filteredActiveCategories.value = [...activeCategories.value];
  } catch (err) {
    // Set error message if something goes wrong
    activeCategories.value = [];
    filteredActiveCategories.value = [];
    errorListActive.value = err.message;
  }

  // Load archived categories
  try {
    archivedCategories.value = await getArchivedCategories();
    filteredArchivedCategories.value = [...archivedCategories.value];
  } catch (err) {
    // Set error message if something goes wrong
    archivedCategories.value = [];
    filteredArchivedCategories.value = [];
    errorListArchived.value = err.message;
  }

  // Ensure any filtering applied by the search form is executed
  await nextTick();

  if (searchFormRef.value?.applyFilters && searchFormRef.value !== "") {
    searchFormRef.value.applyFilters();
  }
}
</script>
