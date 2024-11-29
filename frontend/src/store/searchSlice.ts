// src/store/searchSlice.ts
import { createSlice, PayloadAction } from "@reduxjs/toolkit";

// Теперь Interest будет представлять только id интересов
export interface Interest {
  id: number;
}

export interface User {
  name: string;
  age: number;
  city: string;
  education: string;
  photos: string[];
  interests: number[]; // Храним только id интересов
  aboutMe: string;
}

interface SearchState {
  users: User[];
  currentIndex: number;
}

const initialState: SearchState = {
  users: [],
  currentIndex: 0,
};

const searchSlice = createSlice({
  name: "search",
  initialState,
  reducers: {
    setUsers(state, action: PayloadAction<User[]>) {
      state.users = action.payload;
    },
    nextUser(state) {
      state.currentIndex = (state.currentIndex + 1) % state.users.length;
    },
    prevUser(state) {
      state.currentIndex =
        (state.currentIndex - 1 + state.users.length) % state.users.length;
    },
  },
});

export const { setUsers, nextUser, prevUser } = searchSlice.actions;
export default searchSlice.reducer;
