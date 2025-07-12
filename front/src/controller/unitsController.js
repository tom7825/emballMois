import { getBackUrl } from "./BackUrl";
import { getAuthHeaders } from "./security";
import { fetchWithAuth, handleApiResponse } from "./utilsController";

const backUrl = `${getBackUrl()}/unit`;

// Function to retrieve all units from the API
export const getUnits = async () => {
    // Sends a GET request to the API to fetch all units
    let response = await fetchWithAuth(backUrl + `/all`, getAuthHeaders());
    // Parse the response as JSON and extract only the unit names
    response = await handleApiResponse(response)
    return response.data.map(item => ({
        name: item.unit_value, // Extract the unit name from each item
    }));
};