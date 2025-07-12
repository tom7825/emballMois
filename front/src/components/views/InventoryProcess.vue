<template>
    <!-- Main container with margins -->
    <div class="w-full mx-10 my-8">
        <!-- Grid layout for top section with dropdown and buttons -->
        <div class="grid grid-cols-12 w-full">
            <!-- Dropdown + Add/Remove buttons section -->
            <div class="col-span-7 flex gap-4">
                <!-- Dropdown to select a storage area -->
                <DropDownList :options="activeAreasList" :placeholder=DROP_DOWN_LIST_PLACEHOLDER.STORAGE_AREA_LIST
                    selectClass="px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500 shadow-sm w-50"
                    v-model="selectedArea" :reset="resetDropdown" required />

                <!-- Add line button: visible only if selectedArea is chosen and less than 5 registration lines -->
                <TooltipButton :class="{ 'invisible': stockRegistrations.length > 4 || !selectedArea }"
                    @click="addStockRegistrationLine" label="+" tooltip="Ajouter une ligne d'enregistrement de stock"
                    className="mt-0 px-4 py-2 rounded-md bg-teal-500 text-white hover:bg-teal-700" />


                <!-- Remove line button: visible if more than one registration line -->
                <TooltipButton v-if="stockRegistrations.length > 1" @click="removeStockRegistrationLine" label="-"
                    tooltip="Retirer la dernière ligne d'enregistrement de stock"
                    className="mt-0 px-4 py-2 rounded-md bg-teal-500 text-white hover:bg-teal-700" />
            </div>

            <!-- Close inventory button, aligned to the right -->
            <div class="col-span-5 flex justify-end">
                <button @click="emit(EMIT_TYPE.CHANGE_COMPONENT, COMPONENTS.INVENTORY_VALIDATION);"
                    class="px-4 py-2 rounded-md bg-blue-500 text-white hover:bg-blue-700 text-xs">
                    {{ BUTTON_TEXT.VALIDATE_INVENTORY }}
                </button>
            </div>
        </div>

        <!-- Form appears only if a storage area is selected -->
        <div v-if="selectedArea" class="mt-4 w-full">
            <!-- Stock registration form -->
            <form @submit.prevent="saveRegistrations" class="grid grid-cols-12 gap-4">
                <!-- Up to 5 registration lines -->
                <div v-for="(registration, index) in stockRegistrations" :key="index"
                    class="col-span-12 grid grid-cols-12 gap-2">
                    <StockRegistration :id="index" :selectArea="selectedArea"
                        v-model:referenceName="registration.referenceName"
                        v-model:packagingCount="registration.packagingCount" v-model:comment="registration.comment"
                        v-model:quantity="registration.quantity" />
                </div>

                <!-- Submit button -->
                <button type="submit" class="py-2 rounded-md bg-teal-500 text-white hover:bg-teal-700 col-span-2">
                    {{ BUTTON_TEXT.VALIDATE }}
                </button>
            </form>
        </div>

        <!-- Section to show previously registered stock entries -->
        <div class="mt-8">
            <h3>{{ TITLE_TEXT.STOCK_REGISTRATION_IN_PROGRESS_INVENTORY }}</h3>
            <StockRegistrationList :key="stockRegistrationListReloadKey" />
        </div>
    </div>

    <!-- Modal modify stock registration before saving-->
    <div>
        <ReferenceModificationBeforeStockRegistrationModal v-if="isModifyReferenceModalVisible"
            :isVisible="isModifyReferenceModalVisible" @save="referenceUpdated"
            @close="isModifyReferenceModalVisible = false" :missingFields="missingFields" :reference="dataToUpdate" />
    </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import { getAreasWithActiveReferences } from '@/controller/areaController';
