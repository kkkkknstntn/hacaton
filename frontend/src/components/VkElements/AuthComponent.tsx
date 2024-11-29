import React from "react";
import VkIdButton from "./VkButton";
// Подключаем компонент кнопки

const AuthComponent: React.FC = () => {
  const handleClick = () => {
    window.location.href = "http://localhost/api/auth/oauth2/vk";
  };

  return (
    <div>
      <VkIdButton onClick={handleClick} />
    </div>
  );
};

export default AuthComponent;
