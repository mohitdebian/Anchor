import re

file_path = "app/build.gradle.kts"
with open(file_path, "r") as f:
    content = f.read()

content = re.sub(r'applicationId = ".*?"', 'applicationId = "com.devlabs.anchor"', content)
content = re.sub(r'versionCode = \d+', 'versionCode = 3', content)

with open(file_path, "w") as f:
    f.write(content)
