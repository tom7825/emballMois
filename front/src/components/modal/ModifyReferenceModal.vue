<template>
    <!-- Modal overlay: shown only when 'isVisible' is true -->
    <div class="modal-overlay">
        <!-- Modal content -->
        <div class="modal text-left">
            <!-- Modal header with title and close button -->
            <div class="modal-header text-center items-center">
                <h3> {{ OTHER_TEXT.MODIFY + localReference.referenceName }}</h3>
                <button @click="close" class="m-0" style="margin-top: 0px;">
                    <X />
                </button>
            </div>

            <!-- Modal body containing the editable form -->
            <div class="modal-body">
                <form @submit.prevent="submitForm" class="space-y-6">
                    <!-- Supplier input -->
                    <div class="space-x-2">
                        <label for="supplierName">Fournisseur :</label>
                        <input v-model="localReference.supplierName" id="supplierName" type="text"
                            class="style-modal" ref="supplierInput" />
                    </div>

                    <!-- Unit price input -->
                    <div class="space-x-2">
                        <label for="unitPrice">Prix unitaire :</label>
                        <input v-model="localReference.unitPrice" id="unitPrice" type="number" class="style-modal"
                            step=".00001" required />
                        <span v-if="reference.numFact">Numéro de Pièce : {{ reference.numFact }}</span>
                    </div>

                    <!-- Calculation rule input -->
                    <div class="space-x-2">
                        <label for="calculationRule">Règle de calcul :</label>
                        <input v-model="localReference.calculationRule" id="calculationRule" class="style-modal"
                            type="text" />
                    </div>

                    <!-- Units per packaging input -->
                    <div class="space-x-2">
                        <label for="unitByPackaging">Unités par packaging :</label>
                        <input v-model="localReference.unitByPackaging" id="unitByPackaging" class="style-modal"
                            type="number" />
                    </div>

                    <!-- Units per packaging input -->
                    <div class="space-x-2">
                        <label for="bddReference">Référence EuroFlow :</label>
                        <input v-model="localReference.referenceProductionDB" id="bddReference" class="style-modal"
                            type="text" />
                    </div>

                    <!-- Dropdown lists for category, storage areas, and units -->
                    <div class="grid grid-cols-12 gap-6">
                        <div class="col-span-12 md:col-span-6">
                            <!-- Category dropdown -->
                            <label>Catégorie</label>
                            <DropDownList :color="COLOR_VIEW.MODAL" :options="activeCategoriesList"
                                :placeholder="DROP_DOWN_LIST_PLACEHOLDER.CATEGORY_LIST" :selectClass="dropdownClass"
                                v-model="localReference.categoryName" required />
                        </div>
                        <div class="col-span-12 md:col-span-6">
                            <!-- Storage areas multi-select -->
                            <label>Zone de stockage</label>
                            <StorageAreaMultiSelect :color="COLOR_VIEW.MODAL" :options="activeAreasList"
                                :selectClass="dropdownClass" v-model="localReference.areasName" />
                        </div>
                        <div class="col-span-12 md:col-span-6">
                            <!-- Units dropdown -->
                            <label>Unité de comptage</label>
                            <DropDownList :color="COLOR_VIEW.MODAL" :options="units"
                                :placeholder=DROP_DOWN_LIST_PLACEHOLDER.COUNT_UNIT :selectClass="dropdownClass"
                                v-model="localReference.unitCount" required />
                        </div>
                        <div class="col-span-12 md:col-span-6">
                            <!-- Units dropdown -->
                            <label>Unité fournisseur</label>
                            <DropDownList :color="COLOR_VIEW.MODAL" :options="units"
                                :placeholder=DROP_DOWN_LIST_PLACEHOLDER.SUPPLIER_UNIT :selectClass="dropdownClass"
                                v-model="localReference.unitSupplier" />
                        </div>
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
import { onMounted, ref, watch } from 'vue';
import { getActiveAreas } from '@/controller/areaController';
import { getActiveCategories } from '@/controller/categoryController';
import { getUnits } from '@/controller/unitsController';
import { APP_MESSAGES, BUTTON_TEXT, COLOR_VIEW, DROP_DOWN_LIST_PLACEHOLDER, EMIT_TYPE, OTHER_TEXT } from '@/utils/constants/generalText';
import { MESSAGE_HANDLER_TYPE } from '@/utils/constants/messageHandlerTypeText';
import { messageHandler } from '../../utils/messageManager/messageHandler';
import StorageAreaMultiSelect from '../administrationComponents/areaComponents/StorageAreaMultiSelect.vue';
import DropDownList from '../administrationComponents/genericComponents/DropDownList.vue';

// Props passed to the component item to edit
const props = defineProps({
    reference: {
        type: Object,
        required: true
    }
});

// Reactive lists and selected options
const activeCategoriesList = ref([]);
const activeAreasList = ref([]);
const units = ref([]);

const supplierInput = ref(null);

// Local state for reference to modify
const localReference = ref({ ...props.reference });

// Watch for changes to props and sync with local state
watch(() => props.reference, (newValue) => {
    localReference.value = { ...newValue };
}, { deep: true });

// Load categories, areas, and units from APIs
const loadData = async () => {
    try {
        activeCategoriesList.value = await getActiveCategories();
        activeAreasList.value = await getActiveAreas();
        units.value = await getUnits();
    } catch (err) {
        messageHandler(APP_MESSAGES.LOAD_DATA_IMPOSSIBLE + err.message, MESSAGE_HANDLER_TYPE.ERROR);
    }
};

// Load dropdown data on mount
onMounted(() => {
    loadData();
    supplierInput.value?.focus();
});

// Config for styling
const dropdownClass = 'px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-gray-500 shadow-sm';

// Emit events: to close the modal and save changes
const emit = defineEmits([EMIT_TYPE.CLOSE, EMIT_TYPE.SAVE]);

// Close modal
const close = () => {
    emit(EMIT_TYPE.CLOSE);
};

// Submit form and emit the updated object
const submitForm = () => {
    emit(EMIT_TYPE.SAVE, localReference.value);
};
</script>
