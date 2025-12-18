#!/usr/bin/env python3
"""Send APK file to Telegram Bot API using requests library."""

import os
import sys
import subprocess
from pathlib import Path

try:
    import requests
except ImportError:
    print("❌ Error: 'requests' library not found")
    print("Install it with: pip install requests")
    sys.exit(1)


def get_file_info(file_path: str) -> tuple[str, int]:
    """Get file size in bytes and human-readable format."""
    path = Path(file_path)
    size_bytes = path.stat().st_size
    size_mb = size_bytes / (1024 * 1024)
    
    if size_mb > 1024:
        size_human = f"{size_mb / 1024:.1f}GB"
    else:
        size_human = f"{size_mb:.1f}MB"
    
    return size_human, size_bytes


def get_commit_info() -> tuple[str, str, str]:
    """Get git commit information."""
    try:
        commit_sha = subprocess.check_output(
            ["git", "rev-parse", "--short", "HEAD"],
            text=True
        ).strip()
        
        commit_author = subprocess.check_output(
            ["git", "log", "-1", "--format=%an"],
            text=True
        ).strip()
        
        commit_message = subprocess.check_output(
            ["git", "log", "-1", "--format=%s"],
            text=True
        ).strip()
        
        return commit_sha, commit_author, commit_message
    except Exception as e:
        print(f"⚠️  Warning: Could not get git info: {e}")
        return "unknown", "unknown", "unknown"


def send_to_telegram(
    bot_token: str,
    chat_id: str,
    file_path: str,
    version_tag: str,
    build_count: str,
) -> bool:
    """Send APK file to Telegram using requests library."""
    
    # Validate inputs
    if not bot_token:
        print("❌ Error: BOT_TOKEN not provided")
        return False
    
    if not chat_id:
        print("❌ Error: CHAT_ID not provided")
        return False
    
    if not os.path.isfile(file_path):
        print(f"❌ Error: APK file not found: {file_path}")
        return False
    
    # Check file size
    size_human, size_bytes = get_file_info(file_path)
    size_mb = size_bytes / (1024 * 1024)
    
    if size_mb > 50:
        print(f"❌ Error: APK file is too large for Telegram ({size_mb:.1f}MB > 50MB limit)")
        print("")
        print("⚠️  Solutions:")
        print("   1. Enable ProGuard/R8 minification in build.gradle.kts")
        print("   2. Send architecture-specific APK (arm64-v8a is smaller)")
        print("   3. Upload to GitHub Releases and share the link instead")
        return False
    
    # Get commit info
    commit_sha, commit_author, commit_message = get_commit_info()
    
    # Build message
    filename = os.path.basename(file_path)
    message = f"""🚀 *Komikku APK Build Ready*

📦 *File:* `{filename}`
📊 *Size:* {size_human}
🔢 *Version:* {version_tag}
📈 *Build:* #{build_count}
🔗 *Commit:* `{commit_sha}`

👤 *Author:* {commit_author}
💬 *Message:* {commit_message}

✅ Build completed successfully!"""
    
    # Send to Telegram
    url = f"https://api.telegram.org/bot{bot_token}/sendDocument"
    
    print("📤 Sending APK to Telegram...")
    print(f"File: {file_path}")
    print(f"Size: {size_human}")
    
    try:
        with open(file_path, "rb") as f:
            response = requests.post(
                url,
                data={
                    "chat_id": chat_id,
                    "caption": message,
                    "parse_mode": "Markdown",
                },
                files={"document": f},
                timeout=600,
            )
        
        if response.status_code == 200:
            result = response.json()
            if result.get("ok"):
                print("✅ APK sent successfully to Telegram!")
                print(f"Response: {response.text}")
                return True
            else:
                error_desc = result.get("description", "Unknown error")
                print(f"❌ Failed to send APK to Telegram: {error_desc}")
                print(f"Response: {response.text}")
                return False
        else:
            print(f"❌ HTTP Error {response.status_code}: {response.text}")
            return False
    
    except requests.exceptions.Timeout:
        print("❌ Error: Request timeout (file might be too large)")
        return False
    except requests.exceptions.RequestException as e:
        print(f"❌ Error: {e}")
        return False


if __name__ == "__main__":
    if len(sys.argv) < 6:
        print("Usage: send-to-telegram.py <bot_token> <chat_id> <apk_path> <version> <build_count>")
        sys.exit(1)
    
    bot_token = sys.argv[1]
    chat_id = sys.argv[2]
    apk_path = sys.argv[3]
    version = sys.argv[4]
    build_count = sys.argv[5]
    
    success = send_to_telegram(bot_token, chat_id, apk_path, version, build_count)
    sys.exit(0 if success else 1)
