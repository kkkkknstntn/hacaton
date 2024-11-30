# file: app.py
import asyncio
from dataclasses import dataclass
from typing import List
from confluent_kafka import Consumer, Producer
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.orm import sessionmaker
from database import get_user_filters, find_matching_users, get_info_current_user
from geopy.distance import geodesic  # For calculating distances
from app.calculate_rec import compare_user_with_group
import json
DATABASE_URL = "postgresql+asyncpg://postgres:postgres@users_db:5432/users_db"
KAFKA_BROKER = "kafka:9092"
START_TOPIC = "start-calculate-recommendation"
RESPONSE_TOPIC = "recommendation-response"

engine = create_async_engine(DATABASE_URL, echo=True)
async_session = sessionmaker(engine, class_=AsyncSession, expire_on_commit=False)

@dataclass
class User:
    user_id: int
    about_me: str
    selected_interests: List[int]
    photos: List[str]
    gender: str
    city: str
    job: str
    education: str
    age: int

producer = Producer({'bootstrap.servers': KAFKA_BROKER})

async def handle_message(message):
    try:
        user_id, status = message.get("user_id"), message.get("status")
        if status != "start":
            return
        
        async with async_session() as session:
            filters = await get_user_filters(session, user_id)
            if not filters:
                print(f"Filters not found for user_id {user_id}")
                return

            users = await find_matching_users(session, filters)
        
        interests_list_current_user = get_info_current_user(user_id["user_id"])[1] # тянем инфу о текущем юзере
        about_me_current_user = get_info_current_user(user_id["user_id"])[0]
        
        current_user = User( # делаем объект User 
            user_id=user_id,
            about_me=about_me_current_user,
            selected_interests=interests_list_current_user,
            photos=[],
            gender="",  
            city="",
            job="",
            education="",
            age=0
        )

        matching_users_with_scores = compare_user_with_group(current_user, users) # сравниваем его с остальными (возвращает List[User])
        users_list = [] # делаем назад списком словарей))))
        for user in matching_users_with_scores:
            user_data = {
                "user_id": user.user_id,
                "about_me": user.about_me,
                "selected_interests": user.selected_interests, 
                "photos": user.photos,  
                "gender": user.gender,
                "city": user.city,
                "job": user.job,
                "education": user.education,
                "age": user.age,
                }
            users_list.append(user_data)
        
        response = {
            "user_id": user_id,
            "users_list": users_list # отдаем
        }
        
        producer.produce(RESPONSE_TOPIC, value=json.dumps(response).encode('utf-8'))
        producer.flush()
    except Exception as e:
        print(f"Error handling message: {e}")


async def consume():
    consumer = Consumer({
        'bootstrap.servers': KAFKA_BROKER,
        'group.id': 'recommendation-service',
        'auto.offset.reset': 'earliest',
    })

    consumer.subscribe([START_TOPIC])

    while True:
        msg = consumer.poll(1.0)
        if msg is None:
            continue
        if msg.error():
            print(f"Consumer error: {msg.error()}")
            continue

        message = eval(msg.value().decode('utf-8'))
        await handle_message(message)


if __name__ == "__main__":
    loop = asyncio.get_event_loop()
    loop.run_until_complete(consume())
