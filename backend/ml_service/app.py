from flask import Flask, request, jsonify
import pandas as pd
import random
import os
import time

app = Flask(__name__)

# Load metadata
dataset_dir = os.path.join(os.path.dirname(__file__), 'dataset')
disease_info_path = os.path.join(dataset_dir, 'Krushi_Aadhar_Disease_Info.csv')

if os.path.exists(disease_info_path):
    disease_df = pd.read_csv(disease_info_path)
else:
    disease_df = pd.DataFrame()

@app.route('/predict', methods=['POST'])
def predict():
    # In a real app, this would run an ML model on request.files['image']
    # and maybe take questionnaire answers into account.
    # Here we mock the prediction by picking a random disease from the CSV.
    time.sleep(2) # simulate processing time
    
    if disease_df.empty:
        return jsonify({
            "success": False,
            "error": "Dataset not loaded."
        }), 500
    
    # Pick a random disease
    sample = disease_df.sample(1).iloc[0]
    
    # Map the severity based on some logic or just assign one
    severity = sample.get('Severity', 'High')
    if "Reference guidance" in str(severity):
        severity = random.choice(["High", "Medium", "Low"])
        
    return jsonify({
        "success": True,
        "data": {
            "disease_name": sample['Disease_Name'],
            "scientific_name": sample['Scientific_Name'],
            "crop": sample['Crop'],
            "symptoms": sample['Symptoms'],
            "management": sample['Management'],
            "treatment": sample['Treatment'],
            "prevention": sample['Prevention'],
            "severity": severity,
            "confidence": round(random.uniform(75.0, 98.0), 1)
        }
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
