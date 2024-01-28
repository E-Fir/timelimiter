#! /usr/bin/env bash

set -euox pipefail

ROOT="${BASH_SOURCE[0]}"
if ([ -h "${ROOT}" ]); then
  while ([ -h "${ROOT}" ]); do ROOT=$(readlink "${ROOT}"); done
fi
ROOT=$(cd $(dirname "${ROOT}") && pwd)
cd "${ROOT}"

OUTPUT_FILENAME="sources.md"
function add_file() {
  FILE_TO_ADD="${1}"
  CODE_LANG="${2}"
  echo "${FILE_TO_ADD}" >> "${OUTPUT_FILENAME}"
  echo '```'"${CODE_LANG}" >> "${OUTPUT_FILENAME}"
  cat "app/src/main/${FILE_TO_ADD}" >> "${OUTPUT_FILENAME}"
  echo '```' >> "${OUTPUT_FILENAME}"
  echo '' >> "${OUTPUT_FILENAME}"
}

cat header.md > "${OUTPUT_FILENAME}"

add_file AndroidManifest.xml xml
add_file java/com/efir/timelimiter/LockLauncherActivity.kt kotlin
add_file res/layout/activity_lock_launcher.xml xml
add_file java/com/efir/timelimiter/LockScreenActivity.kt kotlin
add_file res/layout/activity_lock_screen.xml xml
add_file java/com/efir/timelimiter/LockAccessibilityService.kt kotlin
add_file java/com/efir/timelimiter/LockDeviceAdminReceiver.kt kotlin
add_file java/com/efir/timelimiter/LockService.kt kotlin
add_file java/com/efir/timelimiter/OverlayLockService.kt kotlin
add_file res/layout/overlay_lock_screen.xml xml
add_file res/xml/device_admin.xml xml
add_file res/xml/accessibility_service_config.xml xml

cat footer.md >> "${OUTPUT_FILENAME}"
