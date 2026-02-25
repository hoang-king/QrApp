import os
import sys

base_path = r"C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1\app\src\main\java\com\example\qrgrenertor"

folders = [
    os.path.join(base_path, "domain", "model"),
    os.path.join(base_path, "domain", "repository"),
    os.path.join(base_path, "domain", "usecase"),
    os.path.join(base_path, "data", "local", "entity"),
    os.path.join(base_path, "data", "local", "dao"),
    os.path.join(base_path, "data", "local", "database"),
    os.path.join(base_path, "data", "remote", "dto"),
    os.path.join(base_path, "data", "remote", "api"),
    os.path.join(base_path, "data", "mapper"),
    os.path.join(base_path, "data", "repository_impl"),
    os.path.join(base_path, "data", "sync"),
    os.path.join(base_path, "presentation", "ui"),
    os.path.join(base_path, "presentation", "viewmodel"),
]

for folder in folders:
    try:
        os.makedirs(folder, exist_ok=True)
        print(f"Created: {folder}")
    except Exception as e:
        print(f"Error creating {folder}: {e}")

print("\nAll folders have been created successfully!")
