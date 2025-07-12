<template class="grid grid-cols-12 gap-4">
    <!-- Dropdown for selecting a reference -->
    <div class="col-span-12 sm:col-span-3 lg:col-span-3 grid grid-cols-12 gap-2">
        <DropDownList label="Sélectionnez référence" :id="`selectReference-${id}`" :name="`selectReference-${id}`"
            :options="referencesForArea" placeholder="Référence"
            selectClass="px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500 shadow-sm"
            v-model="localReferenceName" required class="col-span-9" />
        <TooltipButton @click="addReferenceDuringProcess" label="+" v-if="!disableReferenceSelection"
            tooltip="Ajouter une référence qui n'existe pas dans cette zone de stockage"
            className="px-4 py-2 mt-0 rounded-md bg-teal-500 text-white hover:bg-teal-700" class="col-span-3" />
    </div>

    <!-- Input for quantity with switch to toggle packaging/unit -->
    <div class="col-span-12 sm:col-span-5 lg:col-span-4 grid grid-cols-12 gap-4">
        <div class="col-span-7 grid grid-flow-col" :class="localUnitCount ? 'grid-rows-2' : 'grid-rows-1'">
            <input v-model="localQuantity" :id="`quantity-${id}`" :name="`quantity-${id}`" type="number"
                placeholder="Quantité"
                class=" px-4 row-span-1 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500 shadow-sm col-span-2"
                :max="999999999" :min="0" step="any" required />
            <span v-if="localUnitCount" class="row-span-1 text-sm">Unité de comptage : {{ localUnitCount }}</span>
        </div>
        <!-- Switch to indicate unit type (packaged or unitary) -->
        <div class="col-span-5 flex items-start justify-center pr-4">
            <Switch v-model="localPackagingCount" :id="`switch-${id}`" />
        </div>
    </div>

    <!-- Input for optional comment about the registration -->
    <div class="col-span-12 sm:col-span-4 lg:col-span-5">
        <input v-model="localComment" type="text" placeholder="Commentaire" :id="`comment-${id}`"
            :name="`comment-${id}`"
            class="px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-teal-500 shadow-sm w-full" />
    </div>
    <AddReferenceModal v-if="addReferenceIsVisible" :storageAreaName="selectArea" @save="closeModalAddReference"
        @close="addReferenceIsVisible = false" />
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import { getReferencesByArea } from '@/controller/referenceController';
import { MESSAGE_HANDLER_TYPE } from '@/utils/constants/messageHandlerTypeText';
import { messageHandler } from '../../utils/messageManager/messageHandler';
import DropDownList from '../administrationComponents/genericComponents/DropDownList.vue';
import AddReferenceModal from '../modal/AddReferenceModalDuringStockRegistration.vue';
import Switch from './UnitPackagingSwitch.vue';
import TooltipButton from './utils/TooltipButton.vue';

const localUnitCount = ref("");
const addReferenceIsVisible = ref(false);
const referencesForArea = ref([]);


// Define the props passed from the parent
const props = defineProps({
    id: Number,
    referenceName: String,
    packagingCount: Boolean,
    comment: String,
    quantity: Number,
    selectArea: String,
    unitCount: String,
    disableReferenceSelection: {
        type: Boolean,
        default: false,
    },
});

const emit = defineEmits([
    'update:referenceName',
    'update:packagingCount',
    'update:comment',
    'update:quantity'
]);

onMounted(() => loadReferencesForArea());

const localReferenceName = ref(props.referenceName);
const localPackagingCount = ref(false);
if (props.packagingCount) {
    localPackagingCount.value = props.packagingCount;
}
const localComment = ref(props.comment);
const localQuantity = ref(props.quantity);


watch(localReferenceName, val => emit('update:referenceName', val));
watch(localPackagingCount, val => emit('update:packagingCount', val));
watch(localComment, val => emit('update:comment', val));
watch(localQuantity, val => emit('update:quantity', val));

// Update local state if props change externally
watch(() => props.selectArea, () => loadReferencesForArea());
watch(() => props.referenceName, val => localReferenceName.value = val);
watch(() => props.packagingCount, val => {
    if (val) localPackagingCount.value = val;
    else localPackagingCount.value = false;
});
watch(() => props.comment, val => localComment.value = val);
watch(() => props.quantity, val => localQuantity.value = val);
watch(localReferenceName, (localReferenceName) => {
    if (localReferenceName == "" || localReferenceName == null) {
        localUnitCount.value = "";
    } else {
        localUnitCount.value = referencesForArea.value.find(reference => reference.name == localReferenceName).unitCount
    }
});

const addReferenceDuringProcess = () => {
    addReferenceIsVisible.value = true;
}

const closeModalAddReference = () => {
    loadReferencesForArea();
    addReferenceIsVisible.value = false;
}

// Load references related to the selected area
async function loadReferencesForArea() {
    if (props.selectArea) {
        try {
            const data = await getReferencesByArea(props.selectArea);
            referencesForArea.value = data.data.map(ref => ({
                name: ref.referenceName,
                id: ref.idReference,
                unitCount: ref.unitCount
            }));
        } catch (err) {
            messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR);
        }
    }
}

</script>
