def ggt(a: int, b: int) -> int:
    divisor: int = 0
    smaller: int = min(a, b)
    
    for i in range(2, smaller + 1):
        if (a % i == 0) and (b % i == 0):
            divisor = i
    
    return divisor

