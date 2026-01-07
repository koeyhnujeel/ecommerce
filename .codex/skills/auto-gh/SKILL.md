---
name: auto-gh
description: git status 기반으로 브랜치명 생성 → 브랜치 생성 → 관련 파일 add/커밋(여러 개) → PR 생성/본문 작성 → 머지 → remote/local 브랜치 정리까지 자동 수행
metadata:
  short-description: One-command GitHub PR automation (branch/commits/PR/merge/cleanup)
---

# auto-gh skill

## 목표
- 사용자는 main(또는 base)에서 작업만 하고,
- `codex run auto-gh` 1번으로
  브랜치/커밋/PR/머지/정리를 끝낸다.

## 하드 규칙
- diff를 “시작 신호”로 쓰지 말고 `git status --porcelain`로 변경 파일 목록을 먼저 수집한다.
- 다만 커밋/PR 제목·본문을 제대로 쓰기 위해, 필요 최소한으로 변경된 파일 내용을 읽는 것은 허용한다(워킹트리 파일 읽기).
- base 브랜치는 기본 `main`. 레포가 `master`를 쓰면 `master`를 base로 사용.
- 머지는 기본적으로 **merge 방식(커밋 유지)**를 먼저 시도한다. 레포 정책으로 불가하면 squash로 fallback 한다.
- CI/브랜치 보호 규칙 때문에 즉시 머지가 불가능하면 작업을 중단하고 PR URL만 출력한다(삭제 작업은 하지 않는다).

## 브랜치명 생성 규칙
1) `git status --porcelain`로 변경 파일 목록을 확보한다.
2) scope 추론:
    - 경로에 `/domain/` 포함 → scope=domain
    - `/application/` 포함 → scope=application
    - `/adapter/webapi` 포함 → scope=api
    - `/adapter/persistence` 포함 → scope=persistence
    - `/adapter/security` 포함 → scope=security
    - `/adapter/integration` 포함 → scope=integration
    - 그 외 → scope=repo
3) type 추론:
   - 변경이 테스트만이면 `test`
   - 문서만이면 `docs`
   - 빌드/설정만이면 `chore`
   - 리팩토링이면 `refactor`
   - 그 외 기본 `feat` (명백한 버그 수정이면 `fix`)
4) slug는 대표 변경 파일/클래스 이름에서 케밥케이스로 만든다(최대 40자).
5) 최종: `{type}/{scope}-{slug}`

## 커밋 분리 규칙(완전 자동)
- Commit 1: 프로덕션 코드(domain) (예: `**/src/main/**`)
- Commit 2: 프로덕션 코드(application) (예: `**/src/main/**`)
- Commit 3: 프로덕션 코드(api) (예: `**/src/main/**`)
- Commit 4: 프로덕션 코드(persistence) (예: `**/src/main/**`)
- Commit 5: 프로덕션 코드(security) (예: `**/src/main/**`)
- Commit 6: 프로덕션 코드(integration) (예: `**/src/main/**`)
- Commit 7: 테스트 코드 (예: `**/src/test/**`)
- Commit 8: 나머지(gradle, docs, config 등)
- 그룹에 해당 파일이 없으면 해당 커밋은 건너뛴다.
- 커밋 메시지는 머리말을 제외하고 한국어로 작성
- 커밋 메시지는 Conventional Commits 형태로:
  - prod: `{type}({scope}): <핵심 변경>`
  - test: `test({scope}): <핵심 테스트>`
  - etc: `chore({scope}): <기타 변경>`

## PR 제목/본문 규칙
- 한국어로 작성
- 제목: 첫 번째 커밋(프로덕션 커밋) 요약을 기반으로 작성
  - 예: {type}: 제목
- 본문 구조:
  - 구체적인 파일/클래스 단위로 작성 (DTO 파일 제외)
  - 추상적 표현 금지 ("연동", "정비", "강화" 등 사용 금지)
  - 동사는 구체적으로: 추가, 삭제, 수정, 분리, 이동, 이름변경
  - 헥사고날 아키텍처 용어 준수

## 실행
- 위 내용을 바탕으로 `.codex/skills/auto-gh/scripts/run.sh`를 실행해 전체 플로우를 수행한다.
- 실행 전에 필요한 값(브랜치명, 커밋 메시지, PR 제목/본문)을 환경변수로 세팅한다:
    - BASE_BRANCH
    - NEW_BRANCH
    - SCOPE
    - TYPE
    - COMMIT_MSG_PROD
    - COMMIT_MSG_TEST
    - COMMIT_MSG_MISC
    - PR_TITLE
    - PR_BODY

- 파일 그룹은 스크립트에서 `git status`로 다시 계산한다(단일 소스 유지).
