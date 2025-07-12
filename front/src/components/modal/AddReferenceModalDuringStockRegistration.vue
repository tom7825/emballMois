<template>
    <!-- Modal overlay: shown only when 'isVisible' is true -->
    <div class="modal-overlay">
        <!-- Modal content -->
        <div v-if="allReferences.length && storageAreaName" class="modal text-left">
            <!-- Modal header with title and close button -->
            <div class="modal-header text-center items-center">
                <h2> {{ TITLE_TEXT.ADD_REFERENCE_IN_AREA + storageAreaName }}</h2>
                <button @click="close" aria-label="Close the modal" class="m-0" style="margin-top: 0px;">
                    <X />
                </button>
            </div>

            <!-- Modal body containing the editable form -->
            <div class="modal-body">
                <form @submit.prevent="addReferenceInArea" class="space-y-6 flex flex-col items-center">
                    <div class="">
                        <DropDownList :options="allReferences" :placeholder=DROP_DOWN_LIST_PLACEHOLDER.REFERENCE
                            selectClass="px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500 shadow-sm"
                            v-model="localSelectedReference" class="w-auto" ref="allReferencesDrop" required />
                    </div>
                    <div v-if="localSelectedReference == INVENTORY_MESSAGES.NEW_REFERENCE">
                        <span>{{ OTHER_TEXT.NAME_OF_REFERENCE }} </span>
                        <input type="text" v-model="referenceName"
                            class="px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500 shadow-sm" />
                    </div>
                    <!-- Submit button -->
                    <div class="text-right">
                        <button type="submit" class="style-modal">{{ BUTTON_TEXT.SAVE }}</button>
                    </div>
                </form>
            </div>
        </div>
        <div v-else class="p-4 text-red-500">
            Erreur pendant le chargement des données
        </div>
    </div>
</template>

<script setup>
// Import needed modules and components
import { X } from 'lucide-vue-next';
import { onMounted, ref, watch } from 'vue';
import { BUTTON_TEXT, DROP_DOWN_LIST_PLACEHOLDER, EMIT_TYPE, OTHER_TEXT, TITLE_TEXT } from '@/utils/constants/generalText';
import { getReferenceAddDuringInventoryProcessMessage, getRemindUpdateReferenceMessage, INVENTORY_MESSAGES } from '@/utils/constants/inventoryProcessText';
import { MESSAGE_HANDLER_TYPE } from '@/utils/constants/messageHandlerTypeText';
import { createMinimalReference, loadReferencesButAlreadyExistsInThisAreaAndMap, updateReference } from '@/utils/useReferences';
import { messageHandler } from '../../utils/messageManager/messageHandler';
import DropDownList from '../administrationComponents/genericComponents/DropDownList.vue';

// Props passed to the component: modal visibility and item to edit
const props = defineProps({
    storageAreaName: {
        type: String,
        required: true
    }
});

// Reactive variables for the references list, selected reference, and reference name
const allReferences = ref([]);
const localSelectedReference = ref('');
const referenceName = ref('');

const allReferencesDrop = ref(null);

// When the component is mounted, load the data
onMounted(() => {
    loadData();
    allReferencesDrop.value?.focus();
});

// Emit events for close and save actions
const emit = defineEmits([EMIT_TYPE.CLOSE, EMIT_TYPE.SAVE]);

// Watch for changes in storageAreaName and reload data when it changes
watch(() => props.storageAreaName, (newVal) => {
    if (newVal) {
        loadData();
    }
});

// Function to load references for the current storage area
async function loadData() {
    try {
        // Load references and add a "new reference" option at the beginning
        allReferences.value = await loadReferencesButAlreadyExistsInThisAreaAndMap(props.storageAreaName)
        allReferences.value.unshift({ name: INVENTORY_MESSAGES.NEW_REFERENCE })
    } catch (err) {
        // Handle any error during data loading
        messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR);
    }
}

