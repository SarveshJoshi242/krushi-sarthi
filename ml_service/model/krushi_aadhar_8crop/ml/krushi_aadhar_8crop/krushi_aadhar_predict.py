from pathlib import Path
import json
import re

import numpy as np
import pandas as pd
import tensorflow as tf
from PIL import Image as PILImage

# Resolve paths from this file so the module works after cloning the repository.
PACKAGE_DIR = Path(__file__).resolve().parent
REPO_ROOT = PACKAGE_DIR.parents[1]

MODEL_PATH = PACKAGE_DIR / "Krushi_Aadhar_8Crop_Final_Submission.keras"
INDICES_PATH = PACKAGE_DIR / "class_indices.json"
DISEASE_INFO_PATH = REPO_ROOT / "dataset" / "Krushi_Aadhar_Disease_Info.csv"


def normalize(value):
    value = str(value).strip().lower().replace("_", " ")
    return re.sub(r"\s+", " ", value)


with open(INDICES_PATH, "r", encoding="utf-8") as f:
    class_indices = json.load(f)

label_by_index = {int(index): label for label, index in class_indices.items()}

disease_df = pd.read_csv(DISEASE_INFO_PATH)
disease_lookup = {}

for _, row in disease_df.iterrows():
    key = (normalize(row["Crop"]), normalize(row["Class_Name"]))
    if key in disease_lookup:
        raise ValueError(f"Duplicate disease mapping: {key}")
    disease_lookup[key] = row.to_dict()

model = tf.keras.models.load_model(MODEL_PATH, compile=False)
GPU_AVAILABLE = bool(tf.config.list_physical_devices("GPU"))


def krushi_aadhar_predict(image_path, top_k=3):
    image_path = Path(image_path)
    if not image_path.is_file():
        raise FileNotFoundError(f"Image not found: {image_path}")

    with PILImage.open(image_path) as img:
        image_array = np.asarray(
            img.convert("RGB").resize((224, 224)),
            dtype=np.float32,
        )

    image_array = np.expand_dims(image_array, axis=0)
    device = "/GPU:0" if GPU_AVAILABLE else "/CPU:0"

    with tf.device(device):
        probabilities = model.predict(image_array, verbose=0)[0]

    if len(probabilities) != len(class_indices):
        raise ValueError("Model output and class mapping do not match.")

    top_k = max(1, min(int(top_k), len(probabilities)))
    top_indices = np.argsort(probabilities)[::-1][:top_k]
    results = []

    for rank, index in enumerate(top_indices, start=1):
        label = label_by_index[int(index)]
        crop, class_name = label.split("/", 1)
        record = disease_lookup.get((normalize(crop), normalize(class_name)))

        if record is None:
            raise KeyError(f"No disease information found for: {label}")

        results.append({
            "Rank": rank,
            "Label": label,
            "Disease_ID": record["Disease_ID"],
            "Crop": record["Crop"],
            "Disease_Name": record["Disease_Name"],
            "Confidence": round(float(probabilities[index]) * 100, 2),
            "Symptoms": record["Symptoms"],
            "Management": record["Management"],
            "Treatment": record["Treatment"],
            "Prevention": record["Prevention"],
            "Severity": record["Severity"],
            "Source": record["Source"],
        })

    return pd.DataFrame(results)
