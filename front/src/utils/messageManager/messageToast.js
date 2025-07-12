import { useToast } from 'vue-toastification';

const toast = useToast();


export const showSuccessToast = (message, time = 5000) => {
    toast.success(message, {
        timeout: time, 
    });
};

export const showErrorToast = (message, time = 5000) => {
    toast.error(message, {
        timeout: time, 
    });
};

export const showInfoToast = (message, time = 5000) => {
    toast.info(message, {
        timeout: time, 
    });
};
