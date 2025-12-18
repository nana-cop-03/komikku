# Telegram APK Delivery Setup Guide

This guide explains how to set up automated APK delivery to Telegram when builds complete successfully.

## Overview

The workflow sends APK files automatically to Telegram when:
- **CI Build** (`build_push.yml`): Each push to master branch completes successfully
- **Release Build** (`build_release.yml`): Each release tag is created successfully

## Prerequisites

### 1. Create a Telegram Bot

1. Open Telegram and search for **@BotFather**
2. Send `/start` command
3. Send `/newbot` command
4. Follow the prompts:
   - Choose a name for your bot (e.g., "Komikku Release Bot")
   - Choose a username (e.g., "@komikku_release_bot")
5. Copy the **API Token** (looks like: `123456789:ABCDefGhIjKlMnoPqRsTuVwXyZ`)

### 2. Get Your Chat ID

#### Option A: Direct Message (Recommended for Private)

1. Message your bot with: `/start`
2. Use this Python script to get your Chat ID:

```bash
curl "https://api.telegram.org/bot<YOUR_BOT_TOKEN>/getUpdates"
```

Replace `<YOUR_BOT_TOKEN>` with your token. Look for the `"id"` field in the response.

#### Option B: Group Chat

1. Add your bot to a group or channel
2. Send a message in that group/channel
3. Get the Chat ID using the curl command above (negative number for groups)

### 3. Add Secrets to GitHub

1. Go to your repository: **Settings → Secrets and variables → Actions**
2. Click **New repository secret** and add:

#### Secret 1: TELEGRAM_BOT_TOKEN
- **Name**: `TELEGRAM_BOT_TOKEN`
- **Value**: Your bot token from BotFather (e.g., `123456789:ABCDefGhIjKlMnoPqRsTuVwXyZ`)

#### Secret 2: TELEGRAM_CHAT_ID
- **Name**: `TELEGRAM_CHAT_ID`
- **Value**: Your Chat ID (e.g., `123456789` or negative number for groups)

## How It Works

### CI Build Workflow (`build_push.yml`)

When you push to the master branch:

1. ✅ Code is checked out and formatted
2. ✅ App is built with gradle
3. ✅ Unit tests run
4. ✅ APK is renamed to: `Nana-Comik-master-r{COMMIT_COUNT}.apk`
5. ✅ APK is uploaded as artifact
6. 📤 **APK is sent to Telegram** with:
   - File name and size
   - Version tag (branch name)
   - Build number (commit count)
   - Commit SHA
   - Author and commit message

### Release Build Workflow (`build_release.yml`)

When you create a release tag (e.g., `v1.0.0`):

1. ✅ Build info is prepared
2. ✅ App is built with gradle
3. ✅ Unit tests run
4. ✅ Multiple APK variants are created:
   - Universal (all architectures)
   - arm64-v8a (64-bit ARM)
   - armeabi-v7a (32-bit ARM)
   - x86 and x86_64 variants
5. ✅ GitHub Release is created with checksums
6. 📤 **Universal and arm64-v8a APKs are sent to Telegram**

## Telegram Message Format

You'll receive messages like:

```
🚀 Komikku APK Build Ready

📦 File: Nana-Comik-master-r1234.apk
📊 Size: 45.2M
🔢 Version: master
📈 Build: #1234
🔗 Commit: abc1234

👤 Author: Developer Name
💬 Message: Implement new feature

✅ Build completed successfully!
```

You can then download the APK directly from Telegram!

## Manual Testing

To test the setup without pushing code:

```bash
# 1. Make the script executable
chmod +x scripts/send-to-telegram.sh

# 2. Build the app locally
./gradlew assemblePreview -Penable-updater

# 3. Send a test APK
./scripts/send-to-telegram.sh \
  "YOUR_BOT_TOKEN" \
  "YOUR_CHAT_ID" \
  "app/build/outputs/apk/preview/app-universal-preview.apk" \
  "test-build" \
  "12345"
```

## Troubleshooting

### Telegram API Errors

| Error | Solution |
|-------|----------|
| `{"ok":false,"error_code":401}` | Bot token is invalid - check in BotFather |
| `{"ok":false,"error_code":403}` | Chat ID is wrong or bot not added to group |
| `{"ok":false,"error_code":400}` | APK file too large (limit: 50MB for telegram) |
| `Bot was blocked by the user` | User blocked the bot - use `/start` again |

### Build Fails Without Sending

The Telegram step only runs if:
- ✅ Build completed successfully (`if: success()`)
- ✅ APK file exists at the expected path
- ✅ Both secrets are configured

Check workflow logs for details if APK wasn't sent.

### APK Too Large

Telegram has a 50MB file size limit. If your APK is larger:

1. Check if ProGuard/R8 is properly enabled
2. Compare with reference build sizes
3. Consider splitting by architecture (arm64-v8a is smaller)

## File Locations

```
scripts/
  └── send-to-telegram.sh          # Main Telegram delivery script

.github/workflows/
  ├── build_push.yml               # CI workflow (sends on every push)
  └── build_release.yml            # Release workflow (sends on tags)
```

## Workflow Steps

### build_push.yml Steps

1. Clone repo
2. Setup JDK 17
3. Create dummy config files (google-services.json, client_secrets.json)
4. Decode keystore
5. Setup Gradle
6. Check code format (spotless)
7. **Build app** (assemblePreview)
8. Run unit tests
9. Rename APK with version/commit info
10. Upload APK as artifact
11. Upload mapping files
12. **📤 Send APK to Telegram** ← YOU ARE HERE

### build_release.yml Steps (Release Job)

1. Prepare build metadata
2. Clone repo
3. Setup JDK 17
4. Create dummy config files
5. Decode keystore
6. Setup Gradle
7. Check code format
8. **Build app** (assembleRelease with variants)
9. Run unit tests
10. Upload all artifacts
11. Upload mapping files
12. Download artifacts for release job
13. Get APK SHAs and clean up
14. **Create GitHub Release** with checksums
15. **📤 Send APKs to Telegram** ← YOU ARE HERE

## Security Notes

- Never commit Bot Token or Chat ID to repository
- Always use GitHub Secrets for sensitive data
- Telegram bot tokens should be rotated if exposed
- Consider using a dedicated bot for builds (separate from your main bot)

## Disable Telegram Notifications

To temporarily disable Telegram sending:

### Option 1: Comment out the step

Edit the workflow file and comment out the Telegram step:

```yaml
# - name: Send APK to Telegram
#   if: success()
#   run: |
```

### Option 2: Remove secrets

Delete the `TELEGRAM_BOT_TOKEN` or `TELEGRAM_CHAT_ID` secrets from GitHub.

## Advanced: Multiple Recipients

To send to multiple users/groups, duplicate the send step:

```yaml
- name: Send APK to Telegram (Owner)
  if: success()
  run: |
    chmod +x scripts/send-to-telegram.sh
    ./scripts/send-to-telegram.sh "$BOT_TOKEN" "$OWNER_CHAT_ID" ...

- name: Send APK to Telegram (Group)
  if: success()
  run: |
    ./scripts/send-to-telegram.sh "$BOT_TOKEN" "$GROUP_CHAT_ID" ...
```

Add separate secrets: `TELEGRAM_OWNER_CHAT_ID`, `TELEGRAM_GROUP_CHAT_ID`

## Future Enhancements

- [ ] Send changelog/commit history with APK
- [ ] Post download link to GitHub Release in Telegram
- [ ] Monitor download count and share in Telegram
- [ ] Add inline keyboard for quick feedback
- [ ] Support multiple Telegram recipients
- [ ] Store delivery history in database

