<template>
    <!-- Modal overlay: shown only when 'isVisible' is true -->
    <div class="modal-overlay">
        <!-- Modal content -->
        <div class="modal text-left">
            <!-- Modal header with title and close button -->
            <div class="modal-header text-center items-center">
                <h2> {{
                    getStockRegistrationForReferenceMessageInArea(stockRegistration.referenceName,
                        stockRegistration.storageAreaName)
                    }}</h2>
                <button @click="close" class="m-0" style="margin-top: 0px;">
                    <X />
                </button>
            </div>

            <!-- Modal body containing the editable form -->
            <div class="modal-body">
                <form @submit.prevent.stop="submitForm" class="space-y-6">

                    <div class="space-x-2">
                        <label for="quantity">Quantité</label>
                        <input v-model="quantity" id="quantity" type="number" class="style-modal" step="any" min="0"
                            max="999999999" ref="quantityInput" required />
                    </div>

                    <div class="space-x-2">
                        <label for="comment">Commentaire</label>
                        <input v-model="comment" id="comment" class="style-modal" />
                    </div>
                    <!-- Submit button -->
                    <div class="text-right">
                        <button type="submit" class="style-modal">{{ BUTTON_TEXT.SAVE }}</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<script setup>
// Import needed modules and components
import { X } from 'lucide-vue-next';
import { onMounted, ref } from 'vue';
import { BUTTON_TEXT, EMIT_TYPE } from '@/utils/constants/generalText';
import { getStockRegistrationForReferenceMessageInArea } from '@/utils/constants/inventoryProcessText';


// Props passed to the component: modal visibility and item to edit
const props = defineProps({
    stockRegistration: {
        type: Object,
        required: true
    }
});

const quantity = ref(props.stockRegistration.quantity);
const comment = ref(props.stockRegistration.comment);

const quantityInput = ref(null);

onMounted(()=>{
    quantityInput.value?.focus();
})

// Emit events: to close the modal and save changes
const emit = defineEmits([EMIT_TYPE.CLOSE, EMIT_TYPE.SAVE]);

// Close modal
const close = () => {
    quantity.value = "";
    comment.value = "";
    emit(EMIT_TYPE.CLOSE);
};

// Submit form and emit the updated object
const submitForm = async () => {
    const registration = { ...props.stockRegistration };
    registration.quantity = quantity.value;
    registration.comment = comment.value;
    emit(EMIT_TYPE.SAVE, registration);
    quantity.value = "";
    comment.value = "";
};
</script>