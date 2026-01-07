---
name: auto-gh
description: git status 기반으로 브랜치/커밋/PR/머지/정리 자동 수행
metadata:
  short-description: One-command GitHub PR automation
---

# auto-gh skill

## 목표
- `codex run auto-gh` 명령 한 번으로 작업 내용(Unstaged)을 분석해 PR을 올리고 머지까지 완료한다.

## 실행 규칙 (Hard Rules)
- **분석 기준**: `git status --porcelain`으로 변경 파일 목록을 먼저 수집한다.
- **Base 브랜치**: 기본 `main` (레포지토리에 따라 `master` fallback).
- **머지 전략**: `Merge Commit`을 우선 시도하고, 실패 시 `Squash`로 fallback 한다.
- **안전 장치**: CI/Conflict 등으로 즉시 머지가 불가능하면 PR URL만 출력하고 종료한다.

---

## 1. 브랜치명 생성 규칙
1. `git status`의 변경 파일 경로를 분석하여 `scope`를 결정한다:
    - `core/.../domain/` → **domain**
    - `core/.../application/` → **app**
    - `api/.../adapter/webapi/` → **api**
    - `api/.../adapter/persistence/` → **db**
    - `api/.../adapter/integration/` → **ext**
    - `api/.../adapter/security/` → **security**
    - 그 외 → **repo**
2. 변경의 성격을 분석하여 `type`을 결정한다:
    - `test`, `docs`, `chore`, `refactor`, `feat`, `fix`
3. 대표 변경 파일명(Class)을 케밥케이스로 변환하여 `slug`를 만든다.
4. **최종 포맷**: `{type}/{scope}-{slug}`

---

## 2. 커밋 그룹핑 규칙 (스크립트 전달용)
스크립트 실행을 위해 다음 3가지 메시지를 환경변수로 생성한다.
메시지 포맷: `{type}({scope}): {한글 요약}`

- **COMMIT_MSG_PROD**: `src/main` 경로의 모든 프로덕션 코드 변경 (Domain, App, Adapter 포함)
    - 예: `feat(product): 상품 등록 도메인 로직 및 API 구현`
- **COMMIT_MSG_TEST**: `src/test` 경로의 테스트 코드 변경
    - 예: `test(product): 상품 등록 성공/실패 테스트 케이스 추가`
- **COMMIT_MSG_MISC**: 그 외 설정, 문서, 빌드 스크립트 등
    - 예: `chore(gradle): 로컬스택 의존성 추가`

---

## 3. PR 본문 작성 규칙 (상세 분석)

PR 본문은 **레이어별로 상세하게** 작성해야 한다.

### 분석 단계 (Chain of Thought)
1. **파일 수집**: 변경된 파일 목록을 확인한다.
2. **의미 파악**: `git diff`를 통해 각 파일의 변경 내용(추가/수정/삭제)을 파악한다.
3. **레이어 분류**: 헥사고날 아키텍처 기준(Domain, Application, Adapter, Config)으로 분류한다.
4. **템플릿 작성**: 아래 양식에 맞춰 작성한다.

### PR 본문 템플릿
```markdown
## 변경 사항

### Domain
- `클래스명`: 변경 내용 요약 (예: 가격 필드 Money 타입으로 변경)

### Application
- `클래스명`: 변경 내용 요약

### Adapter
- [API] `컨트롤러명`: 변경 내용 요약
- [Persistence] `레포지토리명`: 변경 내용 요약
- [Integration] `클라이언트명`: 변경 내용 요약

### Test
- `테스트명`: 테스트 시나리오 요약

### Config & Misc
- 설정 변경 요약