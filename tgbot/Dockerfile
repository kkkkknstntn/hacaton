FROM python:3.10-slim

# Устанавливаем системные зависимости
RUN apt-get update && apt-get install -y \
    build-essential \
    libssl-dev \
    libsasl2-dev \
    libz-dev \
    python3-dev \
    gcc \
    && rm -rf /var/lib/apt/lists/*

# Установим рабочую директорию внутри контейнера
WORKDIR /app

# Скопируем requirements.txt в контейнер
COPY requirements.txt /app/

# Обновим pip и установим зависимости
RUN pip install --upgrade pip
RUN pip install --no-cache-dir -r requirements.txt

# Скопируем все файлы из текущей директории в контейнер
COPY . /app/

# Укажем команду для запуска бота (замените на название вашего скрипта)
CMD ["python", "bot.py"]
