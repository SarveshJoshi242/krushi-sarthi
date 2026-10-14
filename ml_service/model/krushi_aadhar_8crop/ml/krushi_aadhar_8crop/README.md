# Krushi Aadhar — 8-Crop Disease Classification Model

## Overview

This directory contains the trained Krushi Aadhar deep-learning model
for classifying plant diseases across 8 crops and 39 classes.

## Supported crops

- Brinjal
- Cotton
- Groundnut
- Maize
- Potato
- Rice
- Sugarcane
- Tomato

## Files

- `Krushi_Aadhar_8Crop_Final_Submission.keras` — trained Keras model.
- `krushi_aadhar_predict.py` — prediction function with disease-information lookup.
- `class_labels.csv` — class label reference.
- `class_indices.json` — mapping between model output indices and labels.

Disease symptoms, management, treatment, prevention, and severity information
are loaded from `dataset/Krushi_Aadhar_Disease_Info.csv` in the repository root.

## Requirements

Use Python with these packages installed:

- TensorFlow
- NumPy
- pandas
- Pillow

The prediction module uses the GPU when TensorFlow detects one; otherwise,
it falls back to the CPU.

## Example

Run from the repository root:

```python
from ml.krushi_aadhar_8crop.krushi_aadhar_predict import krushi_aadhar_predict

results = krushi_aadhar_predict("/path/to/plant_image.jpg", top_k=3)
print(results.to_string(index=False))
```

The returned results include the predicted class, confidence, disease ID,
symptoms, management, treatment, prevention, severity, and source.

## Important note

Prediction confidence is not a guarantee of real-world diagnostic accuracy.
Confirm results in the field and consult appropriate agricultural experts
before applying treatments.

The training image dataset is not included in this directory.