// Close the modal and reset the selected reference and name
const close = () => {
    localSelectedReference.value = '';
    referenceName.value = '';
    emit(EMIT_TYPE.CLOSE);
};

// Add a reference to the storage area
const addReferenceInArea = async () => {
    // Validate the storage area before proceeding
    if (!isValidStorageArea()) return;

    // Check if a new reference is selected
    const isNewReference = isNewReferenceSelected();

    if (isNewReference) {
        // Handle the creation of a new reference
        await handleNewReferenceCreation();
    } else {
        // Handle the update of an existing reference
        await handleExistingReferenceUpdate();
    }
};

// Check if the storage area is valid (non-empty)
const isValidStorageArea = () => {
    if (!props.storageAreaName) {
        // Show an error message if the storage area is invalid
        messageHandler("Zone de stockage invalide", MESSAGE_HANDLER_TYPE.ERROR);
        return false;
    }
    return true;
};

// Check if the selected reference is a new one
const isNewReferenceSelected = () => localSelectedReference.value === INVENTORY_MESSAGES.NEW_REFERENCE;

// Handle the creation of a new reference
const handleNewReferenceCreation = async () => {
    if (!referenceName.value.trim()) {
        // Show an error if no reference name is provided
        return messageHandler(INVENTORY_MESSAGES.REFERENCE_NAME_MISSING, MESSAGE_HANDLER_TYPE.ERROR);
    }

    // Create an empty reference object with the provided name
    const newReference = getEmptyReference();

    try {
        // Attempt to create the new reference
        await createMinimalReference(newReference);
        // Display success and reminder messages
        handleSuccessMessages(newReference.referenceName, true);
        // Emit the save event
        emit(EMIT_TYPE.SAVE);
    } catch (err) {
        // Handle any errors during the reference creation
        handleError(err);
    }
};

// Handle updating an existing reference
const handleExistingReferenceUpdate = async () => {
    // Find the reference to add to the storage area
    const referenceToAdd = findReferenceByName(localSelectedReference.value);
    if (!referenceToAdd) {
        // Show an error if the reference cannot be found
        return messageHandler("Références invalide", MESSAGE_HANDLER_TYPE.ERROR);
    }

    // Add the storage area to the reference
    addAreaToReference(referenceToAdd, props.storageAreaName);

    try {
        // Attempt to update the reference
        await updateReference(referenceToAdd);
        // Display success messages
        handleSuccessMessages(referenceToAdd.referenceName, false);
        // Emit the save event
        emit(EMIT_TYPE.SAVE);
    } catch (err) {
        // Handle any errors during the reference update
        handleError(err);
    }
};

// Find a reference by its name from the allReferences list
const findReferenceByName = (name) => {
    return allReferences.value.find(ref => ref.referenceName === name);
};

// Add the given storage area to the reference's list of areas
const addAreaToReference = (reference, areaName) => {
    if (!reference.areasName) reference.areasName = [];
    reference.areasName.push(areaName);
};

// Create an empty reference object with default values
const getEmptyReference = () => ({
    referenceName: referenceName.value,
    unitCount: "U",
    supplierName: "",
    areasName: [props.storageAreaName],
    archivedPackaging: false,
    categoryName: "",
    unitPrice: 0
});

// Display success messages when the reference is successfully added
const handleSuccessMessages = (referenceName, isNew) => {
    messageHandler(
        getReferenceAddDuringInventoryProcessMessage(referenceName, props.storageAreaName),
        MESSAGE_HANDLER_TYPE.SUCCESS
    );
    // If it's a new reference, display a reminder to update the reference
    if (isNew) {
        messageHandler(getRemindUpdateReferenceMessage(referenceName), MESSAGE_HANDLER_TYPE.WARNING);
    }
};

// Handle errors by displaying an error message
const handleError = (err) => {
    messageHandler(err.message || "Une erreur est survenue", MESSAGE_HANDLER_TYPE.ERROR);
};
</script>

<style scoped></style>
