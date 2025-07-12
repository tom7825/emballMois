import { getBackUrl } from "./BackUrl";
import { getAuthHeaders } from "./security";
import { fetchWithAuth } from "./utilsController";

const backUrl = `${getBackUrl()}/report`;

// Function to load registration data for a specific inventory
export const loadReport = async (idInventory) => {
    // Send a request to the API to get a report of inventory
    const response = await fetchWithAuth(backUrl + `/generate/${idInventory}`, getAuthHeaders());
    return response;
}