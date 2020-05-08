def greet(hour: int) -> str:
    if 0 <= hour < 6:
        return "Good night"
    if 6 <= hour < 12:
        return "Good morning"
    elif 12 <= hour < 18:
        return "Good evening"
    elif 18 <= hour < 21:
        return "Good evening"
    elif 21 <= hour <= 24:
        return "Good night"
    else:
        return "I do not know this time."
