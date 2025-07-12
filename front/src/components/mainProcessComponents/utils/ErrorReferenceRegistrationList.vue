<template>
    <div v-if="props.errors && Object.keys(props.errors).length > 0" class="grid grid-cols-12 mt-2 ml-1 gap-x-2">
        <h6 class="col-span-12">Erreur dans la référence de stock : </h6>
        <div v-for="(error, errorIndex) in errors" :key="errorIndex" class="col-span-10">
            <span class="text-sm ml-2 text-red-500">{{ error }}</span>
        </div>
    </div>
</template>

<script setup>
import { ref, watch } from 'vue';


const props = defineProps({
    errors: {
        type: Object,
    },
    registrations: {
        type: Array,
    }
})

const errorsRegistrations = ref(null);

watch(props.registrations, (newData) => {
    errorsRegistrations.value = Array.from(
        new Set(
            newData
                .map(r => JSON.stringify(r.errors))
                .filter(e => e !== '{}' && e !== undefined && e !== null)
        )
    ).map(e => JSON.parse(e));
}, { immediate: true });

</script>