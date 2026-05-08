#!/usr/bin/env python3
"""
RelaxMind2 – Refactoring Script (Phase 2 / Option A)
- Crea backup
- Mueve archivos a nueva estructura por features
- Actualiza package declarations e imports
- Elimina anotaciones Hilt de archivos legacy
"""

import os, re, shutil, json
from pathlib import Path
from datetime import datetime

ROOT        = Path(r"c:\Users\arian\arian\Escritorio\RelaxMind2")
SRC_ROOT    = ROOT / "app/src/main/java/com/upn/relaxmind"
BACKUP_ROOT = ROOT / "backup_estructura_original"
LOG_FILE    = ROOT / "refactor_log.json"

# ─────────────────────────────────────────────
# 1. MAPA DE MOVIMIENTO: src → dst  (relativo a SRC_ROOT)
# ─────────────────────────────────────────────
FILE_MOVES = {
    # ── data / core ──────────────────────────
    "data/AppPreferences.kt":          "core/data/preferences/AppPreferences.kt",
    "data/AuthManager.kt":             "core/data/auth/AuthManager.kt",
    "data/LocalDataRepository.kt":     "core/data/repository/LocalDataRepository.kt",
    "data/models/User.kt":             "core/data/models/User.kt",
    "data/models/DataModels.kt":       "core/data/models/DataModels.kt",
    # ── data / features ──────────────────────
    "data/GamificationManager.kt":     "feature/gamification/data/GamificationManager.kt",
    "data/LumiService.kt":             "feature/ai_chat/data/LumiService.kt",
    # ── legacy XML fragments ──────────────────
    "presentation/caregiver/CaregiverManageLinksFragment.kt": "legacy/xml_fragments/caregiver/CaregiverManageLinksFragment.kt",
    "presentation/caregiver/CaregiverViewModel.kt":           "legacy/xml_fragments/caregiver/CaregiverViewModel.kt",
    "presentation/caregiver/PatientAdapter.kt":               "legacy/xml_fragments/caregiver/PatientAdapter.kt",
    # ── core UI ──────────────────────────────
    "ui/components/OmitirSkipButton.kt":       "core/ui/components/OmitirSkipButton.kt",
    "ui/components/RelaxComponents.kt":        "core/ui/components/RelaxComponents.kt",
    "ui/components/UserAvatar.kt":             "core/ui/components/UserAvatar.kt",
    "ui/modifiers/RelaxMindWindowInsets.kt":   "core/ui/modifiers/RelaxMindWindowInsets.kt",
    "ui/navigation/NavGraph.kt":               "core/ui/navigation/NavGraph.kt",
    "ui/theme/Color.kt":                       "core/ui/theme/Color.kt",
    "ui/theme/Theme.kt":                       "core/ui/theme/Theme.kt",
    "ui/theme/Type.kt":                        "core/ui/theme/Type.kt",
    # ── feature: auth ────────────────────────
    "ui/screens/LoginViewScreen.kt":           "feature/auth/ui/LoginViewScreen.kt",
    "ui/screens/SignUpScreen.kt":              "feature/auth/ui/SignUpScreen.kt",
    "ui/screens/ForgotPasswordScreen.kt":      "feature/auth/ui/ForgotPasswordScreen.kt",
    "ui/screens/WelcomeAuthScreen.kt":         "feature/auth/ui/WelcomeAuthScreen.kt",
    "ui/screens/OnboardingScreen.kt":          "feature/auth/ui/OnboardingScreen.kt",
    "ui/screens/RoleSelectionScreen.kt":       "feature/auth/ui/RoleSelectionScreen.kt",
    "ui/screens/VerifyEmailScreen.kt":         "feature/auth/ui/VerifyEmailScreen.kt",
    "ui/screens/RegistrationSuccessScreen.kt": "feature/auth/ui/RegistrationSuccessScreen.kt",
    # ── feature: dashboard ───────────────────
    "ui/screens/DashboardScreen.kt":           "feature/dashboard/ui/DashboardScreen.kt",
    # ── feature: checkin ─────────────────────
    "ui/screens/CheckInScreen.kt":             "feature/checkin/ui/CheckInScreen.kt",
    # ── feature: diary ───────────────────────
    "ui/screens/DiaryScreen.kt":               "feature/diary/ui/DiaryScreen.kt",
    # ── feature: reminders ───────────────────
    "ui/screens/RemindersScreen.kt":           "feature/reminders/ui/RemindersScreen.kt",
    # ── feature: profile ─────────────────────
    "ui/screens/ProfileScreen.kt":             "feature/profile/ui/ProfileScreen.kt",
    "ui/screens/EditProfileScreen.kt":         "feature/profile/ui/EditProfileScreen.kt",
    "ui/screens/ProfileViewScreen.kt":         "feature/profile/ui/ProfileViewScreen.kt",
    "ui/screens/SettingsScreen.kt":            "feature/profile/ui/SettingsScreen.kt",
    # ── feature: ai_chat ─────────────────────
    "ui/screens/AiChatScreen.kt":              "feature/ai_chat/ui/AiChatScreen.kt",
    # ── feature: meditation ──────────────────
    "ui/screens/MeditationScreen.kt":          "feature/meditation/ui/MeditationScreen.kt",
    # ── feature: gamification ────────────────
    "ui/screens/DiagnosticTestScreen.kt":      "feature/gamification/ui/DiagnosticTestScreen.kt",
    "ui/screens/RewardsScreen.kt":             "feature/gamification/ui/RewardsScreen.kt",
    # ── feature: emergency ───────────────────
    "ui/screens/CrisisScreen.kt":              "feature/emergency/ui/CrisisScreen.kt",
    "ui/screens/EmergencyQrScreen.kt":         "feature/emergency/ui/EmergencyQrScreen.kt",
    "ui/screens/RemoteLinkingCodeScreen.kt":   "feature/emergency/ui/RemoteLinkingCodeScreen.kt",
    # ── feature: services_map ────────────────
    "ui/screens/ServicesMapScreen.kt":         "feature/services_map/ui/ServicesMapScreen.kt",
    # ── feature: library ─────────────────────
    "ui/screens/LibraryScreen.kt":             "feature/library/ui/LibraryScreen.kt",
    # ── feature: sounds ──────────────────────
    "ui/screens/SoundsScreen.kt":              "feature/sounds/ui/SoundsScreen.kt",
    # ── feature: info ────────────────────────
    "ui/screens/InfoScreens.kt":               "feature/info/ui/InfoScreens.kt",
    # ── feature: caregiver (Compose) ─────────
    "ui/screens/CaregiverDashboardScreen.kt":      "feature/caregiver/ui/CaregiverDashboardScreen.kt",
    "ui/screens/CaregiverLinkingScreen.kt":        "feature/caregiver/ui/CaregiverLinkingScreen.kt",
    "ui/screens/CaregiverQrScannerScreen.kt":      "feature/caregiver/ui/CaregiverQrScannerScreen.kt",
    "ui/screens/CaregiverManageLinksScreen.kt":    "feature/caregiver/ui/CaregiverManageLinksScreen.kt",
    "ui/screens/CaregiverPatientDetailScreen.kt":  "feature/caregiver/ui/CaregiverPatientDetailScreen.kt",
    "ui/screens/CaregiverNotificationsScreen.kt":  "feature/caregiver/ui/CaregiverNotificationsScreen.kt",
    "ui/screens/CaregiverEditProfileScreen.kt":    "feature/caregiver/ui/CaregiverEditProfileScreen.kt",
    "ui/screens/CaregiverRegistrationScreen.kt":   "feature/caregiver/ui/CaregiverRegistrationScreen.kt",
    "ui/screens/CaregiverSettingsScreen.kt":       "feature/caregiver/ui/CaregiverSettingsScreen.kt",
    # ── utils ─────────────────────────────────
    "utils/BiometricHelper.kt":    "core/utils/BiometricHelper.kt",
    "utils/QRUtils.kt":            "core/utils/QRUtils.kt",
}

