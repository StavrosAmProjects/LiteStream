# T-067: Sync the official IPVIO main branch

- **Goal:** Keep the fork's source current with upstream's moving `main` branch while keeping LiteStream feature work isolated on its feature branch.
- **Affected files:** `.github/workflows/sync-ipvio-main.yml`, `docs/RELEASE.md`, `CHANGELOG.md`, `docs/tasks/WORK_STATE.md`, this task record.
- **Behavior:** Daily and manually triggered workflow fetches `Emrodev/IPVIO-Stream` `main`, merges it into an automation branch based on `codex/litestream-tv-fork`, then opens or updates a review PR into that branch. Merge conflicts stop the run. It never auto-merges or downloads/releases an upstream APK.
- **Branch flow:** Keep fork-specific work on `codex/litestream-tv-fork`, review upstream changes there, and retain LiteStream `main` for releases only.
- **Risks:** Upstream `main` may contain incomplete changes, so each PR requires review and normal project checks. Existing Actions permissions may need `Allow GitHub Actions to create and approve pull requests` enabled.
- **Validation:** `git diff --check` and manual YAML/script review. Live workflow run and GitHub permissions are unverified because the environment could not reach GitHub.
- **Status:** Implemented locally; push and first scheduled/manual run pending.
