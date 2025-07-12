<template>
  <div class="modal-overlay">
    <div class="modal">
      <div class="modal-header text-center items-center">
        <h2>{{ TITLE_TEXT.CONFIRM_INVENTORY_CLOSURE }}</h2>
        <button @click="emit(EMIT_TYPE.CLOSE)" aria-label="Fermer">
          <X />
        </button>
      </div>
      <div class="modal-body flex flex-col items-center">
        <p class="mb-4 text-red-600">
          Certaines zones de l'inventaire contiennent encore des erreurs ou ne sont pas complètement validées.
        </p>
        <p class="mb-4">
          Si vous continuez, l'inventaire sera <strong>clôturé définitivement</strong> et vous ne pourrez plus corriger ces erreurs.
        </p>
        <div class="flex justify-end gap-4 mt-6">
          <button @click="emit(EMIT_TYPE.CLOSE)" class="px-4 py-2 bg-gray-300 rounded shadow">
            Annuler
          </button>
          <button @click="emit(EMIT_TYPE.CONFIRM)" class="px-4 py-2 bg-red-600 text-white rounded shadow hover:bg-red-700">
            Confirmer la clôture
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { X } from 'lucide-vue-next';
import { EMIT_TYPE, TITLE_TEXT } from '@/utils/constants/generalText';

const emit = defineEmits([EMIT_TYPE.CONFIRM, EMIT_TYPE.CLOSE]);
</script>

<style scoped>
.modal-overlay {
  z-index: 50;
}
</style>
