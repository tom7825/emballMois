import { showErrorToast, showInfoToast, showSuccessToast } from "./messageToast";

export const messageHandler = (message, type, time) => {
    switch (type) {
        case 'error':
            showErrorToast(message,time);
            break;
        case 'warning':
            showInfoToast(message,time);
            break;
        case 'success':
            showSuccessToast(message,time);
            break;
        default : 
            showErrorToast('Une erreur est survenue.' + message);
            break;
    }
};
