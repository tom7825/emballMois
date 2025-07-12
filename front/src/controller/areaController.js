import { getAreaFromItem, getAreaFromName, getItemsFromAreas } from "@/utils/areaObject";
import { getBackUrl } from "./BackUrl";
import { getAuthHeaders } from "./security";
import { fetchWithAuth, handleApiResponse } from "./utilsController";

const backUrl = `${getBackUrl()}/area`; // Set the base URL for the 'area' API endpoint

// Function to fetch areas that have active references
export const getAreasWithActiveReferences = async () => {
    // Fetch areas that have active references from the API
    const response = await fetchWithAuth(backUrl + `/areas/active/references/active`, getAuthHeaders());
    const data = await handleApiResponse(response);
    return getItemsFromAreas(data);
};

// Function to fetch all active areas
export const getActiveAreas = async () => {
    // Fetch active areas from the API
    const response = await fetchWithAuth(backUrl + `/areas/active`, getAuthHeaders());
    const data = await handleApiResponse(response);
    return getItemsFromAreas(data);
};

// Function to add a new area
export const addNewArea = async (newStorageArea) => {
    const requestOptions = {
        method: 'POST', // HTTP method for creating a new resource
        ...getAuthHeaders(), // Include authentication headers
        body: JSON.stringify(getAreaFromName(newStorageArea)),
    };

    // Send the request to add the new area
    const response = await fetchWithAuth(backUrl + `/add`, requestOptions);
    return await handleApiResponse(response);
};

// Function to modify an existing area
export const modifyArea = async (area) => {
    const requestOptions = {
        method: 'POST', // HTTP method for modifying a resource
        ...getAuthHeaders(), // Include authentication headers
        body: JSON.stringify(getAreaFromItem(area))
    };

    // Send the request to modify the area
    const response = await fetchWithAuth(backUrl + `/modify`, requestOptions);
    return await handleApiResponse(response);
};

// Function to fetch all archived areas
export const getArchivedAreas = async () => {
    // Fetch archived areas from the API
    const response = await fetchWithAuth(backUrl + `/areas/archived`, getAuthHeaders());
    const data = await handleApiResponse(response);
    return getItemsFromAreas(data);
};
