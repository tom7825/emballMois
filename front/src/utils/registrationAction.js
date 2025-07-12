import { saveRegistration, updateRegistration } from "@/controller/registrationController";
import { messageHandler } from "@/utils/messageManager/messageHandler";
import { MESSAGE_HANDLER_TYPE } from "./constants/messageHandlerTypeText";

export const actionSaveRegistration = async (registration) => {
    try {
        const response = await saveRegistration(registration);
        messageHandler(response.message, MESSAGE_HANDLER_TYPE.SUCCESS)
    } catch (err) {
        return proceedResponseData(err);
    }
}

export const actionUpdateRegistration = async (registration) => {
    try {
        const response = await updateRegistration(registration);
        messageHandler(response.message, MESSAGE_HANDLER_TYPE.SUCCESS)
    } catch (err) {
        return proceedResponseData(err);
    }

}

const proceedResponseData = async (response) => {
    const dataWithErrors = {
        errors: {
            error: { ...response.errors },
            message: response.message
        },
        stockRegistration: { ...response.data.stockRegistration },
        reference: { ...response.data.reference }
    }
    return dataWithErrors;
}