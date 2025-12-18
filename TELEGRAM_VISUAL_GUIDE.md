# Telegram APK Delivery - Visual Guide

## 🏗️ Architecture Diagram

```
                    ┌─────────────────────────────────┐
                    │   Your Local Machine            │
                    │                                 │
                    │  git commit && git push         │
                    │     (to master or tag)          │
                    └──────────────┬──────────────────┘
                                   │
                                   ▼
                    ┌─────────────────────────────────┐
                    │   GitHub Repository             │
                    │                                 │
                    │   komikku-app/komikku           │
                    └──────────────┬──────────────────┘
                                   │
                    ┌──────────────┴──────────────────┐
                    │                                 │
                    ▼                                 ▼
        ┌──────────────────────────┐      ┌──────────────────────────┐
        │   CI Build Trigger       │      │  Release Build Trigger   │
        │   (master push)          │      │  (tag v*.x.x push)       │
        │                          │      │                          │
        │   build_push.yml ──┐     │      │   build_release.yml ──┐  │
        └──────────────────────────┘      └──────────────────────────┘
                    │                                 │
        ┌───────────┴──────────────┐                  │
        │                          │                  │
        │   Build Steps:           │                  │
        │   1. spotlessCheck ✅    │                  │
        │   2. assemblePreview ✅  │                  │
        │   3. testRelease ✅      │      ┌───────────┴──────────────────┐
        │   4. Rename APK ✅       │      │                              │
        │   5. Upload Artifact ✅  │      │   Build Steps:               │
        │   6. SUCCESS ✅          │      │   1. spotlessCheck ✅        │
        │                          │      │   2. assembleRelease ✅      │
        │                          │      │   3. testRelease ✅          │
        │                          │      │   4. Rename all APKs ✅      │
        │                          │      │   5. Create GitHub Release ✅│
        │                          │      │   6. SUCCESS ✅              │
        │                          │      │                              │
        └──────────────┬───────────┘      └──────────────┬───────────────┘
                       │                                 │
                       ▼                                 ▼
        ┌─────────────────────────────────────────────────────────────┐
        │  GitHub Secrets                                             │
        │  TELEGRAM_BOT_TOKEN = 123456789:ABCDefGhIjKlMnoPqRsTuVwXyZ │
        │  TELEGRAM_CHAT_ID = 987654321                              │
        └────────────────────────┬────────────────────────────────────┘
                                 │
        ┌────────────────────────┴──────────────────────┐
        │                                               │
        ▼                                               ▼
  ┌──────────────────────────────┐         ┌────────────────────────────┐
  │  Send APK to Telegram        │         │  Send APKs to Telegram     │
  │  (1 file - Universal)        │         │  (2 files - Universal      │
  │                              │         │   + arm64-v8a)             │
  │  scripts/send-to-telegram.sh │         │                            │
  │                              │         │  scripts/send-to-telegram. │
  │  - Makes script executable   │         │  sh (called twice)         │
  │  - Passes secrets & APK path │         │                            │
  │  - Script validates inputs   │         │  - Makes script executable │
  │  - Gets git commit info      │         │  - Passes secrets & paths  │
  │  - Calculates file size      │         │  - Same validation         │
  │  - Formats message with      │         │  - Sends both variants     │
  │    emojis and details        │         │                            │
  │  - Calls Telegram API        │         │  - Calls Telegram API x2   │
  └──────────────────┬───────────┘         └────────────────┬───────────┘
                     │                                      │
                     │    ┌──────────────────────────────┐  │
                     └───►│   Telegram Bot API          │◄─┘
                          │   api.telegram.org/bot       │
                          │   /sendDocument              │
                          └───────────────┬──────────────┘
                                         │
                                         ▼
                          ┌──────────────────────────────┐
                          │   Telegram Cloud Storage     │
                          │   Files stored securely      │
                          └───────────────┬──────────────┘
                                         │
                                         ▼
                          ┌──────────────────────────────┐
                          │   Your Telegram App          │
                          │   📱                         │
                          │   Message Received! ✅       │
                          │                              │
                          │   🚀 Komikku APK Ready      │
                          │   📦 File: Nana-Comik-...   │
                          │   📊 Size: 45.2M            │
                          │   👤 Author: Dev Name        │
                          │   📥 TAP TO DOWNLOAD         │
                          └──────────────────────────────┘
```

