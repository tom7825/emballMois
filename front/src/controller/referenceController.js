import { getBackUrl } from "./BackUrl";
import { getAuthHeaders } from "./security";
import { fetchWithAuth, handleApiResponse } from "./utilsController";

// Define the base URL for the reference API
const backUrl = `${getBackUrl()}/reference`;

// Function to get active references from the API
export const getActiveReferences = async () => {
    // Send a request to fetch active references
    const response = await fetchWithAuth(backUrl + `/references/active`, getAuthHeaders());
    return await handleApiResponse(response);
};

// Function to get archived references from the API
export const getArchivedReferences = async () => {
    // Send a request to fetch archived references
    const response = await fetchWithAuth(backUrl + `/references/archived`, getAuthHeaders());
    return await handleApiResponse(response);
};

// Function to get references by a specific area
export const getReferencesByArea = async (areaName) => {
    // Send a request to fetch references by area name
    const response = await fetchWithAuth(backUrl + `/references/area/${areaName}`, getAuthHeaders());
    return await handleApiResponse(response);
};

// Function to add a new reference to the API
export const addNewReference = async (reference) => {
    // Prepare the request options for the POST request
    const requestOptions = {
        method: 'POST', // HTTP method is POST
        ...getAuthHeaders(), // Add the authentication headers
        body: JSON.stringify(reference) // Add the reference data as JSON in the body
    };
    // Send the request to add the new reference
    const response = await fetchWithAuth(backUrl + `/add`, requestOptions);
    return await handleApiResponse(response);
};

// Function to add a new reference to the API
export const addNewMinimalReference = async (reference) => {
    // Prepare the request options for the POST request
    const requestOptions = {
        method: 'POST', // HTTP method is POST
        ...getAuthHeaders(), // Add the authentication headers
        body: JSON.stringify(reference) // Add the reference data as JSON in the body
    };

    // Send the request to add the new reference
    const response = await fetchWithAuth(backUrl + `/add/minimal`, requestOptions);
    return await handleApiResponse(response);
};

// Function to modify an existing reference in the API
export const modifyReference = async (reference) => {
    // Prepare the request options for the POST request
    const requestOptions = {
        method: 'POST', // HTTP method is POST
        ...getAuthHeaders(), // Add the authentication headers
        body: JSON.stringify(reference) // Add the reference data as JSON in the body
    };

    // Send the request to modify the reference
    const response = await fetchWithAuth(backUrl + `/modify`, requestOptions);
    return await handleApiResponse(response);
};

export const getAllReferencesButAlreadyExistsInThisArea = async (areaName) => {

    const response = await fetchWithAuth(backUrl + `/references/all/but/${areaName}`, getAuthHeaders());
    return await handleApiResponse(response);
};

export const deleteAreaOnReference = async (referenceId, areaName) => {
    const encodedRef = encodeURIComponent(referenceId);
    const encodedArea = encodeURIComponent(areaName);
    const requestOptions = {
        method: 'POST', // HTTP method is POST
        ...getAuthHeaders(), // Add the authentication headers
    };
    const response = await fetchWithAuth(backUrl + `/${encodedRef}/delete/area/${encodedArea}`, requestOptions)
    return await handleApiResponse(response)
}
