<template>
    <!-- Container for a single stock registration item -->
    <div class="flex w-full min-w-[30rem] mx-2">
        <!-- Column for the reference name -->
        <div class="text-left basis-4/12">
            <span class="truncate max-w-[450px] block">Reference : {{ data.referenceName }}</span>
        </div>

        <!-- Column for the quantity and unit type -->
        <div class="text-left basis-3/12">
            <span>Quantité : {{ data.quantity }} </span>
            <!-- Conditional rendering for unit type -->
            <span v-if="data.packagingCount"> (conditionnées)</span>
            <span v-else> (unités)</span>
        </div>

        <!-- Column for the comment -->
        <div class="text-left basis-4/12">
            <span>Commentaire : {{ data.comment }}</span>
        </div>

        <div v-if="permittedModification" @click.stop="isVisble=true" class="text-left basis-1/12">
            <Pen width="18"/>
            <StockRegistrationUpdateModal v-if="isVisble" :data="data" @close="isVisble=false" @save="saveStockRegistrationUpdated"/>
        </div>
    </div>
    
</template>

<script setup>
import { Pen } from 'lucide-vue-next';
import { ref } from 'vue';
import { EMIT_TYPE } from '@/utils/constants/generalText';
import StockRegistrationUpdateModal from '../modal/StockRegistrationUpdateModal.vue';

defineProps({
    // Props definition for the stock registration item data
    data: {
        type: Object,
        required: true,
    },
    permittedModification: {
        type: Boolean,
        default: true
    }
});

const emit = defineEmits([EMIT_TYPE.UPDATE]);

const isVisble = ref(false);

const saveStockRegistrationUpdated = (stockRegistrationUpdated) => {
    isVisble.value = false;
    emit(EMIT_TYPE.UPDATE,stockRegistrationUpdated );
}

</script>