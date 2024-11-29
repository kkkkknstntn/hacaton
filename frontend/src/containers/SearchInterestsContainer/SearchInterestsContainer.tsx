// src/components/SearchInterests/SearchInterestsContainer.tsx
import React from "react";
import { useSelector } from "react-redux";
import { RootState } from "../../store";
import styles from "./SearchInterestsContainer.module.scss";
import SearchInterests from "../../components/SearchInterests/SearchInterests";
import interestsData from "../../data/interests.json"; // Подключаем JSON с данными интересов
import { Interest } from "../../store/userSlice";

const SearchInterestsContainer: React.FC = () => {
  const { users, currentIndex } = useSelector(
    (state: RootState) => state.search
  );

  // Получаем интересы пользователя по их id
  const userInterestIds = users[currentIndex]?.interests || [];
  const userInterests = userInterestIds
    .map((id) =>
      Object.values(interestsData.categories)
        .flatMap((category) => category.items)
        .find((interest) => interest.id === id)
    )
    .filter(Boolean) as Interest[]; // Приведение типа после фильтрации

  return (
    <div className={styles.interestsContainer}>
      <SearchInterests interests={userInterests} />
    </div>
  );
};

export default SearchInterestsContainer;
