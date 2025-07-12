<template>
    <!-- This div is used to ensure Tailwind includes these dynamic color utility classes.
         Tailwind purges unused classes at build time, so this forces them to be included. -->
    <div class="bg-pink-500 focus:ring-pink-500 text-pink-500 hover:bg-pink-700"></div>

    <!-- Main container with vertical spacing, horizontal margins, and full width -->
    <div class="space-y-6 mt-4 mx-32 w-full">
        <!-- 12-column grid layout for organizing components -->
        <div class="grid grid-cols-12 w-full">
            <!-- Full-width column for the AddReference component -->
            <div class="col-span-12 w-full">
                <!-- Component to add a new reference; triggers loadData on submission -->
                <AddReference :objectType="ADMIN_VIEW_TEXT.REFERENCE_TYPE" :color="COLOR_VIEW.REFERENCE"
                    @save="loadData" />
            </div>

            <!-- Full-width column for the SearchForm component -->
            <div class="col-span-12">
                <!-- Search form with filters for active and archived references -->
                <!-- Uses ref for programmatic access, emits 'search' event with filtered results -->
                <SearchForm :objectType=ADMIN_VIEW_TEXT.REFERENCE_TYPE :color="COLOR_VIEW.REFERENCE"
                    :activeItems="activeReference" :archivedItems="archivedReference" ref="searchFormRef"
                    @search="updateFilteredReference" />
            </div>
        </div>

        <!-- Grid for displaying active and archived reference lists side by side -->
        <div class="grid grid-cols-12 gap-8 p-2 w-full min-h-calc">
            <!-- Active references list, half-width on medium+ screens -->
            <div class="col-span-12 md:col-span-6 w-full">
                <!-- Displays the filtered list of active references -->
                <!-- Handles display errors and modification events -->
                <ReferenceList :title="ADMIN_VIEW_TEXT.ACTIVE_REFERENCE_LIST" :references="filteredActiveReference"
                    :errorMessage="errorListActive" :color="COLOR_VIEW.REFERENCE" @modify="modify" />
            </div>

            <!-- Archived references list, same structure as active -->
            <div class="col-span-12 md:col-span-6 w-full">
                <ReferenceList :title="ADMIN_VIEW_TEXT.ARCHIVE_REFERENCE_LIST" :references="filteredArchivedReference"
                    :color="COLOR_VIEW.REFERENCE" :errorMessage="errorListArchived" @modify="modify" />
            </div>
        </div>
    </div>
</template>

<script setup>
// Vue Composition API imports
import { ref, onMounted, nextTick } from 'vue';
import {
    getActiveReferences,
    getArchivedReferences,
} from '@/controller/referenceController';
import { ADMIN_VIEW_TEXT } from '@/utils/constants/administrationText';
import { COLOR_VIEW, EMIT_TYPE } from '@/utils/constants/generalText';
import SearchForm from '../administrationComponents/genericComponents/SearchForm.vue';
import AddReference from '../administrationComponents/referenceComponents/AddReference.vue';
import ReferenceList from '../administrationComponents/referenceComponents/ReferenceList.vue';


// Reactive state for error messages
const errorListActive = ref("");
const errorListArchived = ref("");

// Raw data lists of active and archived references
const activeReference = ref([]);
const archivedReference = ref([]);

// Filtered data lists used for display after search/filtering
const filteredActiveReference = ref([]);
const filteredArchivedReference = ref([]);

defineEmits([EMIT_TYPE.CHANGE_COMPONENT]);

// Reference to the SearchForm component to trigger filtering methods
const searchFormRef = ref(null);

// Automatically load data when component is mounted
onMounted(loadData);

// Called when the SearchForm emits filtered results
const updateFilteredReference = (filteredActive, filteredArchived) => {
    filteredActiveReference.value = filteredActive;
    filteredArchivedReference.value = filteredArchived;
};

// Handles modification of a reference, then reloads data
const modify = async () => {
    await loadData();
};

// Function to load or reload all data
async function loadData() {
    // Clear any previous error messages
    errorListActive.value = "";
    errorListArchived.value = "";
    // Fetch active references
    try {
        activeReference.value = await getActiveReferences();
        activeReference.value = activeReference.value.data.map(data => ({
            ...data,
            name: data.referenceName
        }))
        filteredActiveReference.value = [...activeReference.value];
    } catch (err) {
        activeReference.value = [];
        filteredActiveReference.value = [];
        errorListActive.value = err.message;
    }

    // Fetch archived references
    try {
        archivedReference.value = await getArchivedReferences();
        archivedReference.value = archivedReference.value.data.map(data => ({
            ...data,
            name: data.referenceName
        }))
        filteredArchivedReference.value = [...archivedReference.value];
    } catch (err) {
        archivedReference.value = [];
        filteredArchivedReference.value = [];
        errorListArchived.value = err.message;
    }

    // Wait for DOM updates before triggering any UI-dependent logic
    await nextTick();

    // Reapply filters if the search form has a method for it
    if (searchFormRef.value?.applyFilters) {
        searchFormRef.value.applyFilters();
    }
}
</script>
