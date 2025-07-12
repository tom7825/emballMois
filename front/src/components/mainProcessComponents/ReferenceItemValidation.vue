<template>
  <div>
    <div class="flex">
      <!-- Component to display individual reference details -->
      <div class="flex flex-row w-full pr-2 py-1">
        <div class="basis-5/12">Référence : {{ reference.referenceName }}</div>
        <div class="basis-5/12">Nombre de saisie de stock : {{ reference.numberOfRegistration }}</div>
        <div class="basis-2/12 flex justify-end">
          <div v-if="reference.isValid">
            <Check class="text-green-500" />
          </div>
          <div v-else>
            <TriangleAlert class="text-yellow-500" />
          </div>
        </div>
      </div>
    </div>
    <div v-if="!reference.isValid">
      <div>
        <ErrorReferenceRegistrationList :errors="reference.errors" :registrations="reference.registrations" />
      </div>
      <div class="flex">
        <ReferenceValidationAction :reference="reference" :areaName="areaName"
          v-on:[EMIT_TYPE.CHANGE]="updateRegistrationView" />
        <RegistrationValidationAction :reference="reference" :errors="reference.errors" :areaName="areaName"
          v-on:[EMIT_TYPE.CHANGE]="updateRegistrationView" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { TriangleAlert, Check } from 'lucide-vue-next';
import { defineProps } from 'vue';
import { EMIT_TYPE } from '@/utils/constants/generalText';
import ErrorReferenceRegistrationList from './utils/ErrorReferenceRegistrationList.vue';
import ReferenceValidationAction from './utils/ReferenceValidationAction.vue';
import RegistrationValidationAction from './utils/RegistrationValidationAction.vue';

defineProps({
  reference: {
    type: Object,
    required: true,
  },
  buttonClass: {
    type: String,
  },
  areaName: {
    type: String,
    required: true,
  }
});

const emit = defineEmits([EMIT_TYPE.CHANGE]);

const updateRegistrationView = () => {
  emit(EMIT_TYPE.CHANGE);
}

</script>
