<template>
    <!-- Main container for inventory validation -->
    <div class="mt-8 mx-16 w-full">
        <!-- Inventory title showing the month -->
        <div class="flex flex-inline">
            <h2 class="my-4 font-bold grow">
                {{ TITLE_TEXT.INVENTORY_VALIDATION + getMonthName(new Date(inventory.startDateInventory)) }}
            </h2>

            <!-- Button to generate report for the specific inventory -->
            <div class="m-2">
                <button type="button" @click="handleGenerateReport"
                    class="p-2 bg-blue-500 text-white text-xs rounded shadow-md w-auto hover:bg-blue-700">
                    {{ BUTTON_TEXT.GENERATE_REPORT }}
                </button>
            </div>
        </div>
        <!-- Loading indicator -->
        <div v-if="isLoading" class="text-gray-500 italic">{{ OTHER_TEXT.LOADING_TEXT }}
            <Loader />
        </div>

        <!-- Message when no data is available -->
        <div v-else-if="areasWithReferences.length === 0" class="text-red-500">{{ OTHER_TEXT.EMPTY_DATA }}</div>

        <!-- Loop through each area -->
        <div v-else v-for="area in areasWithReferences" :key="area.name"
            class="mb-2 w-full border-4 p-2 pl-4 cursor-pointer bg-gray-100 transition">

            <!-- Area header with toggle functionality -->
            <div class="flex p-1 hover:bg-gray-200" @click="toggleArea(area.name)">
                <h3 class="font-bold text-lg grow">
                    {{ area.name }}
                </h3>
                <!-- Validity indicator -->
                <Check v-if="area.isValid" class="text-green-500" />
                <X v-else class="text-red-500" />
            </div>

            <!-- List of references for the area -->
            <div v-if="area?.isOpen">
                <ul class="mx-2 transition-all duration-300 ease-in-out">
                    <li v-if="area?.references?.some(ref => ref.numberOfRegistration === 0)" class="border-4 m-1">
                        <div>
                            <span>Pour toutes les références de {{ area?.name }} : </span>
                            <TooltipButton label=" Ajouter stock précédent"
                                tooltip="Ajoute, s'il existe, le dernier stock connu à chaque référence sans enregistrement"
                                @click="updateLasRegistrationForAllRef(area?.name)" />
                        </div>
                    </li>
                    <li v-for="ref in area?.references" :key="ref.referenceName + ref.referenceId" class="border-4 m-1">
                        <ReferenceItemValidation :reference="ref" :areaName="area.name"
                            v-on:[EMIT_TYPE.CHANGE]="loadData" />
                    </li>
                </ul>
            </div>
        </div>
    </div>
    <ConfirmInventoryClosureModal v-if="showConfirmModal" @confirm="confirmClosure" @close="showConfirmModal = false" />
</template>


<script setup>
// Import required components and icons
import { Loader, Check, X } from 'lucide-vue-next';
import { ref, onMounted } from 'vue';
import { getRegistrationValidated, isInventoryInProgress, stopInventory } from '@/controller/inventoryController';
import { updateLasRegistrationForAllRefInArea } from '@/controller/registrationController';
import { BUTTON_TEXT, COMPONENTS, EMIT_TYPE, OTHER_TEXT, TITLE_TEXT } from '@/utils/constants/generalText';
import { MESSAGE_HANDLER_TYPE } from '@/utils/constants/messageHandlerTypeText';
import { getMonthName } from '@/utils/dateUtils';
import { downloadReport } from '@/utils/downloadReport';
import { messageHandler } from '../../utils/messageManager/messageHandler';
import ReferenceItemValidation from '../mainProcessComponents/ReferenceItemValidation.vue';
import TooltipButton from '../mainProcessComponents/utils/TooltipButton.vue';
import ConfirmInventoryClosureModal from '../modal/ConfirmInventoryClosureModal.vue';

// Define reactive state
const inventory = ref({});
const areasWithReferences = ref([]);
const isLoading = ref(true);

const showConfirmModal = ref(false);
const emit = defineEmits([EMIT_TYPE.CHANGE_COMPONENT]);

// Fetch and process inventory data when the component is mounted
const loadData = async () => {
    try {
        // Retrieve inventory currently in progress
        inventory.value = (await isInventoryInProgress()).data;

        // Retrieve registration data grouped by area and reference
        const response = await getRegistrationValidated(inventory.value.idInventory);
        const rawData = response.data;

        const structuredAreas = [];

        // Loop through each area to structure its references
        for (const [areaName, references] of Object.entries(rawData)) {
            const groupedRefs = mapReferences(references);
            let area = areasWithReferences.value.find(a => a.name === areaName);
            area = {
                name: areaName,
                isValid: isAreaValid(groupedRefs),
                isOpen: area?.isOpen ?? false,
                references: groupedRefs
            };

            structuredAreas.push(area);
        }

        // Replace the original areas data with the structured one
        areasWithReferences.value = structuredAreas;
    } catch (err) {
        // Handle errors during fetch
        messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR);
    } finally {
        // Disable loading state
        isLoading.value = false;
    }
}

function mapReferences(references) {
    return references.map(ref => ({
        ...ref,
        numberOfRegistration: ref.registrations.length,
    }));
}

onMounted(loadData);

// Check if all references in an area are valid
function isAreaValid(references) {
    return references.every(ref => ref.isValid);
}

// Toggle the display of references within an area
const toggleArea = (areaName) => {
    areasWithReferences.value.forEach(area => {
        area.isOpen = (area.name === areaName) ? !area.isOpen : false;
    });
};

async function generateReport(idInventory) {
    try {
        await downloadReport(idInventory);
        await stopInventory();
        emit(EMIT_TYPE.CHANGE_COMPONENT, COMPONENTS.MAIN_FRAME)
    } catch (err) {
        messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR);
    }
}

async function updateLasRegistrationForAllRef(area) {
    try{
       const response = await updateLasRegistrationForAllRefInArea(area);
       messageHandler(response.message, MESSAGE_HANDLER_TYPE.SUCCESS);
       loadData();
    }catch (err){
        messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR);
    }
}
 
const handleGenerateReport = () => {
    const hasErrors = !areasWithReferences.value.every(area => area.isValid);
    if (hasErrors) {
        showConfirmModal.value = true;
    } else {
        generateReport(inventory.value.idInventory);
    }
};

const confirmClosure = () => {
    showConfirmModal.value = false;
    generateReport(inventory.value.idInventory);
};

</script>
