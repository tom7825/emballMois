<template>
  <!-- Single item row with name and archive button -->
  <li class="border-b py-2 flex justify-between items-center">
    <!-- Display the name of the item -->
    <div>{{ item.name }}</div>
    
    <!-- Button to toggle archive status -->
    <button 
      @click="onArchiveChange"
      class="px-2 py-1 ml-2 rounded-md text-sm"
      :class="buttonClass" 
    >
      {{ buttonText }} <!-- Button label changes based on archive state -->
    </button>
  </li>
</template>

<script setup>
import { defineProps, defineEmits, computed } from 'vue';
import { ADMIN_VIEW_TEXT } from '@/utils/constants/administrationText';
import { EMIT_TYPE } from '@/utils/constants/generalText';

// Props received from the parent component
const props = defineProps({
  item: {
    type: Object,       // The item to display and manage
    required: true
  },
  buttonClass: {
    type: String        // CSS classes for styling the button
  }
});

// Emit events back to the parent
const emit = defineEmits([EMIT_TYPE.MODIFY]);

// Trigger when the archive toggle button is clicked
const onArchiveChange = () => {
  // Emit the item's name and the toggled archive state
  emit(EMIT_TYPE.MODIFY, { name: props.item.name, isArchived: !props.item.isArchived });
};

// Computed property to set button text based on item archive state
const buttonText = computed(() => (props.item.isArchived ? ADMIN_VIEW_TEXT.ACTIVE_BUTTON : ADMIN_VIEW_TEXT.ARCHIVE_BUTTON));
</script>
