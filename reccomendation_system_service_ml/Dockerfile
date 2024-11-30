# Используем официальный образ Python
FROM python:3.9-slim

# Устанавливаем дополнительные зависимости для работы с Russian models
RUN apt-get update && apt-get install -y \
    libicu-dev \
    libpq-dev \
    gcc \
    netcat-openbsd \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Копируем файл зависимостей
COPY requirements.txt ./requirements.txt

COPY interest_embeddings.json ./interest_embeddings.json

# Устанавливаем Python-зависимости с кэшированием pip
RUN --mount=type=cache,target=/root/.cache/pip pip install --no-cache-dir -r requirements.txt

# Копируем исходный код приложения
COPY ./app ./app

# Скачиваем модели Stanza и Sentence-Transformers в кэш
# Кешируем модель Stanza с помощью mount
RUN --mount=type=cache,target=/root/.cache/stanza \
    python -c "import stanza; stanza.download('ru')"

RUN --mount=type=cache,target=/root/.cache/huggingface \
    python -c "from sentence_transformers import SentenceTransformer; SentenceTransformer('paraphrase-multilingual-MiniLM-L12-v2')"

# Устанавливаем переменную окружения для предотвращения буферизации вывода
ENV PYTHONUNBUFFERED=1

# Команда запуска
CMD ["python", "app/app.py"]