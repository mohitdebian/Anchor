import re

file_path = "app/src/main/AndroidManifest.xml"
with open(file_path, "r") as f:
    content = f.read()

if '<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />' not in content:
    content = content.replace(
        '<application',
        '<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />\n    <application'
    )

if '<service android:name=".services.BlockService"' not in content:
    content = content.replace(
        '</application>',
        '    <service android:name=".services.BlockService" android:exported="false" />\n    </application>'
    )

with open(file_path, "w") as f:
    f.write(content)
