with open("app/src/main/java/com/example/ui/screens/planner/PlannerScreen.kt", "r") as f:
    lines = f.readlines()

with open("app/src/main/java/com/example/ui/screens/planner/PlannerScreen.kt", "w") as f:
    for line in lines:
        if 'text = "No schedules for this date.' in line:
            f.write('                        text = "No schedules for this date.\\nTap + to add one.",\n')
        elif 'Tap + to add one.",' in line:
            pass # skip the second line
        else:
            f.write(line)
