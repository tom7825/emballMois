<template>
  <!-- Full-width container centered text -->
  <div class="w-full text-center">
    <!-- Section title with dynamic text and styling -->
    <a :class="textClass" class="block">Ajout d'une nouvelle {{ objectType }}</a>

    <!-- Form for adding a new item, submission is handled by the submit function -->
    <form @submit.prevent="submit" class="mt-2">
      <!-- Input field bound to inputValue -->
      <input v-model="inputValue" type="text" :placeholder="inputPlaceholder" :class="inputClass" maxlength="20">

      <!-- Submit button with dynamic styling -->
      <button type="submit" :class="buttonClass">
        Ajouter
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { getPlaceHolderAddForm } from '@/utils/constants/administrationText';

// Input value bound to the form's text field
const inputValue = ref('');

// Define props to receive values from the parent component
const props = defineProps({
  // Used to customize the form (e.g., 'zone', 'catégorie')
  objectType: {
    type: String,
    required: true
  },
  // Used for dynamic styling (e.g., green, blue)
  color: {
    type: String,
    required: true
  },
});

// Declare the emitted event(s)
const emit = defineEmits(['submit']);

// Placeholder text for the input field
const inputPlaceholder = getPlaceHolderAddForm(props.objectType);

// Tailwind CSS classes with dynamic color binding
const buttonClass = `px-4 py-2 ml-2 rounded-md bg-${props.color}-500 text-white hover:bg-${props.color}-700`;
const inputClass = `px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-${props.color}-500`;
const textClass = `text-${props.color}-500`;

// Function to handle form submission
const submit = () => {
  emit('submit', inputValue.value);  // Emit the value to the parent
  inputValue.value = '';             // Reset the input field after submission
};
</script>