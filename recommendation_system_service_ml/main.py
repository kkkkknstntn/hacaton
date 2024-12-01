import asyncio
from confluent_kafka import Consumer, Producer
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.orm import sessionmaker
from app.db.database import get_user_filters, find_matching_users
from geopy.distance import geodesic  # For calculating distances
import json
import os
from dotenv import load_dotenv
load_dotenv()

DATABASE_URL = os.getenv("DATABASE_URL")
KAFKA_BROKER = os.getenv("KAFKA_BROKER")
START_TOPIC = os.getenv("START_TOPIC")
RESPONSE_TOPIC = os.getenv("RESPONSE_TOPIC")


engine = create_async_engine(DATABASE_URL, echo=True)
async_session = sessionmaker(engine, class_=AsyncSession, expire_on_commit=False)

producer = Producer({'bootstrap.servers': KAFKA_BROKER})