import { BUTTON_TEXT, COMPONENTS, DROP_DOWN_LIST_PLACEHOLDER, EMIT_TYPE, TITLE_TEXT } from '@/utils/constants/generalText';
import { getInvalidQuantityMessage, INVENTORY_MESSAGES } from '@/utils/constants/inventoryProcessText';
import { MESSAGE_HANDLER_TYPE } from '@/utils/constants/messageHandlerTypeText';
import { actionSaveRegistration } from '@/utils/registrationAction';
import { messageHandler } from '../../utils/messageManager/messageHandler';
import DropDownList from '../administrationComponents/genericComponents/DropDownList.vue';
import StockRegistration from '../mainProcessComponents/StockRegistration.vue';
import StockRegistrationList from '../mainProcessComponents/StockRegistrationList.vue';
import TooltipButton from '../mainProcessComponents/utils/TooltipButton.vue';
import ReferenceModificationBeforeStockRegistrationModal from '../modal/ReferenceModificationBeforeStockRegistrationModal.vue';


// Lifecycle: fetch areas on mount
onMounted(loadData);

// Reactive state variables
const activeAreasList = ref([]);
const selectedArea = ref("");
const resetDropdown = ref(false);
const stockRegistrationListReloadKey = ref(0);
const emit = defineEmits([EMIT_TYPE.CHANGE_COMPONENT]);
const stockRegistrations = ref([]); // Data for stock input rows
const isModifyReferenceModalVisible = ref(false);
const dataToUpdate = ref(null);
const missingFields = ref({ message: "", error: {} });


// Watch selectedArea to auto-add one registration line when changed
watch(selectedArea, async () => {
    if (stockRegistrations.value.length == 0) {
        addStockRegistrationLine();
    } else {
        stockRegistrations.value.length = 0;
        addStockRegistrationLine();
    }
});

// Fetch storage areas with active references
async function loadData() {
    try {
        activeAreasList.value = await getAreasWithActiveReferences();
    } catch (err) {
        messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR)
    }
}

const emptyStockRegistration = {
    quantity: null,
    comment: '',
    referenceName: '',
    packagingCount: false
}

// Adds a new registration line (max 5 allowed)
function addStockRegistrationLine() {
    if (stockRegistrations.value.length < 5) {
        stockRegistrations.value.push({ ...emptyStockRegistration });
    }
}

// Removes the last registration line
function removeStockRegistrationLine() {
    if (stockRegistrations.value.length >= 1) {
        stockRegistrations.value.pop();
    }
}

// Handles form submission
async function saveRegistrations() {
    // Transform the form data into a consistent payload
    const registrations = createRegistrationsToAddArray();
    for (const registration of registrations) {
        if (!isValidRegistration(registration)) {
            return;
        }
        const errorDuringSavingRegistration = await actionSaveRegistration(registration);
        if (errorDuringSavingRegistration) {
            missingFields.value = errorDuringSavingRegistration.errors;
            dataToUpdate.value = errorDuringSavingRegistration.reference;
            isModifyReferenceModalVisible.value = true;
            return;
        } else {
            stockRegistrations.value.splice(registration, 1);
        }
    }
    stockRegistrations.value = [];
    addStockRegistrationLine();
    stockRegistrationListReloadKey.value++; // Triggers reload of StockRegistrationList

}

function isValidRegistration(registration) {
    if (!registration.referenceName) {
        messageHandler(INVENTORY_MESSAGES.REFERENCE_NAME_MISSING, MESSAGE_HANDLER_TYPE.WARNING);
        return false;
    }
    if (registration.quantity < 0) {
        messageHandler(getInvalidQuantityMessage(registration.referenceName), MESSAGE_HANDLER_TYPE.WARNING);
        return false;
    }
    return true;
}

function createRegistrationsToAddArray() {
    return stockRegistrations.value.map(registration => ({
        quantity: registration.quantity,
        comment: registration.comment,
        referenceName: registration.referenceName,
        storageAreaName: selectedArea.value,
        packagingCount: registration.packagingCount
    }));
}

async function referenceUpdated() {
    isModifyReferenceModalVisible.value = false;
    saveRegistrations();
}
</script>
