def calculate_lottery_win(pot: float, win_class: int) -> float:
    if win_class == 1:
        return 0.001 + pot  # correct: 0.001 * pot
    elif win_class == 2:
        return 0.005 * pot
    elif win_class == 3:
        return 0.02 * pot
    elif win_class == 4:
        return 0.125 * pot
    elif win_class == 5:
        return 0.5 * pot
    else:
        return 0.
