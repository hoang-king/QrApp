from pathlib import Path

directories = [
    r'C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1\app\src\main\java\com\example\qrgrenertor\domain\model',
    r'C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1\app\src\main\java\com\example\qrgrenertor\domain\repository',
    r'C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1\app\src\main\java\com\example\qrgrenertor\domain\usecase',
    r'C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1\app\src\main\java\com\example\qrgrenertor\data\local\entity',
    r'C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1\app\src\main\java\com\example\qrgrenertor\data\local\dao',
    r'C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1\app\src\main\java\com\example\qrgrenertor\data\local\database',
    r'C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1\app\src\main\java\com\example\qrgrenertor\data\remote\dto',
    r'C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1\app\src\main\java\com\example\qrgrenertor\data\remote\api',
    r'C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1\app\src\main\java\com\example\qrgrenertor\data\mapper',
    r'C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1\app\src\main\java\com\example\qrgrenertor\data\repository_impl',
    r'C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1\app\src\main\java\com\example\qrgrenertor\data\sync',
    r'C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1\app\src\main\java\com\example\qrgrenertor\presentation\ui',
    r'C:\Users\Nam_Nguyen\AndroidStudioProjects\QrApp1\app\src\main\java\com\example\qrgrenertor\presentation\viewmodel'
]

for directory in directories:
    Path(directory).mkdir(parents=True, exist_ok=True)
    print(f'Created: {directory}')

print(f'\nSuccessfully created {len(directories)} directories')
