<!-- components/TooltipButton.vue -->
<template>
    <div class="relative inline-block  z-0">
        <div class="group inline-block">
            <button type="button" :class="computedClass" @click="handleClick">
                {{ label }}
            </button>
            <div class="absolute bottom-full left-1/2 -translate-x-1/2 mb-2 px-3 py-2 bg-gray-800 text-white text-sm rounded
             opacity-0 group-hover:opacity-100 transition-opacity
             max-w-[250px] w-max whitespace-normal break-words
             pointer-events-none">
                {{ tooltip }}
            </div>
        </div>
    </div>
</template>


<script setup>
import { defineProps, defineEmits, computed } from 'vue';

const props = defineProps({
    label: {
        type: String,
        required: true
    },
    tooltip: {
        type: String,
        required: true
    },
    className: {
        type: String,
        default: 'm-2 p-2 bg-blue-500 text-white text-xs rounded shadow-md w-auto hover:bg-blue-700'
    }
});

const emit = defineEmits(['click']);

const handleClick = (event) => {
    emit('click', event);
};

const computedClass = computed(() => props.className);
</script>