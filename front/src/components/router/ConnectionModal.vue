<template>
  <!-- Login modal wrapper, only visible if isLoginVisible is true -->
  <div v-if="isLoginVisible" class="modal-overlay">
    <div class="modal">
      <!-- Modal header -->
      <div class="modal-header">
        <h3 class="font-bold">{{ TITLE_TEXT.CONNECTION }}</h3>
      </div>
      <!-- Modal body containing the login form -->
      <div class="modal-body">
        <form @submit.prevent="submitForm" class="space-y-3">
          <!-- Password input -->
          <div >
            <input id="passwordInput" type="password" v-model="password" placeholder="Mot de passe"
              class="px-4 py-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
          </div>
          <!-- Submit button -->
          <div class="text-right" >
            <button id="connectionButton" type="submit" class="style-modal">{{BUTTON_TEXT.CONNECTION}}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { login } from '@/controller/securityController';
import { isLoginVisible } from '@/stores/authModalState';
import { APP_MESSAGES, BUTTON_TEXT, EMIT_TYPE, TITLE_TEXT } from '@/utils/constants/generalText';
import { MESSAGE_HANDLER_TYPE } from '@/utils/constants/messageHandlerTypeText';
import { messageHandler } from '../../utils/messageManager/messageHandler';

// Reactive variable for password input
const password = ref('');

// Declare emit event for save action
const emit = defineEmits([EMIT_TYPE.SAVE]);

// Function triggered on form submit
const submitForm = async () => {
  try {
    // Attempt to log in and store token
    const token = await login(password.value);
    localStorage.setItem('token', token.token);
    isLoginVisible.value = false;
    emit(EMIT_TYPE.SAVE); // Notify parent of successful login
    messageHandler(APP_MESSAGES.CONNECTION_SUCCESS, MESSAGE_HANDLER_TYPE.SUCCESS, 1000);
  } catch (err) {
    // Display error message on failure
    messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR);
  }
};
</script>

<style scoped>
/* Modal content box */
.modal {
  padding: 2rem;
  width: 100%;
  max-width: 400px; /* Contrôle la taille max en desktop */
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

/* Centrer la modale dans l'écran */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

/* Modal header with centered title */
.modal-header {
  justify-content: center;
}

/* Modal body centered layout */
.modal-body {
  display: flex;
  justify-content: center;
  
}
</style>