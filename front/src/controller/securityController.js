'use strict'

import { getBackUrl } from "./BackUrl";
import { handleApiResponse } from "./utilsController";

// Define the API URL for authentication
const backUrl = `${getBackUrl()}/auth`;

// Function to attempt login with a password
export const login = async (password) => {
    // Prepare options for the HTTP request
    const requestOptions = {
        method: 'POST', // POST request method
        headers: {
            'Content-Type': 'application/json', // Indicates the request body is in JSON format
        },
        body: JSON.stringify({
            "password": password // The password is sent in the request body
        })
    };

    // Send the login request to the API
    const response = await fetch(backUrl + `/login`, requestOptions);
   return await handleApiResponse(response)

};
