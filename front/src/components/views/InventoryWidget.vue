<template>
    <!-- Wrapper div with vertical layout -->
    <div class="flex flex-col">
        <!-- Button that triggers inventoryAction when clicked -->
        <!-- Styling includes padding, blue background, small text, rounded corners, hover effect -->
        <button @click="inventoryAction" type="button"
            class="p-2 bg-blue-500 text-white text-xs rounded shadow-md w-auto hover:bg-blue-700">
            <!-- Dynamic button text, depends on whether inventory is in progress -->
            {{ buttonText }}
        </button>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { isInventoryInProgress, startInventory } from '@/controller/inventoryController';
import { APP_MESSAGES, COMPONENTS, EMIT_TYPE } from '@/utils/constants/generalText';
import { MESSAGE_HANDLER_TYPE } from '@/utils/constants/messageHandlerTypeText';
import { messageHandler } from '../../utils/messageManager/messageHandler';

// Local non-reactive variable to store inventory status
let inventoryStatus;

// Reactive state for button label and error message
const buttonText = ref("");

// Declares emitted event
const emit = defineEmits([EMIT_TYPE.CHANGE_COMPONENT]);

// Runs once when the component is mounted
onMounted(async () => {
    try {
        // Checks if an inventory is already in progress
        inventoryStatus = (await isInventoryInProgress()).data;
        buttonText.value = APP_MESSAGES.PROCESS_INVENTORY; // Show continue option
    } catch (err) {
        // If check fails (or no inventory), fallback to "start"
        err.message = "";
        buttonText.value = APP_MESSAGES.START_INVENTORY;
    }
});

// Triggered when user clicks the button
async function inventoryAction() {
    if (inventoryStatus) {
        // If inventory is already in progress, switch to inventory process view
        emit(EMIT_TYPE.CHANGE_COMPONENT, COMPONENTS.INVENTORY_PROCESS);
    } else {
        // Otherwise, try to start a new inventory
        try {
            await startInventory();
            inventoryStatus = true; // Update status to reflect inventory has started
            emit(EMIT_TYPE.CHANGE_COMPONENT, COMPONENTS.INVENTORY_PROCESS); // Navigate to inventory process
            buttonText.value = APP_MESSAGES.PROCESS_INVENTORY; // Update button text accordingly
        } catch (err) {
            // If starting inventory fails, display error message
            messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR);
            //errMessage.value = err.message;
        }
    }
}
</script>
