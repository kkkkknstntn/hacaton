// src/store/authSlice.ts
import { createSlice, createAsyncThunk, PayloadAction } from "@reduxjs/toolkit";
import { fetchAndSetUserInfo } from "./userSlice";
import { AppDispatch, AppThunk } from ".";
import AuthState from "./authSlice.type";

const initialState: AuthState = {
  isAuthenticated: localStorage.getItem("accessToken") !== null,
  token: localStorage.getItem("accessToken"),
  userId: localStorage.getItem("userId")
    ? parseInt(localStorage.getItem("userId") as string, 10)
    : null,
  loading: false,
  error: null,
};

// Экшен для повторной авторизации
export const relogin = (): AppThunk => async (dispatch) => {
  const token = localStorage.getItem("accessToken");
  const userId = localStorage.getItem("userId");

  if (token && userId) {
    // Если есть токен и userId, обновляем Redux состояние и информацию о пользователе
    dispatch(vkAuthSuccess({ token, userId: parseInt(userId, 10) }));
    await dispatch(fetchAndSetUserInfo());
  }
};

// Экшен для обычной авторизации через логин и пароль
export const login = createAsyncThunk<
  { accessToken: string; userId: number },
  { username: string; password: string },
  { dispatch: AppDispatch }
>("auth/login", async (data, { dispatch, rejectWithValue }) => {
  try {
    const response = await fetch(`/api/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
      credentials: "include",
    });

    if (!response.ok) throw new Error("Ошибка авторизации");

    const responseData = await response.json();
    const {
      access_token: accessToken,
      access_expires_at: accessExpiresAt,
      user_id: userId,
    } = responseData;

    if (accessToken && accessExpiresAt && userId) {
      // Сохраняем данные в localStorage
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("accessTokenExpirationTime", accessExpiresAt);
      localStorage.setItem("userId", userId.toString());

      // Запрос для получения информации о пользователе
      await dispatch(fetchAndSetUserInfo());

      return { accessToken, userId };
    } else {
      throw new Error("Недостаточно данных в ответе от сервера");
    }
  } catch {
    return rejectWithValue("Ошибка авторизации");
  }
});

// Экшен для регистрации нового пользователя
export const register = createAsyncThunk(
  "auth/register",
  async (
    data: {
      username: string;
      password: string;
      first_name: string;
      last_name: string;
    },
    { rejectWithValue }
  ) => {
    try {
      const response = await fetch(`/api/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
        credentials: "include",
      });

      if (!response.ok) {
        throw new Error("Ошибка регистрации");
      }

      const responseData = await response.json();
      return { userId: responseData.user_id };
    } catch (error) {
      let errorMessage = "Неизвестная ошибка";
      if (error instanceof Error) {
        errorMessage = error.message;
      }
      return rejectWithValue(errorMessage);
    }
  }
);

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    logout(state) {
      state.isAuthenticated = false;
      state.token = null;
      state.userId = null;
      // Удаляем данные из localStorage
      localStorage.removeItem("accessToken");
      localStorage.removeItem("accessTokenExpirationTime");
      localStorage.removeItem("userId");
    },
    // Экшен для успешной авторизации через VK
    vkAuthSuccess(
      state,
      action: PayloadAction<{ token: string; userId: number }>
    ) {
      state.loading = false;
      state.isAuthenticated = true;
      state.token = action.payload.token;
      state.userId = action.payload.userId;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(
        login.fulfilled,
        (
          state,
          action: PayloadAction<{ accessToken: string; userId: number }>
        ) => {
          state.loading = false;
          state.isAuthenticated = true;
          state.token = action.payload.accessToken;
          state.userId = action.payload.userId;
          // Сохраняем userId в localStorage
          localStorage.setItem("userId", action.payload.userId.toString());
        }
      )
      .addCase(login.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(register.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(
        register.fulfilled,
        (state, action: PayloadAction<{ userId: number }>) => {
          state.loading = false;
          state.userId = action.payload.userId;
        }
      )
      .addCase(register.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });
  },
});

export const { logout, vkAuthSuccess } = authSlice.actions;
export default authSlice.reducer;
