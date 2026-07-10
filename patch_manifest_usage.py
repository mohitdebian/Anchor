import re

file_path = "app/src/main/AndroidManifest.xml"
with open(file_path, "r") as f:
    content = f.read()

if '<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />' not in content:
    content = content.replace(
        '<application',
        '<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" tools:ignore="ProtectedPermissions" />\n    <application'
    )

if 'xmlns:tools="http://schemas.android.com/tools"' not in content:
    content = content.replace(
        'xmlns:android="http://schemas.android.com/apk/res/android"',
        'xmlns:android="http://schemas.android.com/apk/res/android"\n    xmlns:tools="http://schemas.android.com/tools"'
    )

with open(file_path, "w") as f:
    f.write(content)
