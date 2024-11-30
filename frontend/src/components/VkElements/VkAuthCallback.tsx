import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { vkAuthSuccess } from "../../store/authSlice";

const VkAuthCallback: React.FC = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    console.log("URL параметр:", window.location.search); // Проверь параметры в URL
    const params = new URLSearchParams(window.location.search);
    const code = params.get("code"); // Извлекаем код авторизации
    console.log("Код авторизации:", code);
    if (code) {
      fetch(`http://localhost/api/auth/login/oauth2/code/vk?code=${code}`, {
        method: "GET",
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error("Ошибка авторизации с VK");
          }
          return response.json();
        })
        .then((data) => {
          const { access_token, access_expires_at, user_id } = data;
          if (access_token && access_expires_at && user_id) {
            localStorage.setItem("accessToken", access_token);
            localStorage.setItem(
              "accessTokenExpirationTime",
              access_expires_at
            );
            localStorage.setItem("userId", user_id.toString());
            dispatch(vkAuthSuccess({ token: access_token, userId: user_id }));
            navigate("/myprofile");
          } else {
            console.error("Недостаточно данных в ответе от сервера");
          }
        })
        .catch((error) => {
          console.error("Ошибка при получении данных: ", error);
        });
    } else {
      console.error("Не найден код авторизации в URL");
    }
  }, [dispatch, navigate]);

  return null;
};

export default VkAuthCallback;
