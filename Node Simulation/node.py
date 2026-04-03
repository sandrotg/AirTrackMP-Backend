import random
import time
import requests

#URL = "http://localhost"
pm10 = 10.0
pm25 = 12.0
temperature = 30.0
humidity = 65.0

def IoTNode(value, min_value, max_value, max_change=1.5):
    change = random.uniform(-max_change, max_change)
    new_value = value + change
    return max(min(new_value, max_value), min_value)

while True:
    pm10 = IoTNode(pm10, 0, 500)
    pm25 = IoTNode(pm25, 0, 500)
    temperature = IoTNode(temperature, -20, 50)
    humidity = IoTNode(humidity, 0, 100)

    data = {
        "pm10": pm10,
        "pm25": pm25,
        "temperature": temperature,
        "humidity": humidity
    }
    try:
        response = requests.post(URL, json=data)
        print(f"Sent: {data} | Status: {response.status_code}")
    except Exception as e:
        print("Error sending data:", e)
    time.sleep(10)
    
