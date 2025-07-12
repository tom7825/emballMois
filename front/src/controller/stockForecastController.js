import { getBackUrl } from "./BackUrl";
import { getAuthHeaders } from "./security";
import { fetchWithAuth, handleApiResponse } from "./utilsController";

// Define the base URL for the registration API
const backUrl = `${getBackUrl()}/forecast`;

/**
 * Fetches the stock forecast data from the backend using native fetch.
 * @returns {Promise<Array>} List of stock forecast DTOs
 * @throws {Error} if the request fails
 */
export async function fetchStockForecast() {
  const response = await fetchWithAuth(backUrl + '/stockdays',getAuthHeaders());
  const result = await handleApiResponse(response);
  return result.data || [];
}
