import { getBackUrl } from "./BackUrl";
import { isInventoryInProgress } from "./inventoryController";
import { getAuthHeaders } from "./security";
import { fetchWithAuth, handleApiResponse } from "./utilsController";

// Define the base URL for the registration API
const backUrl = `${getBackUrl()}/registration`;

// Function to save a new registration
export const saveRegistration = async (registration) => {
    // Prepare the request options including method, headers, and body
    const requestOptions = {
        method: 'POST', // HTTP method
        ...getAuthHeaders(), // Add authentication headers
        body: JSON.stringify(registration), // Convert the registration object to JSON
    };
    // Send the request to the API to add the registration
    const response = await fetchWithAuth(backUrl + `/add`, requestOptions);
    return await handleApiResponse(response);
};

// Function to load registrations data for a specific inventory
export const loadRegistrations = async (idInventory) => {
    // Send a request to the API to get all registrations for a specific inventory
    const response = await fetchWithAuth(backUrl + `/allStockRegistration/inventory/${idInventory}`, getAuthHeaders());
    return await handleApiResponse(response);
};


// Function to load registration data by id
export const loadRegistrationById = async (idRegistration) => {
    // Send a request to the API to get registration by its id
    const response = await fetchWithAuth(backUrl + `/registration/${idRegistration}`, getAuthHeaders());
    return await handleApiResponse(response);
};

// Function to load registration data for a specific inventory
export const loadRegistrationForValidation = async (idInventory) => {
    // Send a request to the API to get all registrations for a specific inventory
    const response = await fetchWithAuth(backUrl + `/allStockRegistration/inventory/${idInventory}/validation`, getAuthHeaders());
    return await handleApiResponse(response); // Return the registration data

};

// Function to get all stock registrations for an inventory that is in progress
export const getAllStockRegistrationForInventoryInProgress = async () => {
    // Check if an inventory is in progress
    const inventoryInProgress = (await isInventoryInProgress()).data;
    const idInventory = inventoryInProgress.idInventory; // Get the ID of the in-progress inventory
    return await loadRegistrations(idInventory); // Load the registrations for the in-progress inventory
};

// Function to add last stock registration
export const addLastStockRegistrationToCurrentInventory = async (referenceName, areaName) => {
    // Send a request to the API to add last known registration to current inventory
    const params = new URLSearchParams({
        areaName: areaName,
        referenceName: referenceName
    });
    const response = await fetchWithAuth(backUrl + `/lastStockRegistration?${params.toString()}`, getAuthHeaders());
    return await handleApiResponse(response);
};

export const updateLasRegistrationForAllRefInArea= async (areaName) => {
    // Send a request to the API to add last known registration to current inventory
    const params = new URLSearchParams({
        areaName: areaName
    });
    const response = await fetchWithAuth(backUrl + `/lastStockRegistration/all?${params.toString()}`, getAuthHeaders());
    return await handleApiResponse(response);
};

export const updateRegistration = async (registration) => {
    const requestOptions = {
        method: 'POST', // HTTP method
        ...getAuthHeaders(), // Add authentication headers
        body: JSON.stringify(registration), // Convert the registration object to JSON
    };

    const response = await fetchWithAuth(backUrl + '/modify', requestOptions);
    return await handleApiResponse(response)
}


