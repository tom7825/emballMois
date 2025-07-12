import { getCategoryFromItem, getCategoryObjectFromName as getCategoryFromName, getItemsFromCategories } from "@/utils/categoryObject";
import { getBackUrl } from "./BackUrl";
import { getAuthHeaders } from "./security";
import { fetchWithAuth, handleApiResponse } from "./utilsController";

// Define the base URL for the category API
const backUrl = `${getBackUrl()}/category`;

// Function to get all active categories
export const getActiveCategories = async () => {
    // Send a request to fetch all active categories
    const response = await fetchWithAuth(backUrl + `/categories/active`, getAuthHeaders());
    const data = await handleApiResponse(response); // Parse the response data as JSON
    return getItemsFromCategories(data);
};

// Function to add a new category
export const addCategory = async (categoryName) => {
    // Prepare the request options to send the category data in JSON format
    const requestOptions = {
        method: 'POST', // The method is POST to add a new category
        ...getAuthHeaders(), // Include the authentication headers
        body: JSON.stringify(getCategoryFromName(categoryName))
    };

    // Send the request to add the category
    const response = await fetchWithAuth(backUrl + `/add`, requestOptions);
    return await handleApiResponse(response);
};

// Function to modify an existing category (e.g., archiving it)
export const modifyCategory = async (category) => {
    // Prepare the request options to modify the category data
    const requestOptions = {
        method: 'POST', // The method is POST to modify an existing category
        ...getAuthHeaders(), // Include the authentication headers
        body: JSON.stringify(getCategoryFromItem(category))
    };

    // Send the request to modify the category
    const response = await fetchWithAuth(backUrl + `/modify`, requestOptions);
    return await handleApiResponse(response);
};

// Function to get all archived categories
export const getArchivedCategories = async () => {
    // Send a request to fetch all archived categories
    const response = await fetchWithAuth(backUrl + `/categories/archived`, getAuthHeaders());
    const data = await handleApiResponse(response);
    return getItemsFromCategories(data);
};
