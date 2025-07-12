<template>
  <!-- Wrapper for the form -->
  <div class="w-full">
    <form @submit.prevent="submit" class="flex flex-col items-center w-full">
      <!-- Form title -->
      <a :class="textClass" class="my-2">{{ TITLE_TEXT.ADD_REFERENCE }}</a>

      <!-- Grid layout for input fields -->
      <div class="grid grid-cols-12 gap-6 w-full">
        <!-- Reference name -->
        <div class="col-span-12 sm:col-span-6 md:col-span-4 lg:col-span-3 xl:col-span-2">
          <input v-model="inputName" type="text" :placeholder="INPUT_PLACEHOLDER.REFERENCE_NAME" :class="inputClass"
            maxlength="20" required>
        </div>

        <!-- Category dropdown -->
        <div class="col-span-12 sm:col-span-6 md:col-span-4 lg:col-span-3 xl:col-span-2">
          <DropDownList :color="color" :options="activeCategoriesList"
            :placeholder="DROP_DOWN_LIST_PLACEHOLDER.CATEGORY_LIST" :selectClass="inputClass" v-model="selectedCategory"
            required />
        </div>

        <!-- Storage areas dropdown -->
        <div class="col-span-12 sm:col-span-6 md:col-span-4 lg:col-span-3 xl:col-span-2">
          <StorageAreaMultiSelect v-model="selectedAreas" :options="activeAreasList" :color="color"
            :selectClass="inputClass" :placeholder="DROP_DOWN_LIST_PLACEHOLDER.STORAGE_AREA_LIST" />
        </div>

        <!-- Unit dropdown -->
        <div class="col-span-12 sm:col-span-6 md:col-span-4 lg:col-span-3 xl:col-span-2">
          <DropDownList :options="units" :color="color" :placeholder=DROP_DOWN_LIST_PLACEHOLDER.SUPPLIER_UNIT
            :selectClass="inputClass" v-model="selectedUnitSupplier" />
        </div>

        <!-- Unit dropdown -->
        <div class="col-span-12 sm:col-span-6 md:col-span-4 lg:col-span-3 xl:col-span-2">
          <DropDownList :options="units" :color="color" :placeholder=DROP_DOWN_LIST_PLACEHOLDER.COUNT_UNIT
            :selectClass="inputClass" v-model="selectedUnitCount" required />
        </div>

        <!-- Unit price input -->
        <div class="col-span-12 sm:col-span-6 md:col-span-4 lg:col-span-3 xl:col-span-2">
          <input v-model="inputPrice" type="number" :placeholder=INPUT_PLACEHOLDER.UNIT_PRICE :class="inputClass"
            :max="999999999" :min="0" step=".00001" required>
        </div>

        <!-- Units per packaging input -->
        <div class="col-span-12 sm:col-span-6 md:col-span-4 lg:col-span-3 xl:col-span-2">
          <input v-model="inputUnitByPackaging" type="number" :placeholder=INPUT_PLACEHOLDER.UNIT_PACKAGING
            :max="999999999" :min="0" :class="inputClass" maxlength="10">
        </div>

        <!-- Calculation rule input -->
        <div class="col-span-12 sm:col-span-6 md:col-span-4 lg:col-span-3 xl:col-span-2">
          <input v-model="calculationRule" type="text" :placeholder=INPUT_PLACEHOLDER.CALC_RULE :class="inputClass"
            maxlength="30">
        </div>

        <!-- Ref bdd prod input -->
        <div class="col-span-12 sm:col-span-6 md:col-span-4 lg:col-span-3 xl:col-span-2">
          <input v-model="refBdd" type="text" :placeholder=INPUT_PLACEHOLDER.REF_BDD :class="inputClass"
            maxlength="30">
        </div>

        <!-- Submit button -->
        <div class="col-span-12 grid grid-cols-12">
          <div class="col-span-12 justify-self-end">
            <button type="submit" :class="buttonClass">{{ BUTTON_TEXT.ADD }}</button>
          </div>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { getActiveAreas } from '@/controller/areaController';
import { getActiveCategories } from '@/controller/categoryController';
import { addNewReference } from '@/controller/referenceController';
import { getUnits } from '@/controller/unitsController';
import { BUTTON_TEXT, DROP_DOWN_LIST_PLACEHOLDER, EMIT_TYPE, INPUT_PLACEHOLDER, TITLE_TEXT } from '@/utils/constants/generalText';
import { MESSAGE_HANDLER_TYPE } from '@/utils/constants/messageHandlerTypeText';
import { messageHandler } from '../../../utils/messageManager/messageHandler';
import StorageAreaMultiSelect from '../areaComponents/StorageAreaMultiSelect.vue';
import DropDownList from '../genericComponents/DropDownList.vue';

// State for dropdown options
const activeCategoriesList = ref([]);
const activeAreasList = ref([]);
const units = ref([]);

// Reactive form inputs
const selectedUnitSupplier = ref("");
const selectedUnitCount = ref("");
const inputName = ref("");
const inputPrice = ref("");
const inputUnitByPackaging = ref("");
const calculationRule = ref("");
const refBdd = ref("");
const selectedAreas = ref([]);
const selectedCategory = ref("");

// Props and emits
const emit = defineEmits([EMIT_TYPE.SAVE]);
const props = defineProps({
  color: {
    type: String,
    required: true
  }
});

// Classes dynamically based on the theme color
const buttonClass = `px-4 py-2 ml-2 rounded-md bg-${props.color}-500 text-white hover:bg-${props.color}-700 shadow-sm`;
const inputClass = `w-full px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-${props.color}-500 shadow-sm`;
const textClass = `text-${props.color}-500`;

// Handles form submission
const submit = async () => {
  const reference = createReference();
  try {
    const response = await addNewReference(reference);
    messageHandler(response.message, MESSAGE_HANDLER_TYPE.SUCCESS);
    emit(EMIT_TYPE.SAVE); // Notify parent
    clearForm(); // Reset form
  } catch (err) {
    messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR)
  }

};

// Create reference object from form inputs
const createReference = () => {
  return {
    "referenceName": inputName.value,
    "unitSupplier": selectedUnitSupplier.value,
    "unitCount": selectedUnitCount.value,
    "calculationRule": calculationRule.value,
    "unitPrice": inputPrice.value,
    "supplierName": "",
    "unitByPackaging": inputUnitByPackaging.value,
    "archivedPackaging": false,
    "areasName": selectedAreas.value,
    "categoryName": selectedCategory.value,
    "referenceProductionDB": refBdd.value
  };
};

// Reset all input fields
function clearForm() {
  inputName.value = '';
  calculationRule.value = '';
  inputPrice.value = '';
  inputUnitByPackaging.value = '';
  selectedAreas.value = [];
  selectedCategory.value = '';
  selectedUnitSupplier.value = '';
  selectedUnitCount.value = '';
  refBdd.value = ''
}

// Load dropdown options from API
onMounted(loadData);

async function loadData() {
  try {
    activeCategoriesList.value = await getActiveCategories();
    activeAreasList.value = await getActiveAreas();
    units.value = await getUnits();
  } catch (err) {
    messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR);
  }
}
</script>