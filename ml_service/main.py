from fastapi import FastAPI, File, UploadFile, Form
from typing import Optional
import pandas as pd
import random
import os

app = FastAPI(title="Krushi-Adhaar Disease Prediction Service")

# Load the dataset
DATASET_PATH = r"c:\Users\sj130\Downloads\Crop Management\krushi-adhhar\dataset\Krushi_Aadhar_Disease_Info.csv"

# Load data into a DataFrame
if os.path.exists(DATASET_PATH):
    df = pd.read_csv(DATASET_PATH)
else:
    df = pd.DataFrame()

@app.post("/predict")
async def predict_disease(
    file: UploadFile = File(...),
    crop_name: Optional[str] = Form(None)
):
    if df.empty:
        return {"error": "Dataset not found. Cannot make prediction."}
    
    # Filter dataset based on crop name if provided
    if crop_name:
        # Case insensitive match
        filtered_df = df[df['Crop'].str.lower() == crop_name.lower()]
        if filtered_df.empty:
            # Fallback to entire dataset if crop is not found
            filtered_df = df
    else:
        filtered_df = df
        
    # Simulate ML prediction by picking a random row from the available classes
    # Pretend we processed the file
    predicted_row = filtered_df.sample(n=1).iloc[0]
    
    # Generate a random confidence score between 0.70 and 0.99
    confidence_score = round(random.uniform(0.70, 0.99), 4)
    
    # Fill NaN values with empty strings
    predicted_row = predicted_row.fillna("")
    
    # Generate mock data for medicines (at least 2) and estimated cost
    mock_medicines = [
        "Fungicide A and Neem Oil",
        "Pesticide B and Organic Spray",
        "Copper Sulfate and Antibiotic C",
        "Herbicide D and Nutrient Mix"
    ]
    recommended_medicines = random.choice(mock_medicines)
    estimated_cost = round(random.uniform(500, 2500), 2)
    
    return {
        "Disease_ID": str(predicted_row.get("Disease_ID", "")),
        "Class_Name": str(predicted_row.get("Class_Name", "")),
        "Disease_Name": str(predicted_row.get("Disease_Name", "")),
        "Symptoms": str(predicted_row.get("Symptoms", "")),
        "Management": str(predicted_row.get("Management", "")),
        "Treatment": str(predicted_row.get("Treatment", "")),
        "Prevention": str(predicted_row.get("Prevention", "")),
        "Confidence_Score": confidence_score,
        "Medicines": recommended_medicines,
        "Estimated_Cost": estimated_cost
    }
