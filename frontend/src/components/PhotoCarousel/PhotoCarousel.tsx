// // src/components/PhotoCarousel/PhotoCarousel.tsx
// import React, { useState, useEffect } from "react";
// import { Box, Typography, IconButton } from "@mui/material";
// import { User } from "../../store/searchSlice";
// import styles from "./PhotoCarousel.module.scss";
// import ArrowBackIosNewIcon from "@mui/icons-material/ArrowBackIosNew";
// import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos";
// import LocationOnIcon from "@mui/icons-material/LocationOn";
// import GroupsIcon from "@mui/icons-material/Groups";
// import { fetchWithTokenPhoto } from "../../services/fetchService";

// interface PhotoCarouselProps {
//   user: User;
// }

// const PhotoCarousel: React.FC<PhotoCarouselProps> = ({ user }) => {
//   const [currentPhoto, setCurrentPhoto] = useState(0);
//   const [photoSrc, setPhotoSrc] = useState<string | null>(null);

//   useEffect(() => {
//     const loadPhoto = async () => {
//       const photoId = user.photos[currentPhoto];
//       const photoName = photoId.split("/").pop();
//       const photoUrl = `/api/users/${user.id}/photo/${photoName}`;

//       try {
//         const response = await fetchWithTokenPhoto(photoUrl, {
//           method: "GET",
//           credentials: "include",
//         });
//         const url = URL.createObjectURL(response);
//         setPhotoSrc(url);
//       } catch (error) {
//         console.error("Failed to load photo:", error);
//       }
//     };

//     if (user.photos.length > 0) {
//       loadPhoto();
//     }
//   }, [currentPhoto, user.id, user.photos]);

//   const nextPhoto = () => {
//     setCurrentPhoto((currentPhoto + 1) % user.photos.length);
//   };

//   const prevPhoto = () => {
//     setCurrentPhoto((currentPhoto - 1 + user.photos.length) % user.photos.length);
//   };

//   return (
//     <Box className={styles.carousel}>
//       {photoSrc && (
//         <img src={photoSrc} alt="User photo" className={styles.photo} />
//       )}
//       <Box className={styles.overlay}>
//         <Box className={styles.buttonsContainer}>
//           <IconButton onClick={prevPhoto} className={styles.navButton}>
//             <ArrowBackIosNewIcon />
//           </IconButton>
//           <IconButton onClick={nextPhoto} className={styles.navButton}>
//             <ArrowForwardIosIcon />
//           </IconButton>
//         </Box>
//         <Typography variant="h5" className={styles.name}>
//           {user.firstName} {user.age}
//         </Typography>
//         <Box className={styles.infoRow}>
//           <LocationOnIcon className={styles.icon} />
//           <Typography variant="body2">{user.city}</Typography>
//         </Box>
//         <Box className={styles.infoRow}>
//           <GroupsIcon className={styles.icon} />
//           <Typography variant="body2">{user.education}</Typography>
//         </Box>
//       </Box>
//     </Box>
//   );
// };

// export default PhotoCarousel;
