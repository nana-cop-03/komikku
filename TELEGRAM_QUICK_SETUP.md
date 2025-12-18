# Telegram APK Delivery - Quick Setup Checklist

## ✅ What Was Implemented

- [x] **send-to-telegram.sh** - Bash script to send APKs to Telegram
- [x] **build_push.yml** - Updated CI workflow with Telegram notification
- [x] **build_release.yml** - Updated Release workflow with Telegram notifications
- [x] **TELEGRAM_SETUP.md** - Complete setup documentation

## 📋 Setup Steps (Do This Once)

### Step 1: Create Telegram Bot
- [ ] Open Telegram and chat with **@BotFather**
- [ ] Send `/newbot` and follow prompts
- [ ] Copy the **API Token** from BotFather
- [ ] Example: `123456789:ABCDefGhIjKlMnoPqRsTuVwXyZ`

### Step 2: Get Your Chat ID
- [ ] Message your bot with `/start`
- [ ] Run this command in terminal:
```bash
curl "https://api.telegram.org/bot<YOUR_BOT_TOKEN>/getUpdates"
```
- [ ] Copy your chat ID from the response (look for `"id": 123456789`)

### Step 3: Add Secrets to GitHub
1. [ ] Go to: **GitHub → Settings → Secrets and variables → Actions**
2. [ ] Click **New repository secret**
3. [ ] Add `TELEGRAM_BOT_TOKEN`
   - Value: Your bot token from Step 1
4. [ ] Click **New repository secret** again
5. [ ] Add `TELEGRAM_CHAT_ID`
   - Value: Your chat ID from Step 2

### Step 4: Test (Optional)
```bash
# Make script executable
chmod +x scripts/send-to-telegram.sh

# Build the app locally
./gradlew assemblePreview -Penable-updater

# Test sending
./scripts/send-to-telegram.sh \
  "YOUR_BOT_TOKEN" \
  "YOUR_CHAT_ID" \
  "app/build/outputs/apk/preview/app-universal-preview.apk" \
  "test-v1" \
  "123"
```

## 🚀 How It Works Now

### On Every Push to Master
```
Push to master → Build APK → Upload to Artifact → Send to Telegram ✅
```

You get a Telegram message with:
- APK filename and size
- Version tag (branch: master)
- Build number (commit count)
- Commit hash
- Author and commit message

### On Release Tag Push
```
Push tag (v1.0.0) → Build All Variants → Create GitHub Release → Send to Telegram ✅
```

You get Telegram messages for:
- Universal APK (works on all devices)
- arm64-v8a APK (recommended for modern phones)

## 📲 Example Telegram Message

```
🚀 Komikku APK Build Ready

📦 File: Nana-Comik-master-r1234.apk
📊 Size: 45.2M
🔢 Version: master
📈 Build: #1234
🔗 Commit: abc1234

👤 Author: John Developer
💬 Message: Fix PDF rendering issue

✅ Build completed successfully!
```

You can download directly from Telegram!

## ⚠️ Troubleshooting

| Problem | Solution |
|---------|----------|
| No message received | Check if secrets are correctly added |
| "Bot was blocked" | Message your bot `/start` again |
| "Chat ID not found" | Verify chat ID is correct in secrets |
| APK not sent | Check GitHub Actions workflow logs |

## 📚 More Info

See **TELEGRAM_SETUP.md** for:
- Detailed troubleshooting
- Advanced usage (multiple recipients)
- Security notes
- How to disable notifications

## 💡 Next Steps

1. Create your Telegram bot (Steps 1-3)
2. Add secrets to GitHub (Step 3)
3. Push to master and watch your Telegram inbox! 📬

That's it! Your APKs will now be delivered automatically to Telegram. 🎉

