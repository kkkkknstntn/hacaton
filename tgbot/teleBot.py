import asyncio
import json
import logging
from aiokafka import AIOKafkaConsumer
from os import getenv
from aiogram import Bot, Dispatcher, html, Router, F
from aiogram.client.default import DefaultBotProperties
from aiogram.enums import ParseMode
from aiogram.filters import CommandStart, Command
from aiogram.types import Message, KeyboardButton, ReplyKeyboardMarkup, InlineKeyboardMarkup, InlineKeyboardButton
from apscheduler.schedulers.asyncio import AsyncIOScheduler

KAFKA_BROKER = 'kafka:9092'
INPUT_TOPIC = 'notification-tg-bot'
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)
consumer_conf = {
    'bootstrap.servers': KAFKA_BROKER,
    'group.id': 'notification-tg-bot',
    'auto.offset.reset': 'earliest'
}
TOKEN = "8139260626:AAEFB1ZobUmmNEX3TB3o_67cKpat6d1PXv0"
BASE_URL = getenv('BASE_URL')
dp = Dispatcher()
scheduler = AsyncIOScheduler(timezone='Europe/Moscow')
start_router = Router()
bot = Bot(token=TOKEN, default=DefaultBotProperties(parse_mode=ParseMode.HTML))


def kb_notifications_on():
    kb = [[KeyboardButton(text="Включить уведомления")]]
    kb = ReplyKeyboardMarkup(
        keyboard=kb,
        resize_keyboard=True,
        one_time_keyboard=True
    )
    return kb


def main_kb():
    kb_list = [
        [KeyboardButton(text="О боте")],
        [KeyboardButton(text="Перейти на сайт")],
        [KeyboardButton(text="Выключить уведомления")]
    ]
    keyboard = ReplyKeyboardMarkup(
        keyboard=kb_list,
        resize_keyboard=True,
        input_field_placeholder="Воспользуйтесь меню:"
    )
    return keyboard


def site_link_kb():
    kb = [
        [InlineKeyboardButton(text="Наш сайт", url="https://www.wildberries.ru/")]
    ]
    return InlineKeyboardMarkup(inline_keyboard=kb)

@start_router.message(CommandStart())
async def command_start_handler(message: Message):
    await message.answer(f'Привет, {message.from_user.full_name}\n'
                         f'Этот бот будет присылать тебе уведомления о новых '
                         f'лайках и мэтчах с сайта [имя сайта]\n'
                         f'Если хочешь начать получать уведомления прямо сейчас, нажми на кнопку ниже',
                         reply_markup=kb_notifications_on())


@start_router.message(F.text == "Перейти на сайт")
async def get_inl_between_link(message: Message):
    await message.answer("Скорей переходи на сайт!", reply_markup=site_link_kb())


@start_router.message(F.text == "О боте")
async def command_info(message: Message):
    await  message.answer(f'Это бот Бабангида\n'
                          f'Он будет уведомлять тебя о новых мэтчах и лайках с сайта [имя сайта]')


@start_router.message(F.text == "Выключить уведомления")
async def notifications_off(message: Message):
    asyncio.create_task(consume_kaf_messages())
    await message.answer(f'Уведомления успешно выключены', reply_markup=kb_notifications_on())


@start_router.message(F.text == "Включить уведомления")
async def notifications_on(message: Message):
    asyncio.create_task(consume_kaf_messages())
    await message.answer(f'Уведомления успешно включены', reply_markup=main_kb())


async def kaf_stop():
    consumer = AIOKafkaConsumer(
        INPUT_TOPIC,
        bootstrap_servers=KAFKA_BROKER,
        value_deserializer=lambda m: json.loads(m.decode('utf-8'))
    )
    await consumer.stop()


async def consume_kaf_messages():
    consumer = AIOKafkaConsumer(
        INPUT_TOPIC,
        bootstrap_servers=KAFKA_BROKER,
        value_deserializer=lambda m: json.loads(m.decode('utf-8'))
    )
    await consumer.start()
    try:
        async for msg in consumer:
            massage_data = msg.value
            await bot.send_message(chat_id="8139260626", text="бабангида")
    except:
        await consumer.stop()
        await bot.send_message(chat_id="8139260626", text="бабанОшибка")


async def main():
    dp.include_router(start_router)
    await dp.start_polling(bot)


if __name__ == "__main__":
    asyncio.run(main())
