import os

import joblib
import pandas as pd
from flask import Flask, jsonify, request

app = Flask(__name__)

model_pm25 = joblib.load("model_pm25.pkl")
model_pm10 = joblib.load("model_pm10.pkl")

dataset = None
if os.path.exists("dataset.pkl"):
    dataset = joblib.load("dataset.pkl")


def build_features(data):
    timestamp = pd.to_datetime(data["timestamp"])

    if "pm25" in data:
        pm25 = float(data["pm25"])
        pm10 = float(data["pm10"])
        return [[
            timestamp.hour,
            timestamp.day,
            timestamp.month,
            timestamp.dayofweek,
            float(data["temperature"]),
            float(data["humidity"]),
            pm25,
            pm10,
            float(data.get("pm25_prev1", pm25)),
            float(data.get("pm25_prev2", pm25)),
            float(data.get("pm10_prev1", pm10)),
            float(data.get("pm10_prev2", pm10)),
        ]]

    if dataset is None:
        raise ValueError("Live measurement fields are required when dataset.pkl is unavailable")

    row = dataset[dataset["timestamp"] == timestamp]
    if row.empty:
        raise ValueError("No existe esa fecha en el dataset")

    return [[
        row["hour"].values[0],
        row["day"].values[0],
        row["month"].values[0],
        row["day_of_week"].values[0],
        row["temperature"].values[0],
        row["humidity"].values[0],
        row["pm2_5"].values[0],
        row["pm10"].values[0],
        row["pm25_prev1"].values[0],
        row["pm25_prev2"].values[0],
        row["pm10_prev1"].values[0],
        row["pm10_prev2"].values[0],
    ]]


@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok"})


@app.route("/predict", methods=["POST"])
def predict():
    data = request.get_json(silent=True)
    if not data or "timestamp" not in data:
        return jsonify({"error": "timestamp is required"}), 400

    try:
        features = build_features(data)
    except ValueError as error:
        return jsonify({"error": str(error)}), 400

    future_pm25 = float(model_pm25.predict(features)[0])
    future_pm10 = float(model_pm10.predict(features)[0])

    return jsonify({
        "timestamp": str(pd.to_datetime(data["timestamp"])),
        "future_pm25": future_pm25,
        "future_pm10": future_pm10,
    })


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
