<template>
    <div v-if="reference.numberOfRegistration == 0">
        <TooltipButton label="Stock précédent"
            tooltip="Ajoute à l'inventaire la dernière saisie de stock connue pour cette référence."
            @click="() => savePreviousStock(reference.referenceName, areaName)" />

        <TooltipButton label="Saisir stock"
            tooltip="Ajoute une nouvelle saisie de stock pour cette référence dans cette zone."
            @click="() => addStockRegistration(reference, areaName)" />
        <TooltipButton label="Retirer de la zone" tooltip="Retire cette référence de la zone de stockage concernée"
            @click="() => modifyReferenceAreas(reference.referenceId, areaName)" />
    </div>
    <div v-if="Object.keys(reference.errors).length > 0 && Object.keys(reference.errors).some(key =>
        ['price', 'category', 'formula', 'packaging'].includes(key)
    )">
        <TooltipButton label="Modif référence" tooltip="Modifier la référence" @click="() => changeReference()" />
    </div>
    <StockRegistrationModal v-if="showNewRegistrationModal" :stock-registration="registrationModalData"
        @close="showNewRegistrationModal = !showNewRegistrationModal"
        @save="(stockRegistration) => saveRegistrationModification(stockRegistration)" />
    <ReferenceModificationBeforeStockRegistrationModal v-if="isModifyReferenceModalVisible"
        :isVisible="isModifyReferenceModalVisible" @save="referenceChange()
            " @close="isModifyReferenceModalVisible = false" :missingFields="missingFields"
        :reference="referenceToUpdate" />
</template>

<script setup>
import { ref } from 'vue';
import ReferenceModificationBeforeStockRegistrationModal from '@/components/modal/ReferenceModificationBeforeStockRegistrationModal.vue';
import StockRegistrationModal from '@/components/modal/StockRegistrationModal.vue';
import { deleteAreaOnReference } from '@/controller/referenceController';
import { addLastStockRegistrationToCurrentInventory } from '@/controller/registrationController';
import { EMIT_TYPE } from '@/utils/constants/generalText';
import { INVENTORY_MESSAGES } from '@/utils/constants/inventoryProcessText';
import { MESSAGE_HANDLER_TYPE } from '@/utils/constants/messageHandlerTypeText';
import { messageHandler } from '@/utils/messageManager/messageHandler';
import { actionSaveRegistration } from '@/utils/registrationAction';
import TooltipButton from './TooltipButton.vue';

const props = defineProps({
    reference: {
        type: Object,
        required: true,
    },
    areaName: {
        type: String,
        required: true,
    }
})
const emit = defineEmits([EMIT_TYPE.SAVE_PREVIOUS_STOCK, EMIT_TYPE.ADD_NEW_STOCK, EMIT_TYPE.CHANGE]);

const showNewRegistrationModal = ref(false);
const registrationModalData = ref({});
const referenceToUpdate = ref(null);
const missingFields = ref({ message: "", error: {} });
const isModifyReferenceModalVisible = ref(false);
const registrationToSave = ref(null);

const savePreviousStock = async (referenceName, areaName) => {
    try {
        await addLastStockRegistrationToCurrentInventory(referenceName, areaName);
        messageHandler(INVENTORY_MESSAGES.LAST_STOCK_REGISTRATION_ADD_SUCCESS, MESSAGE_HANDLER_TYPE.SUCCESS)
        emit(EMIT_TYPE.CHANGE)
    } catch (err) {
        messageHandler(err.message, MESSAGE_HANDLER_TYPE.WARNING)
    }
}

const addStockRegistration = (reference, areaName) => {
    registrationModalData.value = {
        ...reference,
        storageAreaName: areaName,
        quantity: '',
        comment: ''
    };
    showNewRegistrationModal.value = true;
}

const changeReference = async () => {
    referenceToUpdate.value = { ...props.reference };
    missingFields.value.error = { ...referenceToUpdate.value.errors }
    missingFields.value.message = "Référence incomplète : "
    isModifyReferenceModalVisible.value = true;
}

const modifyReferenceAreas = async (referenceId, areaName) => {
    try{
        await deleteAreaOnReference(referenceId, areaName)
        messageHandler("Référence modifiée avec succés", MESSAGE_HANDLER_TYPE.SUCCESS)
        emit(EMIT_TYPE.CHANGE)
    } catch (err){
        messageHandler(err.message, MESSAGE_HANDLER_TYPE.WARNING)
    }
}

const referenceChange = () => {
    if (registrationToSave.value) {
        saveRegistrationModification(registrationToSave.value);
        registrationToSave.value = null;
    }

    isModifyReferenceModalVisible.value = false;
    emit(EMIT_TYPE.CHANGE)
}

const saveRegistrationModification = async (stockRegistration) => {
    const error = await actionSaveRegistration(stockRegistration);
    if (error) {
        missingFields.value = error.errors;
        referenceToUpdate.value = error.reference;
        isModifyReferenceModalVisible.value = true;
        registrationToSave.value = stockRegistration;
        return;
    }
    showNewRegistrationModal.value = false;
    emit(EMIT_TYPE.CHANGE)
}

</script>