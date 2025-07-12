<template>
    <div v-if="Object.keys(errors).length > 0 && Object.keys(errors).includes('quantity')">
        <TooltipButton label="Modifier Stock" tooltip="Modifier une saisie de stock incohérente"
            @click="() => modifyStockRegistration()" />
    </div>
    <div v-for="registration in registrationsToUpdate" :key="registration.stockRegistrationId">
        <StockRegistrationModal v-if="showNewRegistrationModal[registration.stockRegistrationId]" :stock-registration="registration"
            @close="showNewRegistrationModal[registration.stockRegistrationId] = false"
            @save="(stockRegistration) => saveRegistrationModification(stockRegistration)" />
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import StockRegistrationModal from '@/components/modal/StockRegistrationModal.vue';
import { EMIT_TYPE } from '@/utils/constants/generalText';
import { actionUpdateRegistration } from '@/utils/registrationAction';
import TooltipButton from './TooltipButton.vue';

const props = defineProps({
    reference : {
        type: Object
    },
    registrations: {
        type: Array
    },
    errors: {
        type: Object,
    },
    areaName : {
        type: String
    }

})

const registrationsToUpdate = ref([]);
const showNewRegistrationModal = ref([]);

const emit = defineEmits([EMIT_TYPE.CHANGE])

onMounted(() => registrationProcess());

function registrationProcess() {
    for (let registration of props.reference.registrations) {
        if (registration.quantity < 0) {
            registration.idReference = props.reference.referenceId;
            registration.storageAreaName = props.areaName;
            registration.referenceName = props.reference.referenceName;
            registrationsToUpdate.value.push(registration);
            showNewRegistrationModal.value[registration.stockRegistrationId] = false;
        }
    }
}

function modifyStockRegistration(){
    showNewRegistrationModal.value = showNewRegistrationModal.value.map(()=>true);
}

function saveRegistrationModification(stockRegistration){
    actionUpdateRegistration(stockRegistration)
    showNewRegistrationModal.value[stockRegistration.stockRegistrationId] = false;
    emit(EMIT_TYPE.CHANGE)
}
</script>