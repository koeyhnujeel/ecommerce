#!/usr/bin/env bash
set -euo pipefail

# ---- helpers ----
die() { echo "❌ $*" >&2; exit 1; }
info() { echo "ℹ️  $*"; }
ok() { echo "✅ $*"; }

need() {
  local name="$1"
  [[ -n "${!name:-}" ]] || die "환경변수 $name 가(이) 필요합니다. (SKILL.md 규칙에 따라 Codex가 세팅해야 함)"
}

# ---- preflight ----
git rev-parse --is-inside-work-tree >/dev/null 2>&1 || die "git 레포가 아닙니다."
command -v gh >/dev/null 2>&1 || die "gh(GitHub CLI)가 필요합니다."
gh auth status >/dev/null 2>&1 || die "gh auth login 이 필요합니다."

# Determine base branch
DEFAULT_BASE="main"
if git show-ref --verify --quiet refs/heads/main; then DEFAULT_BASE="main"
elif git show-ref --verify --quiet refs/heads/master; then DEFAULT_BASE="master"
fi

BASE_BRANCH="${BASE_BRANCH:-$DEFAULT_BASE}"
need BASE_BRANCH

# Require changes
if [[ -z "$(git status --porcelain)" ]]; then
  die "변경사항이 없습니다. (git status clean)"
fi

# Require branch name & messages provided by Codex
need NEW_BRANCH
need SCOPE
need TYPE
need COMMIT_MSG_PROD
need COMMIT_MSG_TEST
need COMMIT_MSG_MISC
need PR_TITLE
need PR_BODY

CURRENT_BRANCH="$(git branch --show-current)"
info "현재 브랜치: $CURRENT_BRANCH"
info "base 브랜치: $BASE_BRANCH"
info "생성할 브랜치: $NEW_BRANCH"

# Create & switch branch (preserve working tree changes)
if git show-ref --verify --quiet "refs/heads/$NEW_BRANCH"; then
  die "로컬에 이미 브랜치가 존재합니다: $NEW_BRANCH"
fi
git switch -c "$NEW_BRANCH"
ok "브랜치 생성/체크아웃 완료: $NEW_BRANCH"

# ---- file grouping based on git status (not diff) ----
# We group by path patterns. This uses working-tree file list from status.
mapfile -t CHANGED_PATHS < <(git status --porcelain | sed -E 's/^.. //' | sed -E 's/^"(.+)"$/\1/')

# Build groups (newline-separated paths)
PROD_LIST=()
TEST_LIST=()
MISC_LIST=()

for p in "${CHANGED_PATHS[@]}"; do
  # ignore empty
  [[ -n "$p" ]] || continue

  # Prefer explicit test/main folders
  if [[ "$p" == *"/src/test/"* ]]; then
    TEST_LIST+=("$p")
  elif [[ "$p" == *"/src/main/"* ]]; then
    PROD_LIST+=("$p")
  else
    # Heuristic: Kotlin tests often end with Test.kt
    if [[ "$p" == *"Test.kt" ]]; then
      TEST_LIST+=("$p")
    else
      MISC_LIST+=("$p")
    fi
  fi
done

stage_and_commit() {
  local msg="$1"; shift
  local -a files=("$@")

  if [[ "${#files[@]}" -eq 0 ]]; then
    info "스킵: 커밋할 파일 없음 ($msg)"
    return 0
  fi

  # Stage only selected files (including deletions)
  git add -A -- "${files[@]}" || true

  if git diff --cached --quiet; then
    info "스킵: 스테이징 결과 변경 없음 ($msg)"
    return 0
  fi

  git commit -m "$msg"
  ok "커밋 완료: $msg"
}

# Commit 1: prod
stage_and_commit "$COMMIT_MSG_PROD" "${PROD_LIST[@]}"

# Commit 2: test
stage_and_commit "$COMMIT_MSG_TEST" "${TEST_LIST[@]}"

# Commit 3: misc
stage_and_commit "$COMMIT_MSG_MISC" "${MISC_LIST[@]}"

# If anything remains unstaged, commit it as misc (full automation safety net)
if [[ -n "$(git status --porcelain)" ]]; then
  info "남은 변경사항이 있어 마지막으로 전부 커밋합니다."
  git add -A
  if ! git diff --cached --quiet; then
    git commit -m "$COMMIT_MSG_MISC"
    ok "잔여 변경 커밋 완료: $COMMIT_MSG_MISC"
  fi
fi

# ---- push + PR create ----
git push -u origin "$NEW_BRANCH"
ok "origin push 완료"

PR_URL="$(gh pr create --base "$BASE_BRANCH" --head "$NEW_BRANCH" --title "$PR_TITLE" --body "$PR_BODY" --json url -q .url)"
ok "PR 생성 완료: $PR_URL"

# ---- merge (synchronous) ----
# Try merge method first to preserve commits; fallback to squash if merge is not allowed by repo policy.
set +e
gh pr merge "$PR_URL" --merge --delete-branch --yes
MERGE_RC=$?
if [[ $MERGE_RC -ne 0 ]]; then
  info "merge 방식 머지 실패(레포 정책/체크 미통과일 수 있음). squash로 재시도합니다."
  gh pr merge "$PR_URL" --squash --delete-branch --yes
  MERGE_RC=$?
fi
set -e

if [[ $MERGE_RC -ne 0 ]]; then
  die "머지 실패. (CI/브랜치 보호/리뷰 필요 가능) PR을 확인하세요: $PR_URL"
fi

ok "PR 머지 완료"

# ---- cleanup local ----
git switch "$BASE_BRANCH"
git pull --ff-only || true
git branch -D "$NEW_BRANCH" || true
git fetch --prune || true
ok "로컬 정리 완료 (브랜치 삭제 + prune)"
