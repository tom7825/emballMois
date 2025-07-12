<template>
    <!-- Label container for the switch element, with cursor-pointer and full width for the label -->
    <label class="flex flex-col items-center cursor-pointer w-full">
        <!-- Hidden checkbox input, styled as a peer element to control the switch -->
        <input 
            type="checkbox" 
            class="sr-only peer"  
            :checked="modelValue"  
            @change="update"  
            v-bind="$attrs"
            :aria-checked="modelValue.toString()"
            :aria-label="modelValue ? 'Vers unités' : 'Vers conditionnement'"

        />
        <!-- The switch container with transition effects when the checkbox state changes -->
        <div class="w-[50px] h-[25px] bg-gray-200 peer-checked:bg-teal-400 rounded-full relative transition-colors">
            <!-- The knob of the switch, positioned absolutely inside the container, moves when checked -->
            <div 
                class="w-[21px] h-[21px] bg-white border border-gray-200 rounded-full absolute top-[2px] left-[2px] transition-transform duration-300 ease-in-out"
                :class="{ 'translate-x-[24px]': modelValue }"  
            ></div><!-- When modelValue is true, move the knob to the right -->
        </div>
        
        <!-- The label text that displays 'Conditionnement' when checked and 'Unités' when unchecked -->
        <span class="mt-1 text-xs text-gray-600 opacity-75 text-center w-full max-w-full overflow-hidden truncate">
            {{ modelValue ? 'Conditionnement' : 'Unités' }}  <!-- Conditional rendering of the label text -->
        </span>
    </label>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

// Define the props that the component accepts, in this case the 'modelValue' prop
defineProps({
    modelValue: {
        type: Boolean,  
        default: false, 
    }
})

// Define the event emitter for updating the modelValue
const emit = defineEmits(['update:modelValue'])

// Function to handle the change event when the checkbox is clicked
const update = (event) => {
    const checked = event.target.checked;  // Get the new checked state of the checkbox
    emit('update:modelValue', checked);  // Emit the 'update:modelValue' event with the new checked value
}
</script>
