# T-056 — Reviewable upstream release sync

## Goal

Automatically bring new stable IPVIO source releases to the LiteStream repository as reviewable pull requests, while preserving manual review and LiteStream's fork-specific behavior.

## Analysis and plan

- The fork already shares upstream Git history, and its branch policy sends changes through `dev` before release.
- Sync the newest stable upstream release tag into a bot branch based on `dev`.
- Open or update one pull request for review; do not merge automatically.
- Keep upstream APK distribution separate from LiteStream's own signed releases and in-app update feed.

## Affected files

- `.github/workflows/sync-ipvio-release.yml` — scheduled and manual source synchronization.
- `docs/RELEASE.md` — distinguish source sync from LiteStream APK updates.
- `CHANGELOG.md` — record the workflow.
- `docs/tasks/WORK_STATE.md` — handoff and validation status.

## Acceptance criteria

- [x] Check upstream's latest stable GitHub release daily and allow a manual run.
- [x] Merge its source tag into a branch based on `dev` and open/update a PR into `dev`.
- [x] Stop on merge conflicts; never auto-merge the PR.
- [x] Keep upstream APKs out of LiteStream's installation/update path.
- [ ] Confirm the workflow against the live GitHub repository and upstream release endpoint.
- [ ] Observe a successful first workflow run and review the generated PR.

## Risks and validation

- A conflict stops the run and needs a maintainer to resolve it; it does not overwrite `dev`.
- The workflow requires GitHub Actions to be enabled and the repository's Actions setting to allow the workflow token to create pull requests.
- Local network access to GitHub was unavailable during implementation, so the workflow was not triggered or tested against the live repositories.
- `git diff --check` is the only local check planned for this workflow change; no app code changed.

## Status

Implemented and pushed on `codex/litestream-tv-fork` in commit `95e2cb8`. It becomes schedulable after merge to the repository's default branch. Not yet run in GitHub Actions; repository permissions and the live upstream endpoint remain unverified.
