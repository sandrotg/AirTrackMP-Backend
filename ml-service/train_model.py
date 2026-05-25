import pandas as pd
import joblib
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import (mean_absolute_error, mean_absolute_percentage_error)

# CARGAR DATASET

df = pd.read_csv("pakistan_air_quality_final_clean.csv")

# TIMESTAMP

df['timestamp'] = pd.to_datetime(df['timestamp'])

df = df.sort_values('timestamp')

# VARIABLES TEMPORALES

df['hour'] = df['timestamp'].dt.hour
df['day'] = df['timestamp'].dt.day
df['month'] = df['timestamp'].dt.month
df['day_of_week'] = df['timestamp'].dt.dayofweek

# HISTORIAL PM2.5

df['pm25_prev1'] = df['pm2_5'].shift(1)
df['pm25_prev2'] = df['pm2_5'].shift(2)

# HISTORIAL PM10

df['pm10_prev1'] = df['pm10'].shift(1)
df['pm10_prev2'] = df['pm10'].shift(2)

# OBJETIVOS FUTUROS

df['future_pm25'] = df['pm2_5'].shift(-1)
df['future_pm10'] = df['pm10'].shift(-1)

# LIMPIAR

df = df.dropna()

# FEATURES

X = df[[
    'hour',
    'day',
    'month',
    'day_of_week',
    'temperature',
    'humidity',
    'pm2_5',
    'pm10',
    'pm25_prev1',
    'pm25_prev2',
    'pm10_prev1',
    'pm10_prev2'
]]

# TARGETS

y_pm25 = df['future_pm25']
y_pm10 = df['future_pm10']

# SPLIT

X_train, X_test, y25_train, y25_test = train_test_split(
    X,
    y_pm25,
    test_size=0.2,
    random_state=42
)

_, _, y10_train, y10_test = train_test_split(
    X,
    y_pm10,
    test_size=0.2,
    random_state=42
)

# MODELOS

model_pm25 = RandomForestRegressor(
    n_estimators=100,
    random_state=42
)

model_pm10 = RandomForestRegressor(
    n_estimators=100,
    random_state=42
)

# ENTRENAMIENTO

model_pm25.fit(X_train, y25_train)
model_pm10.fit(X_train, y10_train)

# PREDICCIONES

pred25 = model_pm25.predict(X_test)
pred10 = model_pm10.predict(X_test)

# EVITAR CERO

y25_test = y25_test.replace(0, 0.0001)
y10_test = y10_test.replace(0, 0.0001)

# MÉTRICAS

mae25 = mean_absolute_error(y25_test, pred25)
mae10 = mean_absolute_error(y10_test, pred10)

mape25 = mean_absolute_percentage_error(y25_test, pred25) * 100
mape10 = mean_absolute_percentage_error(y10_test, pred10) * 100

print("\n========= RESULTADOS =========")

print(f"\nMAE PM2.5: {mae25:.2f}")
print(f"MAE PM10: {mae10:.2f}")

print(f"\nError porcentual PM2.5: {mape25:.2f}%")
print(f"Error porcentual PM10: {mape10:.2f}%")

# GUARDAR MODELOS

joblib.dump(model_pm25, "model_pm25.pkl")
joblib.dump(model_pm10, "model_pm10.pkl")

# Guardar dataset procesado
joblib.dump(df, "dataset.pkl")

print("\nModelos guardados correctamente")