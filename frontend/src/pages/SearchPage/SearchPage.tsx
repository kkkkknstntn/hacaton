// import React, { useEffect } from "react";
// import { Box, CircularProgress } from "@mui/material";
// import { useDispatch, useSelector } from "react-redux";
// import { RootState } from "../../store";
// import { fetchUsers } from "../../store/searchSlice"; // действия для установки данных в Redux
// import styles from "./SearchPage.module.scss";
// import PhotoCarouselContainer from "../../containers/PhotoCarouselContainer/PhotoCarouselContainer";
// import SearchActionsContainer from "../../containers/SearchActionsContainer/SearchActionsContainer";
// import SearchInterestsContainer from "../../containers/SearchInterestsContainer/SearchInterestsContainer";
// import SearchAboutMeContainer from "../../containers/SearchAboutMeContainer/SearchAboutMeContainer";

// const SearchPage: React.FC = () => {
//   const dispatch = useDispatch();
//   const { users, isLoading } = useSelector((state: RootState) => state.search);

//   useEffect(() => {
//     dispatch(fetchUsers());
//   }, [dispatch]);

//   if (isLoading) {
//     return (
//       <Box className={styles.loadingContainer}>
//         <CircularProgress />
//       </Box>
//     );
//   }

//   return (
//     <Box className={styles.container}>
//       <Box className={styles.leftColumn}>
//         {users.length > 0 && <PhotoCarouselContainer/>}
//         <SearchActionsContainer />
//       </Box>
//       <Box className={styles.rightColumn}>
//         {users.length > 0 && <SearchInterestsContainer />}
//         {users.length > 0 && <SearchAboutMeContainer />}
//       </Box>
//     </Box>
//   );
// };

// export default SearchPage;
