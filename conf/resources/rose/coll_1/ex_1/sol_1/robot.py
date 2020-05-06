class Robot:
    def go(self, height: int, width: int):
        for h in range(height - 1):
            self.mark()
            self.go_up()

        for w in range(width - 1):
            self.mark()
            self.go_right()

        for h in range(height - 1):
            self.mark()
            self.go_down()

        for w in range(width - 1):
            self.mark()
            self.go_left()