# ─────────────────────────────────────────────
# 2. PACKAGE MAPPING: old package → new package
# ─────────────────────────────────────────────
PKG_MAP = {
    "com.upn.relaxmind.data.preferences":        "com.upn.relaxmind.core.data.preferences",
    "com.upn.relaxmind.data.auth":               "com.upn.relaxmind.core.data.auth",
    "com.upn.relaxmind.data.repository":         "com.upn.relaxmind.core.data.repository",
    "com.upn.relaxmind.data.models":             "com.upn.relaxmind.core.data.models",
    "com.upn.relaxmind.data":                    "com.upn.relaxmind.core.data",  # fallback
    "com.upn.relaxmind.presentation.caregiver":  "com.upn.relaxmind.legacy.xml_fragments.caregiver",
    "com.upn.relaxmind.ui.components":           "com.upn.relaxmind.core.ui.components",
    "com.upn.relaxmind.ui.modifiers":            "com.upn.relaxmind.core.ui.modifiers",
    "com.upn.relaxmind.ui.navigation":           "com.upn.relaxmind.core.ui.navigation",
    "com.upn.relaxmind.ui.theme":                "com.upn.relaxmind.core.ui.theme",
    "com.upn.relaxmind.utils":                   "com.upn.relaxmind.core.utils",
}

