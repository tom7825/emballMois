import { addNewMinimalReference, getAllReferencesButAlreadyExistsInThisArea, modifyReference } from "@/controller/referenceController";
import { MESSAGE_HANDLER_TYPE } from "./constants/messageHandlerTypeText";
import { messageHandler } from "./messageManager/messageHandler";


export const loadReferencesButAlreadyExistsInThisAreaAndMap = async (storageAreaName) => {
    try {
        const references = await getAllReferencesButAlreadyExistsInThisArea(storageAreaName);
        return references.data.map(ref => ({
            ...ref,
            name: ref.referenceName
        }));
    } catch (err){
        messageHandler(err.message, MESSAGE_HANDLER_TYPE.ERROR)
    }
};

export const createMinimalReference = async (reference) => {
    return await addNewMinimalReference(reference);
};

export const updateReference = async (reference) => {
    return await modifyReference(reference);
};
