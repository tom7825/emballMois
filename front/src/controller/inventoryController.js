import { getBackUrl } from "./BackUrl";
import { getAuthHeaders } from "./security";
import { fetchWithAuth, handleApiResponse } from "./utilsController";

// Define the base URL for the inventory API
const backUrl = `${getBackUrl()}/inventory`;

// Function to check if an inventory process is in progress
export const isInventoryInProgress = async () => {
    // Send a request to check if an inventory is currently in progress
    let response = await fetchWithAuth(backUrl + `/exists`, getAuthHeaders());
    return await handleApiResponse(response); // Return the data indicating if an inventory is in progress
};

// Function to start a new inventory process
export const startInventory = async () => {
    // Send a request to start a new inventory
    const response = await fetchWithAuth(backUrl + `/create`, getAuthHeaders());
    return await handleApiResponse(response);
};

// Function to stop the current inventory process
export const stopInventory = async () => {
    // Send a request to end the current inventory process
    const response = await fetchWithAuth(backUrl + `/end`, getAuthHeaders());
    return await handleApiResponse(response);
};

export const getRegistrationValidated = async (idInventory) => {
    const response = await fetchWithAuth(backUrl + `/validation/inventory/${idInventory}`, getAuthHeaders());
    return await handleApiResponse(response);
}

// Function to load all inventories from the API
export const loadInventories = async () => {
    // Send a request to fetch all inventories
    const response = await fetchWithAuth(backUrl + `/inventories`, getAuthHeaders());
    return await handleApiResponse(response);
};
