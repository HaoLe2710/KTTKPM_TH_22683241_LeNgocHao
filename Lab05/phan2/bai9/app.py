from flask import Flask, jsonify

app = Flask(__name__)

@app.route("/")
def home():
    return "Hello from Flask Docker Compose!"

@app.route("/api")
def api():
    return jsonify({
        "message": "Flask API is working!"
    })

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
