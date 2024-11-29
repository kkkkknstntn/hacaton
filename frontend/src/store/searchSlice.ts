// import { createSlice, PayloadAction } from '@reduxjs/toolkit';
// import { fetchInterests, fetchUsers, sendLike } from '../services/apiService';

// export interface Interest {
//   name: string;
//   color: string;
//   textColor: string;
// }

// export interface User {
//   id: number;
//   username: string;
//   firstName: string;
//   lastName: string;
//   age: number;
//   city: string;
//   job: string;
//   education: string;
//   aboutMe: string;
//   photos: string[];
//   interests: Interest[];
//   // другие поля
// }

// interface SearchState {
//   users: User[];
//   currentIndex: number;
//   isLoading: boolean;
// }

// const initialState: SearchState = {
//   users: [],
//   currentIndex: 0,
//   isLoading: true, // Начальное состояние загрузки
// };

// const searchSlice = createSlice({
//   name: 'search',
//   initialState,
//   reducers: {
//     setUsers(state, action: PayloadAction<User[]>) {
//       state.users = action.payload;
//       state.isLoading = false; // Данные загружены
//     },
//     setLoading(state, action: PayloadAction<boolean>) {
//       state.isLoading = action.payload;
//     },
//     setNextUser(state) {
//       state.currentIndex = (state.currentIndex + 1) % state.users.length;
//     },
//     sendLikeAction(state, action: PayloadAction<number>) {
//       const userId = state.users[state.currentIndex].id;
//       sendLike(userId, action.payload); // Тип лайка передаём через payload
//     },
//   },
// });

// export const { setUsers, setLoading, setNextUser, sendLikeAction } = searchSlice.actions;

// export const fetchUsersData = () => async (dispatch: any) => {
//   try {
//     dispatch(setLoading(true)); // Включаем загрузку

//     // Здесь можно получить пользователей с сервера
//     const usersData = await fetchUsers();
//     dispatch(setUsers(usersData));
//   } catch (error) {
//     console.error('Ошибка при получении пользователей:', error);
//   }
// };

// export const fetchUserInterests = (userId: number) => async (dispatch: any) => {
//   try {
//     const interests = await fetchInterests(userId);
//     dispatch(setUsersWithInterests({ userId, interests }));
//   } catch (error) {
//     console.error('Ошибка при получении интересов:', error);
//   }
// };

// export default searchSlice.reducer;
