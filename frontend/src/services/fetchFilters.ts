import { fetchWithToken } from "./fetchService";

export interface Filter{
    minAge : number;
    maxAge : number;
    genderFilter : string;
    searchRadius : number;
}

//filters/{userId}

export const fetchFilters = async (userId: number): Promise<Filter> => {
    try {
      const response = await fetchWithToken<Filter>(`/api/users/filters/${userId}`, {
        method: "GET",
      });
  
      if (!response) {
        throw new Error("No response from the server.");
      }
  
      return response;
    } catch (error) {
      console.error("Error fetching filters:", error);
      throw new Error("Failed to fetch filters");
    }
  };
  
  export const updateFilters = async (userId: number, filters: Filter): Promise<Filter> => {
    try {
      const response = await fetchWithToken<Filter>(`/api/users/filters/${userId}`, {
        method: "PATCH",
        body: JSON.stringify(filters),
        headers: {
          "Content-Type": "application/json",
        },
      });
  
      if (!response) {
        throw new Error("No response from the server.");
      }
  
      return response;
    } catch (error) {
      console.error("Error updating filters:", error);
      throw new Error("Failed to update filters");
    }
  };
  