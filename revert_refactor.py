#!/usr/bin/env python3
"""
RelaxMind2 – REVERT Script
Restaura el estado original del proyecto desde backup_estructura_original/
Uso: py revert_refactor.py
"""
import shutil, os
from pathlib import Path

ROOT        = Path(r"c:\Users\arian\arian\Escritorio\RelaxMind2")
SRC_ROOT    = ROOT / "app/src/main/java/com/upn/relaxmind"
BACKUP_SRC  = ROOT / "backup_estructura_original/app/src"
LIVE_SRC    = ROOT / "app/src"

def main():
    if not BACKUP_SRC.exists():
        print("ERROR: Backup not found at:", BACKUP_SRC)
        return

    confirm = input(
        "\n⚠  This will DELETE the current app/src and restore from backup.\n"
        "   Type 'YES' to confirm: "
    )
    if confirm.strip().upper() != "YES":
        print("Aborted.")
        return

    print("\nReverting...")

    # 1. Remove current src
    shutil.rmtree(LIVE_SRC)
    print("  Removed current app/src")

    # 2. Copy backup back
    shutil.copytree(BACKUP_SRC, LIVE_SRC)
    print("  Restored from backup_estructura_original/")

    print("\nDone! Project is back to original state.")
    print("You may need to run 'Sync Project with Gradle Files' in Android Studio.")

if __name__ == "__main__":
    main()
