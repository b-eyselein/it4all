def ggt(a: int, b: int) -> int:
    # Berechnung mittels Euklidschem Algorithmus
    while b != 0:
        h: int = a % b
        a = b
        b = h

    return a
