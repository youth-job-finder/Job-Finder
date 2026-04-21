
# Git Workflow & Branch Management Guide

This document explains how to safely work with branches in Git, focusing on `develop` as the integration branch and protecting `main` as the release branch.

---

## 📌 Setting `develop` as Default Branch

1. **On GitHub**  
   - Go to **Repository Settings → Branches**  
   - Change **Default branch** from `main` to `develop`  
   - Confirm the change  

2. **Impact**  
   - New clones default to `develop`  
   - Pull requests target `develop`  
   - Branch creation bases off `develop`

3. **Best Practices**  
   - Protect `main` with branch rules (no direct pushes)  
   - Update CI/CD pipelines to trigger from `develop`  
   - Communicate to team that `develop` is the integration branch  

---

## 🔄 Git Pull

- **Definition**: Downloads changes from remote into your local branch.  
- **Command**:
  ```bash
  git pull origin develop
  ```
- **Effect**: Updates your local branch with remote commits.
- **Risks**: May cause merge conflicts if local changes differ.
- **Best Practice**: Run `git fetch` first, then review before merging.

---

## ⬆️ Git Push

- **Definition**: Uploads your local commits to the remote repository.
- **Command**:
  ```bash
  git push origin develop
  ```
- **Effect**: Publishes your work to the team.
- **Risks**: Force pushes (`--force`) can overwrite history.
- **Best Practice**: Avoid force pushes unless coordinated.

---

## 🔧 Git Merge

- **Definition**: Combines changes from one branch into another.
- **Command**:
  ```bash
  git checkout develop
  git merge feature-branch
  ```

### Types of Merges
- **Fast-forward**: Moves branch pointer forward (no conflicts).
- **Three-way merge**: Creates a merge commit when histories diverge.
- **Squash merge**: Combines all commits into one.
- **Rebase + merge**: Replays commits for linear history.

### Conflict Handling
```bash
# After resolving conflicts
git add <file>
git commit
```

---

## 🧩 Base vs. Compare in Merges

- **Base branch**: Target branch receiving changes (e.g., `develop`).
- **Compare branch**: Source branch providing changes (e.g., `feature/login`).

### Example (GitHub Pull Request)
- **Base**: `develop`
- **Compare**: `feature/login`  
  👉 "Take changes from `feature/login` and merge them into `develop`."

---

## 📊 Summary Table

| Action   | Direction        | Command Example            | Effect |
|----------|------------------|----------------------------|--------|
| **Pull** | Remote → Local   | `git pull origin develop`  | Downloads and merges remote changes |
| **Push** | Local → Remote   | `git push origin develop`  | Uploads local commits to remote |
| **Merge**| Branch → Branch  | `git merge feature-branch` | Combines histories into target branch |
| **Base** | Target branch    | `develop`                  | Receives changes |
| **Compare** | Source branch | `feature/login`            | Provides changes |

---

## 🌳 Branching Diagram (ASCII)

```text
                [main]  ← stable release branch
                   ^
                   |
             merge release/hotfix
                   |
              [develop]  ← integration branch
                   ^
                   |
          merge feature branches
                   |
     [feature/login]   [feature/search]   [feature/profile]
```

- **Feature branches** → merged into `develop`
- **Develop** → merged into `main` for releases
- **Hotfixes** → merged into both `main` and `develop`

---

## 📝 Onboarding Checklist

1. **Clone the repository**
   ```bash
   git clone <repo-url>
   cd <repo-name>
   ```

2. **Checkout the default branch (`develop`)**
   ```bash
   git checkout develop
   git pull origin develop
   ```

3. **Create a feature branch**
   ```bash
   git checkout -b feature/<your-feature-name>
   ```

4. **Make changes and commit locally**
   ```bash
   git add .
   git commit -m "Add <feature-name>"
   ```

5. **Push your feature branch to remote**
   ```bash
   git push origin feature/<your-feature-name>
   ```

6. **Open a Pull Request (PR)**
    - **Base branch**: `develop`
    - **Compare branch**: `feature/<your-feature-name>`
    - Add description and reviewers.

7. **Resolve conflicts if needed**
    - Pull latest `develop`
    - Merge into your feature branch
    - Fix conflicts, commit, and push again.

8. **Merge PR into `develop`**
    - After review and approval.
    - CI/CD runs on `develop`.

9. **Release to `main`**
    - Only after testing and approval.
    - Merge `develop` → `main` for production release.

---

## 🏷️ Branch Naming Conventions

To keep branches organized and easy to understand:

- **Feature branches**
  ```
  feature/<short-description>
  ```
  Example: `feature/login-page`, `feature/search-bar`

- **Bugfix branches**
  ```
  bugfix/<short-description>
  ```
  Example: `bugfix/null-pointer`, `bugfix/missing-icon`

- **Hotfix branches**
  ```
  hotfix/<short-description>
  ```
  Example: `hotfix/security-patch`, `hotfix/payment-error`

- **Release branches**
  ```
  release/<version>
  ```
  Example: `release/1.2.0`, `release/2026-Q1`

### ✅ Guidelines
- Use **lowercase** and **hyphens** for readability.
- Keep names **short but descriptive**.
- Avoid personal names — focus on functionality or issue.
- Always branch off from `develop` (except hotfixes, which branch from `main`).

---

## ✅ Key Takeaways

- Always **pull before pushing** to avoid conflicts.
- Protect `main` and use `develop` as the integration branch.
- Double-check **base vs. compare** when opening pull requests.
- Follow the onboarding checklist for consistent workflow.
- Use standardized **branch naming conventions** for clarity and collaboration.