## 🔄 Workflow Timeline

### CI Build (Master Push)
```
Time    Action                                      Status
────────────────────────────────────────────────────────────
T+0s    git push origin master                      ▶ Your laptop
T+2s    GitHub Actions triggered                   📡 GitHub
T+5s    Clone repo + setup JDK                     ⚙️  Building
T+15s   spotlessCheck (formatting)                 ✅ Passed
T+45s   assemblePreview (build APK)                ⏳ Building
T+120s  testReleaseUnitTest                        ✅ Passed
T+135s  Rename APK file                            ✅ Renamed
T+140s  Upload to artifacts                        📤 Uploading
T+145s  Telegram send step starts                  🤖 Bot
T+150s  Message sent to Telegram                   📱 Telegram
T+151s  Workflow complete                          ✅ DONE

→ You receive APK in Telegram inbox!
```

### Release Build (Tag Push)
```
Time    Action                                      Status
────────────────────────────────────────────────────────────
T+0s    git tag v1.0.0 && git push                 ▶ Your laptop
T+2s    GitHub Actions triggered                   📡 GitHub
T+5s    Prepare release metadata                   ⚙️  Prep
T+10s   Clone repo + setup JDK                     ⚙️  Building
T+20s   spotlessCheck (formatting)                 ✅ Passed
T+50s   assembleRelease (all variants)             ⏳ Building
  - universal (all archs)
  - arm64-v8a
  - armeabi-v7a
  - x86
  - x86_64
T+180s  testReleaseUnitTest                        ✅ Passed
T+200s  Rename all APK files                       ✅ Renamed
T+205s  Create GitHub Release                      🏷️  Release
T+210s  Upload 5 APKs to GitHub Release            📤 Release
T+220s  1st Telegram send (universal)              🤖 Bot
T+230s  2nd Telegram send (arm64)                  🤖 Bot
T+240s  Workflow complete                          ✅ DONE

→ You receive 2 APKs in Telegram!
```

## 📁 File Organization

```
komikku/
│
├── 📄 START_HERE.md
│   └─ You are here! Overview of the complete setup
│
├── 📄 TELEGRAM_QUICK_SETUP.md
│   └─ 3-step setup checklist (read first!)
│
├── 📄 TELEGRAM_SETUP.md
│   └─ Comprehensive guide with troubleshooting
│
├── 📄 TELEGRAM_IMPLEMENTATION.md
│   └─ Technical implementation details
│
├── 📄 TELEGRAM_SETUP_COMPLETE.md
│   └─ Summary of all changes made
│
├── scripts/
│   └── 📜 send-to-telegram.sh
│       └─ Main delivery script (82 lines)
│
└── .github/workflows/
    ├── 📋 build_push.yml (MODIFIED +16 lines)
    │   └─ Added: Send APK to Telegram (CI)
    │
    └── 📋 build_release.yml (MODIFIED +34 lines)
        └─ Added: Send APKs to Telegram (Release)
```

## 🔐 Secrets Flow

```
┌───────────────────────────────────────────────────┐
│   GitHub Repository Settings                      │
│                                                   │
│   🔒 Secrets & Variables                          │
│   ├─ TELEGRAM_BOT_TOKEN                          │
│   │  Value: 123456789:ABCDefGhIjKlMnoPqRsTuVwXyZ│
│   │  (Hidden in workflow logs)                    │
│   │                                               │
│   └─ TELEGRAM_CHAT_ID                            │
│      Value: 987654321                            │
│      (Hidden in workflow logs)                    │
└─────────┬─────────────────────────────────────────┘
          │ (Injected at runtime)
          ▼
┌─────────────────────────────────────────────────┐
│  Workflow Job (build_push.yml)                  │
│                                                 │
│  run: |                                         │
│    ./scripts/send-to-telegram.sh \              │
│      "${{ secrets.TELEGRAM_BOT_TOKEN }}" \      │
│      "${{ secrets.TELEGRAM_CHAT_ID }}" \        │
│      "Nana-Comik-master-r1234.apk" \           │
│      "master" \                                 │
│      "1234"                                     │
└──┬──────────────────────────────────────────────┘
   │
   ▼
┌─────────────────────────────────────────────────┐
│  send-to-telegram.sh Script                     │
│                                                 │
│  BOT_TOKEN="$1"                                │
│  CHAT_ID="$2"                                  │
│  APK_FILE="$3"                                 │
│  ...                                            │
│                                                 │
│  curl -F "document=@$APK_FILE" \               │
│       -F "chat_id=$CHAT_ID" \                  │
│  https://api.telegram.org/bot$BOT_TOKEN/...    │
└──┬──────────────────────────────────────────────┘
   │
   ▼
┌─────────────────────────────────────────────────┐
│  Telegram Bot API                               │
│                                                 │
│  POST /sendDocument                             │
│  ├─ chat_id: 987654321                          │
│  ├─ document: APK file (binary)                 │
│  ├─ caption: Formatted message                  │
│  └─ parse_mode: Markdown                        │
└──┬──────────────────────────────────────────────┘
   │
   ▼
┌─────────────────────────────────────────────────┐
│  Your Telegram Chat                             │
│                                                 │
│  ✅ Message Received                            │
│  📦 Nana-Comik-master-r1234.apk                │
│  👆 TAP TO DOWNLOAD                            │
└─────────────────────────────────────────────────┘
```

