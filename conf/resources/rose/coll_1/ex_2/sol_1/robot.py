class Robot:
    def go(self, rabbit_x: int, rabbit_y: int):
        for h in range(rabbit_x - 1):
            self.go_up()

        for w in range(rabbit_y - 1):
            self.go_right()

        self.mark()
