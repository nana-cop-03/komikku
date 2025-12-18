import sys
import time
import requests

BOT_TOKEN, CHAT_ID, FILE_PATH, VERSION, COUNT = sys.argv[1:6]

URL = f"https://api.telegram.org/bot{BOT_TOKEN}/sendDocument"

caption = f"""
📦 *Nana-Comik Build*
• Version: `{VERSION}`
• Revision: `{COUNT}`
""".strip()

for attempt in range(1, 4):
    try:
        with open(FILE_PATH, "rb") as f:
            r = requests.post(
                URL,
                data={
                    "chat_id": CHAT_ID,
                    "caption": caption,
                    "parse_mode": "Markdown"
                },
                files={"document": f},
                timeout=900
            )

        if r.ok:
            print("✅ Telegram upload successful")
            exit(0)

        print(f"⚠️ Attempt {attempt} failed:", r.text)

    except Exception as e:
        print(f"❌ Attempt {attempt} error:", e)

    time.sleep(10)

print("❌ Telegram upload failed after retries")
exit(1)
