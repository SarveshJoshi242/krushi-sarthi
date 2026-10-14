import os
import json
import pandas as pd
import numpy as np
import tensorflow as tf

def build_and_export():
    print("[1/5] Loading 55 Master Classes from CSV...")
    base_dir = os.path.dirname(os.path.abspath(__file__))
    mapping_csv = os.path.join(base_dir, "..", "dataset", "Krushi_Aadhar_Master_Class_Mapping.csv")
    disease_info_csv = os.path.join(base_dir, "..", "dataset", "Krushi_Aadhar_Disease_Info.csv")
    
    if os.path.exists(mapping_csv):
        mapping_df = pd.read_csv(mapping_csv)
    else:
        mapping_df = pd.read_csv(disease_info_csv)
    
    classes = mapping_df['Class_Name'].tolist()
    print(f"Total classes found: {len(classes)}")
    assert len(classes) == 55, f"Expected 55 classes, got {len(classes)}"
    
    model_dir = os.path.join(base_dir, "model")
    os.makedirs(model_dir, exist_ok=True)
    
    # Save class_names.json
    class_names_path = os.path.join(model_dir, "class_names.json")
    with open(class_names_path, "w", encoding="utf-8") as f:
        json.dump(classes, f, indent=2)
    print(f"[2/5] Saved class names to {class_names_path}")
    
    # Build Keras MobileNetV2 Disease Classifier
    print("[3/5] Building MobileNetV2 architecture (Input 224x224x3, Output 55)...")
    base_model = tf.keras.applications.MobileNetV2(
        input_shape=(224, 224, 3),
        include_top=False,
        weights=None
    )
    
    inputs = tf.keras.Input(shape=(224, 224, 3), name="input_1")
    x = base_model(inputs)
    x = tf.keras.layers.GlobalAveragePooling2D(name="global_avg_pool")(x)
    x = tf.keras.layers.BatchNormalization()(x)
    x = tf.keras.layers.Dense(256, activation="relu", name="dense_features")(x)
    x = tf.keras.layers.Dropout(0.3)(x)
    outputs = tf.keras.layers.Dense(55, activation="softmax", name="disease_classifier")(x)
    
    model = tf.keras.Model(inputs=inputs, outputs=outputs, name="Krishi_Aadhar_Pest_Doctor")
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=0.001),
        loss="sparse_categorical_crossentropy",
        metrics=["accuracy"]
    )
    
    h5_path = os.path.join(model_dir, "crop_disease_model.h5")
    model.save(h5_path)
    print(f"[4/5] Saved Keras model to {h5_path}")
    
    # Convert to TFLite
    print("[5/5] Converting to TFLite model for on-device inference...")
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    tflite_model = converter.convert()
    
    ml_tflite_path = os.path.join(model_dir, "krushi_aadhar.tflite")
    with open(ml_tflite_path, "wb") as f:
        f.write(tflite_model)
    print(f"Saved TFLite model to {ml_tflite_path} ({len(tflite_model) / 1024:.1f} KB)")
    
    # Copy to Android assets
    android_assets_dir = os.path.join(base_dir, "..", "android", "app", "src", "main", "assets")
    if os.path.exists(android_assets_dir):
        android_tflite_path = os.path.join(android_assets_dir, "krushi_aadhar.tflite")
        with open(android_tflite_path, "wb") as f:
            f.write(tflite_model)
        print(f"Copied TFLite model to Android assets: {android_tflite_path}")
    
    # Quick sanity check with TFLite interpreter
    interpreter = tf.lite.Interpreter(model_content=tflite_model)
    interpreter.allocate_tensors()
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()
    print("Verification:")
    print("Input shape:", input_details[0]['shape'], "type:", input_details[0]['dtype'])
    print("Output shape:", output_details[0]['shape'], "type:", output_details[0]['dtype'])
    print("SUCCESS: Model generation, export, and Android asset synchronization complete!")

if __name__ == "__main__":
    build_and_export()
