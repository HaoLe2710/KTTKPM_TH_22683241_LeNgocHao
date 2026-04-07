import json
import os
from flask import Flask, render_template, request, redirect
import redis

app = Flask(__name__)

REDIS_HOST = os.getenv("REDIS_HOST", "redis")
OPTION_A = os.getenv("OPTION_A", "Cats")
OPTION_B = os.getenv("OPTION_B", "Dogs")

r = redis.Redis(host=REDIS_HOST, port=6379, db=0, decode_responses=True)

@app.route("/", methods=["GET"])
def index():
    return render_template("index.html", option_a=OPTION_A, option_b=OPTION_B)

@app.route("/vote", methods=["POST"])
def vote():
    vote_value = request.form.get("vote")
    if vote_value in ["a", "b"]:
        payload = json.dumps({"vote": vote_value})
        r.rpush("votes", payload)
    return redirect("/")

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=80, debug=False)
