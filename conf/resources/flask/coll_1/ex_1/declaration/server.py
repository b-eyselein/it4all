from typing import List, Tuple, Optional

from bcrypt import hashpw, checkpw, gensalt
from flask import Flask, render_template, request, session, url_for, redirect

app = Flask(__name__)
app.secret_key = "0i9asdufzvßy9s8dzfßüa089sfß9a8"

user_id_field: str = "userId"

users: List[Tuple[str, str]] = [("test", "$2b$12$MN/nWaRWwHUsFxrA4lMGzO2mCxDvSZarO2.x6tth8IChiNlbs39IG")]

# TODO: routes...

if __name__ == "__main__":
    app.debug = True
    app.run()