# Screen-level package for each file (needed because ui.screens.* splits across many feature packages)
SCREEN_PKG_MAP = {
    "LoginViewScreen":          "com.upn.relaxmind.feature.auth.ui",
    "SignUpScreen":             "com.upn.relaxmind.feature.auth.ui",
    "ForgotPasswordScreen":     "com.upn.relaxmind.feature.auth.ui",
    "WelcomeAuthScreen":        "com.upn.relaxmind.feature.auth.ui",
    "OnboardingScreen":         "com.upn.relaxmind.feature.auth.ui",
    "RoleSelectionScreen":      "com.upn.relaxmind.feature.auth.ui",
    "VerifyEmailScreen":        "com.upn.relaxmind.feature.auth.ui",
    "RegistrationSuccessScreen":"com.upn.relaxmind.feature.auth.ui",
    "UserRole":                 "com.upn.relaxmind.feature.auth.ui",
    "DashboardScreen":          "com.upn.relaxmind.feature.dashboard.ui",
    "CheckInScreen":            "com.upn.relaxmind.feature.checkin.ui",
    "DiaryScreen":              "com.upn.relaxmind.feature.diary.ui",
    "RemindersScreen":          "com.upn.relaxmind.feature.reminders.ui",
    "ProfileScreen":            "com.upn.relaxmind.feature.profile.ui",
    "EditProfileScreen":        "com.upn.relaxmind.feature.profile.ui",
    "ProfileViewScreen":        "com.upn.relaxmind.feature.profile.ui",
    "SettingsScreen":           "com.upn.relaxmind.feature.profile.ui",
    "AiChatScreen":             "com.upn.relaxmind.feature.ai_chat.ui",
    "MeditationScreen":         "com.upn.relaxmind.feature.meditation.ui",
    "DiagnosticTestScreen":     "com.upn.relaxmind.feature.gamification.ui",
    "RewardsScreen":            "com.upn.relaxmind.feature.gamification.ui",
    "CrisisScreen":             "com.upn.relaxmind.feature.emergency.ui",
    "EmergencyQrScreen":        "com.upn.relaxmind.feature.emergency.ui",
    "RemoteLinkingCodeScreen":  "com.upn.relaxmind.feature.emergency.ui",
    "ServicesMapScreen":        "com.upn.relaxmind.feature.services_map.ui",
    "LibraryScreen":            "com.upn.relaxmind.feature.library.ui",
    "SoundsScreen":             "com.upn.relaxmind.feature.sounds.ui",
    "AboutScreen":              "com.upn.relaxmind.feature.info.ui",
    "TermsScreen":              "com.upn.relaxmind.feature.info.ui",
    "InfoScreens":              "com.upn.relaxmind.feature.info.ui",
    "CaregiverDashboardScreen":     "com.upn.relaxmind.feature.caregiver.ui",
    "CaregiverLinkingScreen":       "com.upn.relaxmind.feature.caregiver.ui",
    "CaregiverQrScannerScreen":     "com.upn.relaxmind.feature.caregiver.ui",
    "CaregiverManageLinksScreen":   "com.upn.relaxmind.feature.caregiver.ui",
    "CaregiverPatientDetailScreen": "com.upn.relaxmind.feature.caregiver.ui",
    "CaregiverNotificationsScreen": "com.upn.relaxmind.feature.caregiver.ui",
    "CaregiverEditProfileScreen":   "com.upn.relaxmind.feature.caregiver.ui",
    "CaregiverRegistrationScreen":  "com.upn.relaxmind.feature.caregiver.ui",
    "CaregiverSettingsScreen":      "com.upn.relaxmind.feature.caregiver.ui",
}

# Build reverse map: old import → new import  (for explicit screen imports)
SCREEN_IMPORT_MAP = {
    f"com.upn.relaxmind.ui.screens.{name}": f"{pkg}.{name}"
    for name, pkg in SCREEN_PKG_MAP.items()
}

