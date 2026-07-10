import re

file_path = "app/src/main/java/com/example/navigation/AnchorNavigation.kt"
with open(file_path, "r") as f:
    content = f.read()

# Strip any trailing whitespace and braces
content = re.sub(r'(\s*\})+(\s*)$', '', content)

# Add the proper ending braces
correct_ending = """
                    }
                }
            }
        }
    }
}
}
"""

content = content + correct_ending

with open(file_path, "w") as f:
    f.write(content)
