import os

file_path = "app/src/main/AndroidManifest.xml"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(
    '</application>',
    '    <receiver android:name=".AlarmReceiver" android:exported="false" />\n    </application>'
)

with open(file_path, "w") as f:
    f.write(content)