## 📊 Message Format Breakdown

```
Message Sent to Telegram:

🚀 Komikku APK Build Ready           ← Header emoji
                                     
📦 File: Nana-Comik-master-r1234.apk ← APK filename
📊 Size: 45.2M                        ← File size
🔢 Version: master                    ← Branch/tag
📈 Build: #1234                       ← Commit count
🔗 Commit: abc1234                    ← Commit SHA
                                     
👤 Author: Developer Name             ← Git author
💬 Message: Fix PDF rendering         ← Commit message
                                     
✅ Build completed successfully!      ← Status

Emojis Used:
🚀 - Rocket (header)
📦 - Package (file)
📊 - Chart (size)
🔢 - Numbers (version)
📈 - Graph (build)
🔗 - Link (commit)
👤 - Person (author)
💬 - Speech (message)
✅ - Check (success)
```

## 🎯 Decision Tree

```
                          You Push Code
                                │
                    ┌───────────┴────────────┐
                    │                        │
            Push to Master          Push Release Tag
                    │                        │
                    ▼                        ▼
            build_push.yml           build_release.yml
                    │                        │
                    ▼                        ▼
         Build: assemblePreview    Build: assembleRelease
                    │                        │
                    ▼                        ▼
         1 APK file created        5 APK files created
         (universal)                (universal, arm64,
                                     armeabi, x86, x86_64)
                    │                        │
                    ▼                        ▼
         Send to Telegram           GitHub Release
                    │                        │
                    ▼                        ▼
         1 Message                  Send to Telegram
                    │                        │
                    ▼                        ▼
         Universal APK            2 Messages
         in your Telegram         (universal + arm64)
                    │                        │
                    └────────────┬───────────┘
                                 │
                                 ▼
                        🎉 Download APK!
```

## ⏱️ Execution Timeline

```
GitHub Actions Run Timeline:

[████████████████████████████████████████████████ 240s total]
│
├─ spotlessCheck         [████░] 15s
├─ assemblePreview/Release [████████████████░] 135s
├─ tests                 [████░] 15s
├─ Rename APK            [░] 5s
├─ Upload Artifact       [░░] 5s
└─ Send to Telegram      [░░░] 10s  ← You are here!
                                       (Latest feature!)
```

## 🚦 Status Indicators

```
Workflow Status Flow:

Setup & Prepare
    ↓
[⏳] Running
    │
    ├─ Clone repo             [✅ Done]
    ├─ Setup JDK             [✅ Done]
    ├─ Format check          [✅ Done]
    ├─ Build APK             [✅ Done]
    ├─ Run tests             [✅ Done]
    ├─ Upload artifact       [✅ Done]
    └─ Send Telegram         [✅ Done]  ← NEW STEP!
       └─ Validate secrets    [✅ Done]
       └─ Make script exec    [✅ Done]
       └─ Call Telegram API   [✅ Done]
       └─ Parse response      [✅ Done]
       └─ Log success         [✅ Done]
    
Result: [✅ PASSED]
```

---

**Visual Guide Complete!**

For more details, see the documentation files.

