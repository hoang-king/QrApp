import os
import subprocess
from pathlib import Path

base_path = r'C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1'

directories = [
    r'app\src\main\java\com\example\qrgrenertor\domain\model',
    r'app\src\main\java\com\example\qrgrenertor\domain\repository',
    r'app\src\main\java\com\example\qrgrenertor\domain\usecase',
    r'app\src\main\java\com\example\qrgrenertor\data\local\entity',
    r'app\src\main\java\com\example\qrgrenertor\data\local\dao',
    r'app\src\main\java\com\example\qrgrenertor\data\local\database',
    r'app\src\main\java\com\example\qrgrenertor\data\remote\dto',
    r'app\src\main\java\com\example\qrgrenertor\data\remote\api',
    r'app\src\main\java\com\example\qrgrenertor\data\mapper',
    r'app\src\main\java\com\example\qrgrenertor\data\repository_impl',
    r'app\src\main\java\com\example\qrgrenertor\data\sync'
]

os.chdir(base_path)

for directory in directories:
    try:
        Path(directory).mkdir(parents=True, exist_ok=True)
        print(f'Created: {directory}')
    except Exception as e:
        print(f'Error creating {directory}: {e}')

print(f'\nAll {len(directories)} directories processed successfully!')

# Verify directories exist
print("\n--- Verification ---")
for directory in directories:
    if Path(directory).exists():
        print(f'✓ {directory}')
    else:
        print(f'✗ {directory} (FAILED)')

