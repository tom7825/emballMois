<template>
    <!-- Modal overlay: shown only when 'isVisible' is true -->
    <div class="modal-overlay">
        <!-- Modal content -->
        <div class="modal">
            <!-- Modal header with title and close button -->
            <div class="modal-header">
                <div class="text-left">
                    <h2> {{ TITLE_TEXT.UPDATE_STOCK_REGISTRATION }} </h2>
                    <h3 class="truncate max-w-full">Référence {{ data.referenceName }} </h3>
                    <h3>Zone : {{ data.storageAreaName }}</h3>
                </div>
                <button @click.stop="emit(EMIT_TYPE.CLOSE)" class="m-0">
                    <X />
                </button>
            </div>

            <!-- Modal body containing the editable form -->
            <div class="modal-body">
                <form @submit.prevent="updateStockRegistration" class="space-y-6">
                    <StockRegistration :disableReferenceSelection="true"
                        v-model:referenceName="localStockRegistration.referenceName"
                        v-model:packagingCount="localStockRegistration.packagingCount"
                        v-model:comment="localStockRegistration.comment"
                        v-model:quantity="localStockRegistration.quantity"
                        :id="localStockRegistration.stockRegistrationId" />
                    <!-- Submit button -->
                    <div class="text-right">
                        <button type="submit" class="style-modal">{{ BUTTON_TEXT.SAVE }}</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <ReferenceModificationBeforeStockRegistrationModal v-if="isModifyReferenceModalVisible" @save="referenceUpdated"
        @close="isModifyReferenceModalVisible = false" :missingFields="missingFields" :reference="referenceToUpdate" />
</template>

<script setup>
// Import needed modules and components
import { X } from 'lucide-vue-next';
import { ref, watch } from 'vue';
import { BUTTON_TEXT, EMIT_TYPE, TITLE_TEXT } from '@/utils/constants/generalText';
import { actionUpdateRegistration } from '@/utils/registrationAction';
import StockRegistration from '../mainProcessComponents/StockRegistration.vue';
import ReferenceModificationBeforeStockRegistrationModal from './ReferenceModificationBeforeStockRegistrationModal.vue';

// Props passed to the component: modal visibility and item to edit
const props = defineProps({
    data: Object,
});

const referenceToUpdate = ref(null);
const missingFields = ref({});
const isModifyReferenceModalVisible = ref(false);
const localStockRegistration = ref({ ...props.data });
watch(() => props.data, (newvalue) => localStockRegistration.value = { ...newvalue })


// Emit events: to close the modal and save changes
const emit = defineEmits([EMIT_TYPE.CLOSE, EMIT_TYPE.SAVE]);


// Submit form and emit the updated object
const updateStockRegistration = async () => {
    const errors = await actionUpdateRegistration(localStockRegistration.value);
    if (errors) {
        missingFields.value = errors.errors;
        referenceToUpdate.value = errors.reference;
        isModifyReferenceModalVisible.value = true;
    } else {
        emit(EMIT_TYPE.SAVE, localStockRegistration.value);
    }
};

async function referenceUpdated() {
    isModifyReferenceModalVisible.value = false;
    updateStockRegistration();
}
</script>