# ─────────────────────────────────────────────
# 3. HILT REMOVAL (only for legacy files)
# ─────────────────────────────────────────────
HILT_IMPORTS_TO_REMOVE = [
    "import dagger.hilt.android.lifecycle.HiltViewModel",
    "import dagger.hilt.android.AndroidEntryPoint",
    "import javax.inject.Inject",
]
HILT_ANNOTATIONS_TO_REMOVE = [
    "@HiltViewModel",
    "@AndroidEntryPoint",
]
LEGACY_FILES = {
    "legacy/xml_fragments/caregiver/CaregiverManageLinksFragment.kt",
    "legacy/xml_fragments/caregiver/CaregiverViewModel.kt",
    "legacy/xml_fragments/caregiver/PatientAdapter.kt",
}

# ─────────────────────────────────────────────
# HELPERS
# ─────────────────────────────────────────────

def dst_package(dst_relative: str) -> str:
    """Derive Kotlin package from destination path relative to SRC_ROOT."""
    parts = Path(dst_relative).parent.parts
    return "com.upn.relaxmind." + ".".join(parts).replace("/", ".")


def update_package_line(content: str, new_pkg: str) -> str:
    return re.sub(r'^package\s+[\w.]+', f'package {new_pkg}', content, count=1, flags=re.MULTILINE)


def update_imports(content: str) -> str:
    lines = content.split('\n')
    result = []
    for line in lines:
        stripped = line.strip()

        # ── Wildcard import from old ui.screens → expand (handled below) ──
        # We just leave wildcard imports; they'll be replaced in a second pass
        # Actually we replace each named import first, then handle wildcards

        replaced = False

        # Named screen imports
        for old_imp, new_imp in SCREEN_IMPORT_MAP.items():
            if stripped == f"import {old_imp}":
                result.append(f"import {new_imp}")
                replaced = True
                break

        if not replaced:
            # Other package-level imports (longest match first)
            for old_pkg, new_pkg in sorted(PKG_MAP.items(), key=lambda x: -len(x[0])):
                pattern = f"import {old_pkg}."
                if stripped.startswith(pattern):
                    line = line.replace(f"import {old_pkg}.", f"import {new_pkg}.")
                    break
                # wildcard
                if stripped == f"import {old_pkg}.*":
                    line = line.replace(f"import {old_pkg}.*", f"import {new_pkg}.*")
                    break
            result.append(line)

    return '\n'.join(result)


def expand_screens_wildcard(content: str) -> str:
    """Replace 'import com.upn.relaxmind.ui.screens.*' with explicit imports per feature."""
    if "import com.upn.relaxmind.ui.screens.*" not in content:
        return content

    # Collect all unique packages that screens moved to
    unique_pkgs = sorted(set(SCREEN_PKG_MAP.values()))
    wildcard_replacements = "\n".join(f"import {pkg}.*" for pkg in unique_pkgs)

    content = content.replace(
        "import com.upn.relaxmind.ui.screens.*",
        wildcard_replacements
    )
    return content


def remove_hilt(content: str) -> str:
    lines = content.split('\n')
    result = []
    for line in lines:
        stripped = line.strip()
        # Remove Hilt import lines
        if stripped in HILT_IMPORTS_TO_REMOVE:
            result.append(f"// [HILT-REMOVED] {line}")
            continue
        # Remove Hilt annotations (standalone lines)
        if stripped in HILT_ANNOTATIONS_TO_REMOVE:
            result.append(f"// [HILT-REMOVED] {line}")
            continue
        # Fix constructor: `@Inject constructor()` → `constructor()`
        if "@Inject constructor" in line:
            result.append(line.replace("@Inject constructor", "constructor"))
            continue
        result.append(line)
    return '\n'.join(result)


def process_file(content: str, dst_relative: str) -> str:
    new_pkg = dst_package(dst_relative)
    content = update_package_line(content, new_pkg)
    content = expand_screens_wildcard(content)
    content = update_imports(content)
    if dst_relative in LEGACY_FILES:
        content = remove_hilt(content)
    return content


# ─────────────────────────────────────────────
# MAIN
# ─────────────────────────────────────────────

