#!/bin/bash
# Send APK file to Telegram bot owner
# Usage: ./send-to-telegram.sh <bot_token> <chat_id> <apk_file_path> <version_tag> <commit_count>

set -e

BOT_TOKEN="$1"
CHAT_ID="$2"
APK_FILE="$3"
VERSION_TAG="$4"
COMMIT_COUNT="$5"
GITHUB_REPO="${6:-komikku-app/komikku}"
GITHUB_SERVER_URL="${7:-https://github.com}"

# Validate inputs
if [ -z "$BOT_TOKEN" ]; then
    echo "❌ Error: BOT_TOKEN not provided"
    exit 1
fi

if [ -z "$CHAT_ID" ]; then
    echo "❌ Error: CHAT_ID not provided"
    exit 1
fi

if [ -z "$APK_FILE" ]; then
    echo "❌ Error: APK_FILE not provided"
    exit 1
fi

if [ ! -f "$APK_FILE" ]; then
    echo "❌ Error: APK file not found: $APK_FILE"
    exit 1
fi

# Get file size in MB
APK_SIZE=$(du -h "$APK_FILE" | cut -f1)

# Extract filename
APK_FILENAME=$(basename "$APK_FILE")

# Build message with commit info
COMMIT_SHA=$(git rev-parse --short HEAD)
COMMIT_AUTHOR=$(git log -1 --format='%an')
COMMIT_MESSAGE=$(git log -1 --format='%s')

# Build Telegram message
MESSAGE="🚀 *Komikku APK Build Ready*

📦 *File:* \`$APK_FILENAME\`
📊 *Size:* $APK_SIZE
🔢 *Version:* $VERSION_TAG
📈 *Build:* #$COMMIT_COUNT
🔗 *Commit:* \`$COMMIT_SHA\`

👤 *Author:* $COMMIT_AUTHOR
💬 *Message:* $COMMIT_MESSAGE

✅ Build completed successfully!"

echo "📤 Sending APK to Telegram..."
echo "File: $APK_FILE"
echo "Size: $APK_SIZE"

# Send file to Telegram
RESPONSE=$(curl -s -F "chat_id=$CHAT_ID" \
    -F "document=@$APK_FILE" \
    -F "caption=$MESSAGE" \
    -F "parse_mode=Markdown" \
    "https://api.telegram.org/bot$BOT_TOKEN/sendDocument")

# Check response
if echo "$RESPONSE" | grep -q '"ok":true'; then
    echo "✅ APK sent successfully to Telegram!"
    echo "Response: $RESPONSE"
    exit 0
else
    echo "❌ Failed to send APK to Telegram"
    echo "Response: $RESPONSE"
    exit 1
fi
