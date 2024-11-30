import React, { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { nextUser } from "../../store/searchSlice";

import styles from "./SearchActionsContainer.module.scss";
import SearchActions from "../../components/SearchActions/SearchActions";
import { LikeType, sendLike } from "../../services/likeService";
import { RootState } from "../../store";

const SearchActionsContainer: React.FC = () => {
  const dispatch = useDispatch();
  const { currentIndex, users } = useSelector(
    (state: RootState) => state.search
  );

  const [likeType, setLikeType] = useState<LikeType>(1);

  const handleLike = async (type: LikeType) => {
    const targetId = users[currentIndex]?.id;
    if (targetId) {
      try {
        await sendLike(targetId, type);
        dispatch(nextUser());
      } catch (error) {
        console.error("Failed to send like:", error);
      }
    }
  };

  const handleDislike = async () => {
    const targetId = users[currentIndex]?.id;
    if (targetId) {
      try {
        await sendLike(targetId, 3);
        dispatch(nextUser());
      } catch (error) {
        console.error("Failed to send dislike:", error);
      }
    }
  };

  const handleHiddenLike = async () => {
    const targetId = users[currentIndex]?.id;
    if (targetId) {
      try {
        await sendLike(targetId, 2);
        dispatch(nextUser());
      } catch (error) {
        console.error("Failed to send hidden like:", error);
      }
    }
  };

  return (
    <div className={styles.actionsContainer}>
      <SearchActions
        onLike={handleLike}
        onDislike={handleDislike}
        onHiddenLike={handleHiddenLike}
        likeType={likeType}
        setLikeType={setLikeType}
      />
    </div>
  );
};

export default SearchActionsContainer;
