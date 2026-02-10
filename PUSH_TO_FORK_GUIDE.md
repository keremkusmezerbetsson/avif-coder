# Guide: Push Decoder-Only Branch to Fork and Open PR

## Current Status

✅ **Branch:** `decoder-only-release`  
✅ **Changes committed:** Documentation files added  
✅ **Ready to push**

## Step 1: Add Fork as Remote

You need to add the fork repository as a remote. Run this command:

```bash
git remote add fork git@github.com:keremkusmezerbetsson/avif-coder.git
```

**Alternative (if SSH doesn't work):**
```bash
git remote add fork https://github.com/keremkusmezerbetsson/avif-coder.git
```

**Verify the remote was added:**
```bash
git remote -v
```

You should see:
```
fork    git@github.com:keremkusmezerbetsson/avif-coder.git (fetch)
fork    git@github.com:keremkusmezerbetsson/avif-coder.git (push)
origin  https://github.com/awxkee/avif-coder.git (fetch)
origin  https://github.com/awxkee/avif-coder.git (push)
```

---

## Step 2: Fetch Fork's Current State

Before pushing, fetch the fork to see what branches exist:

```bash
git fetch fork
```

This will show you the branches available in the fork.

---

## Step 3: Push Your Branch to the Fork

Push your `decoder-only-release` branch to the fork:

```bash
git push fork decoder-only-release
```

**If the branch doesn't exist in the fork yet:**
```bash
git push fork decoder-only-release:decoder-only-release
```

**If you want to set upstream tracking:**
```bash
git push -u fork decoder-only-release
```

---

## Step 4: Open a Pull Request

After pushing, open a pull request:

### Option A: Via GitHub Web Interface

1. Go to: https://github.com/keremkusmezerbetsson/avif-coder
2. You should see a banner saying "Your recently pushed branches" with `decoder-only-release`
3. Click **"Compare & pull request"**
4. Or go to: https://github.com/keremkusmezerbetsson/avif-coder/compare/decoder-only-release

### Option B: Direct Link

Create PR from fork to upstream:
- **Base repository:** `awxkee/avif-coder`
- **Base branch:** `master` (or `dev` if that's the target)
- **Head repository:** `keremkusmezerbetsson/avif-coder`
- **Compare branch:** `decoder-only-release`

Direct link format:
```
https://github.com/awxkee/avif-coder/compare/master...keremkusmezerbetsson:avif-coder:decoder-only-release
```

---

## Step 5: PR Title and Description

### Suggested PR Title:
```
Proposal: Decoder-Only Release - 60-70% Size Reduction
```

### Suggested PR Description:

You can use the content from `GITHUB_ISSUE_DECODER_ONLY.md` as the PR description, or use this template:

```markdown
## Summary

This PR proposes a **decoder-only release** that removes all encoding functionality while maintaining full decoding capabilities. This results in a **60-70% reduction in native library size** (from ~12-14 MB to ~3.5-4.5 MB per ABI).

**Branch:** `decoder-only-release`

## Key Changes

- ❌ Removed encoding APIs (`encodeAvif()`, `encodeHeic()`)
- ❌ Removed encoder libraries (libaom, x265, SVT-AV1, kvazaar)
- ✅ Retained full AVIF/HEIC decoding support
- ✅ All HDR, wide color gamut, and ICC profile features maintained
- ✅ Android 15+ compatibility (16 KB page size alignment)

## Size Reduction

| ABI | Before | After | Savings |
|-----|--------|-------|---------|
| arm64-v8a | ~12-14 MB | ~3.5 MB | ~60-70% |
| armeabi-v7a | ~10-12 MB | ~2.6 MB | ~60-70% |
| x86 | ~14-16 MB | ~3.8 MB | ~60-70% |
| x86_64 | ~15-17 MB | ~4.5 MB | ~60-70% |

**Total AAR size:** 9.2 MB (down from ~18-22 MB)

## Documentation

- `DECODER_ONLY_SUMMARY.md` - Comprehensive summary of all changes
- `AAR_SIZE_ANALYSIS.md` - Detailed AAR size breakdown
- `GITHUB_ISSUE_DECODER_ONLY.md` - Proposal document
- `AAR_INTEGRATION_GUIDE.md` - AAR integration guide

## Testing

✅ All decoding functionality verified:
- AVIF/HEIC decoding
- HDR images (10-bit, 12-bit)
- ICC profiles
- Animated AVIF
- Coil integration

## Migration

Apps using encoding can:
1. Stay on `master` branch (full functionality)
2. Use alternative encoding solutions
3. Build from source if needed

## Questions

- Should this be a separate version/tag or new major version?
- Should we maintain both branches long-term?
- Any concerns about removing encoding functionality?
```

---

## Troubleshooting

### Issue: "Permission denied" when adding remote

**Solution:** Check file permissions:
```bash
ls -la .git/config
chmod 644 .git/config
```

Or use HTTPS instead of SSH:
```bash
git remote add fork https://github.com/keremkusmezerbetsson/avif-coder.git
```

### Issue: "Remote already exists"

**Solution:** Remove and re-add:
```bash
git remote remove fork
git remote add fork git@github.com:keremkusmezerbetsson/avif-coder.git
```

### Issue: "Updates were rejected"

**Solution:** The fork might have different commits. Force push (use with caution):
```bash
git push -f fork decoder-only-release
```

**Better approach:** Check what's different:
```bash
git fetch fork
git log fork/decoder-only-release..decoder-only-release
```

### Issue: "Branch not found" in fork

**Solution:** Make sure you pushed to the correct remote:
```bash
git push fork decoder-only-release:decoder-only-release
```

---

## Alternative: Create PR from Command Line

If you have GitHub CLI installed:

```bash
# Install GitHub CLI if needed: brew install gh

# Authenticate
gh auth login

# Create PR
gh pr create \
  --repo awxkee/avif-coder \
  --base master \
  --head keremkusmezerbetsson:decoder-only-release \
  --title "Proposal: Decoder-Only Release - 60-70% Size Reduction" \
  --body-file GITHUB_ISSUE_DECODER_ONLY.md
```

---

## Quick Command Summary

```bash
# 1. Add fork remote
git remote add fork git@github.com:keremkusmezerbetsson/avif-coder.git

# 2. Verify remote
git remote -v

# 3. Fetch fork
git fetch fork

# 4. Push branch
git push -u fork decoder-only-release

# 5. Open PR via GitHub web interface
# Go to: https://github.com/keremkusmezerbetsson/avif-coder
```

---

## Next Steps After PR is Created

1. **Link related issues** (if any exist)
2. **Request reviews** from maintainers
3. **Respond to feedback** and make changes if needed
4. **Update PR** by pushing new commits:
   ```bash
   git add .
   git commit -m "Address review feedback"
   git push fork decoder-only-release
   ```

---

**Last Updated:** January 2026
