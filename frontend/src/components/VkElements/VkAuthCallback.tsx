import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { vkAuthSuccess } from "../../store/authSlice";

const VkAuthCallback: React.FC = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const accessToken = params.get("access_token");
    const accessExpiresAt = params.get("access_expires_at");
    const userIdParam = params.get("user_id");

    if (accessToken && accessExpiresAt && userIdParam) {
      const userId = Number(userIdParam);
      dispatch(vkAuthSuccess({ token: accessToken, userId }));

      navigate("/myprofile");
    } else {
      console.log(params);
      console.error("Access token или access_expires_at не найдены в URL");
    }
  }, [dispatch, navigate]);

  return null;
};

export default VkAuthCallback;
