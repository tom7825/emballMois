// Function to retrieve authentication headers with the stored token
export const getAuthHeaders = () => {
  const token = localStorage.getItem("token"); // Get the token from local storage
  return {
    headers: {
      'Authorization': `Bearer ${token}`, // Add the Bearer token to the Authorization header
      'Content-Type': 'application/json', // Indicate that the request body is in JSON format
    },
  };
};