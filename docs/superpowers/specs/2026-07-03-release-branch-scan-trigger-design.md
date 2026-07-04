# Validate Point ① — Release Branch Scan Trigger (real scan, app-java-demo-8)

## Context

Continuation of the same validation goal originally scoped against
`app-java-demo-secrets`: prove that the untested Point ① workflow pair
(`shared/endor-release-branch-scan.yml` + `caller-examples/endor-release-branch.yml`,
from the ACA CI/CD proposal at
`~/Documents/Claude/Projects/American Acceptance Progress Update/workflows/`)
actually fires on release-branch creation and push, including a real
feature-branch-merge scenario, and does not fire for other branches.

Moved to `app-java-demo-8` because:
- `app-java-demo-secrets` blocked all `Write` tool access under an Endor
  governance policy (security-critical file / secrets-repo guard) that
  explicitly forbids working around it.
- `app-java-demo` (the other candidate) is a fork of `endorlabs-demos/app-java-demo`;
  PRs there need extra care to target the fork, not upstream, so it was
  deprioritized in favor of a repo with no fork complications.
- `app-java-demo-8` has no fork ambiguity, has write access, and already has
  a proven working real-scan pattern on its `setup_gha_scan` branch:
  `namespace: 'leonardo-learn'` with `enable_github_action_token: true` and
  no repo secrets required.

## Goal (upgraded scope)

Given the proven `leonardo-learn` scan pattern is available here, this test
goes **full end-to-end**, not stub-only:
- Trigger fires on release-branch **creation**.
- Trigger fires on **push** to a release branch, including a real PR merge
  from a feature branch.
- Trigger does **not** fire for non-release branches (negative test).
- Each triggered run executes a **real** `endorlabs/github-action` scan
  (dependencies + secrets) against the `leonardo-learn` namespace and
  reports findings to Endor — not a stub.

## Design

Two workflow files, mirroring the real shared+caller split, committed to
`main` first (both `create` and `push` trigger evaluation require the
workflow file to exist on the default branch or the pushed ref, so the
files must land on `main` before any release branch is cut):

1. **`.github/workflows/endor-release-branch-scan.yml`** — reusable
   workflow (`workflow_call`), scan step is the real
   `endorlabs/github-action@v1` with `namespace: 'leonardo-learn'`,
   `enable_github_action_token: true`, `pr: false`, `scan_dependencies: true`,
   `scan_secrets: true` — same pattern already proven on `setup_gha_scan`.
2. **`.github/workflows/endor-release-branch.yml`** — caller, copied from
   `caller-examples/endor-release-branch.yml` unchanged except the `uses:`
   target, which points at the same-repo reusable workflow via
   `./.github/workflows/endor-release-branch-scan.yml` (same-repo relative
   ref) instead of the nonexistent external
   `acacceptance/github-workflows-shared` repo. Trigger definitions,
   `if:` gating, and concurrency group are left as originally drafted.

## Test plan

1. Merge the two workflow files into `main` via a short-lived branch + PR.
2. **Create event**: create `release/1.0.0` off `main`, push it → expect a
   run entering via the `create` branch of the `if:` condition.
3. **Push event (direct commit)**: commit directly to `release/1.0.0` →
   expect a run via the `push` branch of the `if:`.
4. **Push event (feature merge)**: create a small feature branch, open a PR
   into `release/1.0.0`, merge it → the merge-commit push should trigger a
   run, validating the "feature branch merged into release" scenario.
5. **Negative test**: push a commit to a non-release branch → expect no
   workflow run at all.
6. For each case, capture evidence via `gh run list` / `gh run view --log`,
   including confirmation that the real Endor scan step executed and
   reported a result (not just that the job started).

## Outcome

Workflow files remain in `app-java-demo-8` afterward as a working, proven
example with real Actions run history and real Endor scan results —
addressing the client's hesitance about untested examples.
