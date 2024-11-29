import React from "react";
import { Box, IconButton } from "@mui/material";
import FavoriteIcon from "@mui/icons-material/Favorite";
import CloseIcon from "@mui/icons-material/Close";
import styles from "./SearchActions.module.scss";
import { LikeType } from "../../services/likeService";

interface SearchActionsProps {
  onLike: (type: LikeType) => Promise<void>; 
  onDislike: () => void; 
  onHiddenLike: () => void; 
  likeType: LikeType; 
  setLikeType: React.Dispatch<React.SetStateAction<LikeType>>;
}

const SearchActions: React.FC<SearchActionsProps> = ({
  onLike,
  onDislike,
  onHiddenLike,
  likeType,
  setLikeType,
}) => (
  <Box className={styles.actions}>
    <IconButton
      onClick={onDislike}
      className={styles.button}
      aria-label="Dislike"
    >
      <CloseIcon className={styles.dislikeIcon} />
    </IconButton>

    <IconButton
      onClick={() => {
        setLikeType(1);
        onLike(1); 
      }}
      className={`${styles.button} ${likeType === 1 ? styles.active : ""}`}
      aria-label="Like"
    >
      <FavoriteIcon className={styles.likeIcon} />
    </IconButton>

    <IconButton
      onClick={() => {
        setLikeType(2); 
        onHiddenLike(); 
      }}
      className={`${styles.button} ${likeType === 2 ? styles.active : ""}`}
      aria-label="Hidden Like"
    >
      <FavoriteIcon className={`${styles.likeIcon} ${styles.hiddenLikeIcon}`} />
    </IconButton>
  </Box>
);

export default SearchActions;
