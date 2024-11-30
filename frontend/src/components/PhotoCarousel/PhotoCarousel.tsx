import React, { useState, useEffect } from "react";
import { Box, Typography, IconButton } from "@mui/material";
import { User } from "../../store/searchSlice";
import styles from "./PhotoCarousel.module.scss";
import ArrowBackIosNewIcon from "@mui/icons-material/ArrowBackIosNew";
import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos";
import LocationOnIcon from "@mui/icons-material/LocationOn";
import GroupsIcon from "@mui/icons-material/Groups";
import { fetchWithTokenPhoto } from "../../services/fetchService";

interface PhotoCarouselProps {
  user: User;
}

const PhotoCarousel: React.FC<PhotoCarouselProps> = ({ user }) => {
  const [currentPhoto, setCurrentPhoto] = useState(0);
  const [photoUrls, setPhotoUrls] = useState<string[]>([]); // Массив для хранения всех URL-ов фото

  // Загружаем все фото при монтировании компонента
  useEffect(() => {
    const loadAllPhotos = async () => {
      const urls: string[] = [];
      for (const photoId of user.photos) {
        const photoName = photoId.split("/").pop(); // Получаем имя файла
        const photoUrl = `/api/users/${user.id}/photo/${photoName}`;
        try {
          const response = await fetchWithTokenPhoto(photoUrl, {
            method: "GET",
            credentials: "include",
          });
          const url = URL.createObjectURL(response);
          urls.push(url); // Добавляем URL в массив
        } catch (error) {
          console.error("Failed to load photo:", error);
        }
      }
      setPhotoUrls(urls); // Сохраняем все URL-ы фото в состояние
    };

    if (user.photos.length > 0) {
      loadAllPhotos();
    }
  }, [user.id, user.photos]);

  // Функции для переключения фото
  const nextPhoto = () => {
    setCurrentPhoto((prev) => (prev + 1) % photoUrls.length);
  };

  const prevPhoto = () => {
    setCurrentPhoto((prev) => (prev - 1 + photoUrls.length) % photoUrls.length);
  };

  return (
    <Box className={styles.carousel}>
      {/* Если изображения загружены, отображаем их */}
      {photoUrls.length > 0 && (
        <img
          src={photoUrls[currentPhoto]}
          alt="User photo"
          className={styles.photo}
        />
      )}

      <Box className={styles.overlay}>
        <Box className={styles.buttonsContainer}>
          <IconButton onClick={prevPhoto} className={styles.navButton}>
            <ArrowBackIosNewIcon />
          </IconButton>
          <IconButton onClick={nextPhoto} className={styles.navButton}>
            <ArrowForwardIosIcon />
          </IconButton>
        </Box>
        <Typography variant="h5" className={styles.name}>
          {user.firstName} {user.age}
        </Typography>
        <Box className={styles.infoRow}>
          <LocationOnIcon className={styles.icon} />
          <Typography variant="body2">{user.city}</Typography>
        </Box>
        <Box className={styles.infoRow}>
          <GroupsIcon className={styles.icon} />
          <Typography variant="body2">{user.education}</Typography>
        </Box>
      </Box>
    </Box>
  );
};

export default PhotoCarousel;
