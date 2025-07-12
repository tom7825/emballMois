<template>
  <!-- Container with click handler to trigger item editing -->
  <div @click="openModal">
    <!-- List item displaying the reference and archive/activate button -->
    <li class="cursor-pointer border-b py-2 flex justify-between items-center">
      <div class="truncate">{{ reference.referenceName }}</div>

      <!-- Archive/activate toggle button. Click is stopped from propagating to avoid triggering the parent click. -->
      <button @click.stop="archiveStatusModification" class="px-2 py-1 ml-2 rounded-md text-sm" :class="buttonClass">
        {{ buttonText }}
      </button>
    </li>
  </div>
  <!-- Modal dialog used for modifying reference -->
  <ModifyReferenceModal v-if="isModalVisible" :reference="localReference"
    @close="closeModal" @save="modifyLocalReference" />
</template>

<script setup>
import { defineProps, defineEmits, computed, ref } from 'vue';
import ModifyReferenceModal from '@/components/modal/ModifyReferenceModal.vue';
import { modifyReference } from '@/controller/referenceController';
import { ADMIN_VIEW_TEXT } from '@/utils/constants/administrationText';
import { EMIT_TYPE } from '@/utils/constants/generalText';
import { MESSAGE_HANDLER_TYPE } from '@/utils/constants/messageHandlerTypeText';
import { messageHandler } from '@/utils/messageManager/messageHandler';

// Props definition: receives the item data and optional custom button class
const props = defineProps({
  reference: {
    type: Object,
    required: true,
  },
  buttonClass: {
    type: String,
  }
});

// Emits definitions: supports two types of events
const emit = defineEmits([EMIT_TYPE.MODIFY]);

// Modal visibility state
const isModalVisible = ref(false);

const localReference = ref({ ...props.reference });

// Emits the modify event for archiving/unarchiving
const archiveStatusModification = () => {
  const tempReference = {...localReference.value}
  tempReference.archivedPackaging = !tempReference.archivedPackaging  
  modifyLocalReference(tempReference);
};

// Closes the modal
const closeModal = () => {
  isModalVisible.value = false;
};

const openModal = () => {
  isModalVisible.value = true;
}

const modifyLocalReference = async (reference) => {
  try {
    const response = await modifyReference(reference);
    messageHandler(response.message, MESSAGE_HANDLER_TYPE.SUCCESS);
    localReference.value = reference;
    closeModal();
    emit(EMIT_TYPE.MODIFY);
  } catch (err) {
    messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR);
  }
};

// Computed property that determines button text based on archive status
const buttonText = computed(() => (localReference.value.archivedPackaging ? ADMIN_VIEW_TEXT.ACTIVE_BUTTON : ADMIN_VIEW_TEXT.ARCHIVE_BUTTON));
</script>