def main():
    log = {
        "timestamp": datetime.now().isoformat(),
        "backup": str(BACKUP_ROOT),
        "moved": [],
        "errors": [],
    }

    # ── STEP 1: Backup ────────────────────────────────────────────────
    print("=" * 60)
    print("STEP 1: Creating backup...")
    if BACKUP_ROOT.exists():
        shutil.rmtree(BACKUP_ROOT)
    shutil.copytree(
        ROOT / "app/src",
        BACKUP_ROOT / "app/src",
        ignore=shutil.ignore_patterns(".gradle", "build", "*.class")
    )
    # Also backup MainActivity
    main_src = SRC_ROOT / "MainActivity.kt"
    if main_src.exists():
        bk = BACKUP_ROOT / "MainActivity.kt"
        bk.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(main_src, bk)
    print(f"  ✓ Backup created at: {BACKUP_ROOT}")

    # ── STEP 2: Move & transform files ───────────────────────────────
    print("\nSTEP 2: Moving and transforming files...")
    for src_rel, dst_rel in FILE_MOVES.items():
        src_path = SRC_ROOT / src_rel
        dst_path = SRC_ROOT / dst_rel

        if not src_path.exists():
            msg = f"  ⚠ SKIP (not found): {src_rel}"
            print(msg)
            log["errors"].append({"file": src_rel, "error": "source not found"})
            continue

        # Read, transform, write
        try:
            content = src_path.read_text(encoding="utf-8")
            content = process_file(content, dst_rel)

            dst_path.parent.mkdir(parents=True, exist_ok=True)
            dst_path.write_text(content, encoding="utf-8")

            log["moved"].append({"from": src_rel, "to": dst_rel})
            print(f"  ✓  {src_rel}")
            print(f"     → {dst_rel}")
        except Exception as e:
            print(f"  ✗ ERROR processing {src_rel}: {e}")
            log["errors"].append({"file": src_rel, "error": str(e)})

    # ── STEP 3: Update MainActivity.kt ────────────────────────────────
    print("\nSTEP 3: Updating MainActivity.kt...")
    main_path = SRC_ROOT / "MainActivity.kt"
    if main_path.exists():
        content = main_path.read_text(encoding="utf-8")
        content = update_imports(content)
        # Update the specific imports it uses
        content = content.replace(
            "import com.upn.relaxmind.data.AppPreferences",
            "import com.upn.relaxmind.core.data.preferences.AppPreferences"
        )
        content = content.replace(
            "import com.upn.relaxmind.ui.navigation.RelaxMindNavGraph",
            "import com.upn.relaxmind.core.ui.navigation.RelaxMindNavGraph"
        )
        content = content.replace(
            "import com.upn.relaxmind.ui.theme.RelaxMindTheme",
            "import com.upn.relaxmind.core.ui.theme.RelaxMindTheme"
        )
        main_path.write_text(content, encoding="utf-8")
        print("  ✓ MainActivity.kt updated")

    # ── STEP 4: Delete old source files (only if moved successfully) ──
    print("\nSTEP 4: Removing original files...")
    moved_srcs = {item["from"] for item in log["moved"]}
    for src_rel in moved_srcs:
        src_path = SRC_ROOT / src_rel
        if src_path.exists():
            src_path.unlink()
            print(f"  🗑  Deleted: {src_rel}")

    # ── STEP 5: Remove empty old directories ──────────────────────────
    print("\nSTEP 5: Cleaning empty directories...")
    old_dirs = [
        SRC_ROOT / "data",
        SRC_ROOT / "presentation",
        SRC_ROOT / "ui/screens",
        SRC_ROOT / "ui/components",
        SRC_ROOT / "ui/modifiers",
        SRC_ROOT / "ui/navigation",
        SRC_ROOT / "ui/theme",
        SRC_ROOT / "ui",
        SRC_ROOT / "utils",
    ]
    for d in old_dirs:
        try:
            if d.exists() and not any(d.iterdir()):
                d.rmdir()
                print(f"  🗑  Removed empty dir: {d.relative_to(SRC_ROOT)}")
        except Exception:
            pass  # non-empty dirs stay

    # ── STEP 6: Save log ──────────────────────────────────────────────
    LOG_FILE.write_text(json.dumps(log, indent=2, ensure_ascii=False), encoding="utf-8")
    print(f"\n{'='*60}")
    print(f"✅ Refactoring complete!")
    print(f"   Moved : {len(log['moved'])} files")
    print(f"   Errors: {len(log['errors'])}")
    print(f"   Log   : {LOG_FILE}")
    if log["errors"]:
        print("\n⚠ Errors:")
        for e in log["errors"]:
            print(f"  - {e['file']}: {e['error']}")


if __name__ == "__main__":
    main()
