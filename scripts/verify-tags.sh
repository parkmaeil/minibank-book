#!/usr/bin/env bash
# 모든 chNN-end 태그가 빌드되는지 확인한다. (저장소 루트에서 실행)
set -u
ROOT=$(git rev-parse --show-toplevel)
WORK=$(mktemp -d)
FAIL=()
TAGS=$(git -C "$ROOT" tag -l 'ch*-end' | sort)
LAST=$(echo "$TAGS" | tail -1)

for TAG in $TAGS; do
  DIR="$WORK/$TAG"
  git -C "$ROOT" worktree add --detach -q "$DIR" "$TAG"
  echo "=== $TAG ==="
  if [ "$TAG" = "$LAST" ]; then GOALS="build"; else GOALS="compileJava compileTestJava"; fi
  (cd "$DIR/mini-bank" && ./gradlew -q $GOALS) || FAIL+=("$TAG:backend")
  if [ -d "$DIR/minibank-web" ]; then
    (cd "$DIR/minibank-web" && npm ci --silent && npm run build --silent) || FAIL+=("$TAG:web")
  fi
  git -C "$ROOT" worktree remove --force "$DIR"
done

if [ ${#FAIL[@]} -eq 0 ]; then echo "모든 태그 빌드 성공"; else echo "실패: ${FAIL[*]}"; exit 1; fi
