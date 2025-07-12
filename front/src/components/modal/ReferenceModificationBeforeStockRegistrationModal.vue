<template>
    <!-- Modal overlay: shown only when 'isVisible' is true -->
    <div class="modal-overlay">
        <!-- Modal content -->
        <div class="modal text-left">
            <!-- Modal header with title and close button -->
            <div class="modal-header text-center items-center">
                <h2> {{ TITLE_TEXT.UPDATE_REFERENCE + referenceToModify.referenceName }}</h2>
                <button @click="emit(EMIT_TYPE.CLOSE);" class="m-0" style="margin-top: 0px;">
                    <X />
                </button>
            </div>

            <!-- Modal body containing the editable form -->
            <div class="modal-body">
                <form @submit.prevent.stop="saveReferenceModification" class="space-y-6">
                    <div class="space-x-2 whitespace-pre-line max-w-full break-words  overflow-hidden  p-2"
                        v-if="messages">
                        <div class="text-red-500 break-words">{{ messages }} </div>
                    </div>
                    <!-- Unit price input -->
                    <div class="space-x-2" v-if="fields.includes('price')">
                        <label for="unitPrice">Prix unitaire :</label>
                        <input v-model="referenceToModify.unitPrice" id="unitPrice" type="number" class="style-modal"
                            step=".00001" required />
                    </div>

                    <!-- Units per packaging input -->
                    <div class="space-x-2" v-if="fields.includes('packaging')">
                        <label for="unitByPackaging">Unités par conditionnement :</label>
                        <input v-model="referenceToModify.unitByPackaging" id="unitByPackaging" class="style-modal"
                            type="number" min="1" max="999999" />
                    </div>
                    <div class="space-x-2 inline-block flex items-center" v-if="fields.includes('category')">
                        <label for="categoryChoose">Catégorie :</label>
                        <DropDownList :options="categories" :placeholder="DROP_DOWN_LIST_PLACEHOLDER.CATEGORY_LIST"
                            selectClass="style-modal" v-model="referenceToModify.categoryName" class="w-auto"
                            required />
                    </div>
                    <div class="space-x-2" v-if="fields.includes('formula')">
                        <label for="calculationRule">Formule de calcul de valeur de stock</label>
                        <input v-model="referenceToModify.calculationRule" id="calculationRule" class="style-modal"
                            type="text" />
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
import { ref, watch, onMounted, nextTick } from 'vue';
import { getActiveCategories } from '@/controller/categoryController';
import { modifyReference } from '@/controller/referenceController';
import { ADMIN_MESSAGES } from '@/utils/constants/administrationText';
import { BUTTON_TEXT, DROP_DOWN_LIST_PLACEHOLDER, EMIT_TYPE, TITLE_TEXT } from '@/utils/constants/generalText';
import { MISSING_FIELDS } from '@/utils/constants/inventoryProcessText';
import { MESSAGE_HANDLER_TYPE } from '@/utils/constants/messageHandlerTypeText';
import { messageHandler } from '../../utils/messageManager/messageHandler';
import DropDownList from '../administrationComponents/genericComponents/DropDownList.vue';

// Props passed to the component: modal visibility and item to edit
const props = defineProps({
    reference: Object,
    missingFields: {
        type: Object,
        required: true
    }
});


const messages = ref("");
const categories = ref([]);

const referenceToModify = ref({});
const fields = ref([]);

// Emit events: to close the modal and save changes
const emit = defineEmits([EMIT_TYPE.CLOSE, EMIT_TYPE.SAVE]);

watch(() => props.reference, (newVal) => {
    referenceToModify.value = { ...newVal };
},
    { immediate: true });

watch(
    () => props.missingFields,
    (newMissingFields) => {
        fields.value = [];
        messages.value = newMissingFields.message + "\n";
        Object.entries(newMissingFields.error).forEach(([fieldName, message]) => {
            if (fieldName != "registration") {
                fields.value.push(fieldName);
                messages.value += message + "\n";
            }
        });
    },
    { immediate: true }
);

onMounted(() => {
    if (fields.value.includes(MISSING_FIELDS.MISSING_CATEGORY)) {
        loadCategories()
    }
})

async function saveReferenceModification() {
    try {
        await modifyReference(referenceToModify.value);
        nextTick();
        emit(EMIT_TYPE.SAVE, referenceToModify.value);
        messageHandler(ADMIN_MESSAGES.REFERENCE_MODIFICATION_SUCCESS, MESSAGE_HANDLER_TYPE.SUCCESS);
    } catch (err) {
        messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR)
    }
}

async function loadCategories() {
    try {
        categories.value = await getActiveCategories();
    } catch (err) {
        messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR)
    }
}
</script>

<style>
#modal {
    z-index: 101;
}
</style>