import { fetchWithToken } from "./fetchService";

export const sendLike = async (userId: number, targetUserId: number, typeOfLike: number): Promise<void> => {
  try {
    const response = await fetchWithToken<void>(`/api/${userId}/like?targetUserId=${targetUserId}&typeOfLike=${typeOfLike}`, {
      method: "POST",
    });
    
    console.log(`Like from user ${userId} to user ${targetUserId} sent successfully.`, response);
  } catch (error) {
    console.error("Error sending like:", error);
    throw new Error("Failed to send like.");
  }
};
// const userId = 123; // The user who is liking
// const targetUserId = 456; // The user who is being liked
// const typeOfLike = 1; // Type of like (e.g., 1 for regular like, 2 for super like)

// sendLike(userId, targetUserId, typeOfLike)
//   .then(() => {
//     console.log("Like successfully sent.");
//   })
//   .catch((error) => {
//     console.error("Error:", error);
//   });