import re

file_path = "app/src/main/java/com/example/AlarmScheduler.kt"
with open(file_path, "r") as f:
    content = f.read()

if content.endswith("}\n}\n}"):
    content = content[:-2]
elif content.endswith("}\n}"):
    # Wait, the problem is 3 closing brackets instead of 2.
    # Let me just strip trailing brackets and add exactly 2.
    pass

# Better approach:
lines = content.rstrip().split('\n')
if lines[-1] == '}' and lines[-2] == '}' and lines[-3] == '}':
    lines.pop()

with open(file_path, "w") as f:
    f.write('\n'.join(lines) + '\n')
