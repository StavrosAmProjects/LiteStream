# T-066: Check official IPVIO updates

- **Goal:** Compare the official upstream release status with LiteStream's recorded source baseline and document whether any worthwhile stable updates are pending.
- **Affected files:** `README.md`, `CHANGELOG.md`, `docs/tasks/WORK_STATE.md`, this task record.
- **Finding:** On 19 November 2026, the official [IPVIO releases page](https://github.com/Emrodev/IPVIO-Stream/releases) listed v1.4.3 as latest stable, published 16 November at commit `e55395a73ee8ee7b21e67a00a677581d90fa9529`. LiteStream's recorded upstream baseline is the same commit. No newer stable release was listed.
- **Limit:** Direct network access to GitHub failed, and GitHub's commit/compare page could not be retrieved. Current upstream `main` after the recorded baseline and any unreleased commits are therefore unverified. This is not a claim that every upstream commit through the check date is integrated.
- **Decision:** No code sync is warranted based on the verified stable release listing; it is already the baseline. Recheck upstream `main` when network access is available before claiming full source freshness.
- **Tests:** `git diff --check` and manual Markdown review. No app build; documentation-only.
- **Status:** Complete with the source-branch comparison limitation documented.
