
import { isLoginVisible } from "../stores/authModalState";

// Custom function to make an authenticated request
export async function fetchWithAuth(url, options) {
    let response
    try {
        // Perform the request with the provided URL and options
        response = await fetch(url, options);
        // eslint-disable-next-line no-unused-vars
    } catch (err) {
        // Handle network or fetch-level errors (e.g., server down, connection refused)
        throw new Error("Le serveur est injoignable. Veuillez vérifier votre connexion ou réessayer plus tard.");
    }

    // If the status code is 401 (Unauthorized) and reauthentication is required
    if (response.status === 401 && response.headers.get('X-Reauth-Required') === 'true') {
        // Show the login window so the user can log in again
        isLoginVisible.value = true;
        throw new Error("Erreur d'authentification, vous devez vous reconnecter !");
    }
    // If the backend (via Nginx) is unreachable or sends server error
    if (response.status >= 500) {
        throw new Error(`Erreur serveur (${response.status}) : Veuillez vérifier votre connexion ou réessayer plus tard.`);
    }
    // Return the response if everything is okay
    return response;
}

// Handle API response and extract JSON if valid
export const handleApiResponse = async (response) => {
    // If the response is not OK (status not in 200–299)
    if (!response.ok) {
        let error
        // Try to parse the error message from the response body
        try {
            const data = await response.json()
            error = new Error(data.message)
            error.data = data.data;
            error.errors = data.errors;
        } catch {
            error = new Error('Erreur durant la réception des données')
        }
        throw error; // Throw a standardized error
    }

    // Try to parse the response body as JSON
    try {
        return await response.json();
        // eslint-disable-next-line no-unused-vars
    } catch (err) {
        // If parsing fails, throw a specific error
        throw new Error('Réponse du serveur invalide (non-JSON).');
    }
};


