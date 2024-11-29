import { fetchWithToken } from "./fetchService";

// Типы лайков
export type LikeType = 1 | 2 | 3;

export const sendLike = async (targetId: number, likeType: LikeType): Promise<void> => {
  try {
    // Отправка запроса с необходимыми параметрами
    const response = await fetchWithToken(`/api/likes`, {
      method: "POST",
      body: JSON.stringify({
        targetId,
        likeType,  // 1 - лайк, 2 - скрытый лайк, 3 - дизлайк
      }),
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response) {
      throw new Error("Failed to send like");
    }

    // Логируем успешную отправку лайка
    console.log(`Like of type ${likeType} sent to user with ID ${targetId}`);
  } catch (error) {
    console.error("Error sending like:", error);
    throw new Error("Failed to send like");
  }
};
