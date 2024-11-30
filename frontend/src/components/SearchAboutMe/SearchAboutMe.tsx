// // src/components/SearchAboutMe/SearchAboutMe.tsx
// import React from "react";
// import { Box, Typography } from "@mui/material";
// import EmojiEmotionsIcon from "@mui/icons-material/EmojiEmotions";
// import { useSelector } from "react-redux";
// import { RootState } from "../../store";
// import styles from "./SearchAboutMe.module.scss";

// const SearchAboutMe: React.FC = () => {
//   const user = useSelector((state: RootState) => state.search.users[0]); // Извлекаем первого пользователя
//   return (
//     <Box className={styles.container}>
//       <Box className={styles.header}>
//         <EmojiEmotionsIcon className={styles.icon} />
//         <Typography variant="h6" className={styles.title}>
//           О себе
//         </Typography>
//       </Box>
//       <Typography className={styles.text}>{user?.aboutMe}</Typography>
//     </Box>
//   );
// };

// export default SearchAboutMe;
