import re

file_path = "app/src/main/AndroidManifest.xml"
with open(file_path, "r") as f:
    content = f.read()

if '<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />' not in content:
    content = content.replace(
        '<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />',
        '<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />\n    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />'
    )

content = content.replace(
    '<service android:name=".services.BlockService" android:exported="false" />',
    '<service android:name=".services.BlockService" android:exported="false" android:foregroundServiceType="specialUse">\n            <property android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE" android:value="Monitoring apps for focus mode"/>\n        </service>'
)

with open(file_path, "w") as f:
    f.write(content)
