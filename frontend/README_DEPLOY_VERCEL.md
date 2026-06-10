Frontend deployment to Vercel

Option A — Deploy via Git (recommended):
1. Push this repository to GitHub.
2. In Vercel dashboard, "Import Project" → select your GitHub repo → set framework to "Other" or let Vercel detect.
3. Ensure Build Command: `npm run build` and Output Directory: `dist` (vercel.json already configured).
4. Deploy — Vercel will provide a public URL.

Option B — Deploy via Vercel CLI:
1. Install Vercel CLI: `npm i -g vercel` or `npx vercel`
2. From `frontend` folder run: `npx vercel --prod --confirm` and follow prompts to link or create a project.

Option C — One-click GitHub Actions deploy (recommended for CI):
1. Add these repository secrets in GitHub Settings → Secrets → Actions:
	- `VERCEL_TOKEN` — your Vercel personal token (create at https://vercel.com/account/tokens)
	- `VERCEL_ORG_ID` and `VERCEL_PROJECT_ID` (optional but recommended; available from Vercel project settings)
2. The repository includes a GitHub Actions workflow `.github/workflows/ci-deploy.yml` that will build the frontend and backend and deploy the frontend to Vercel when `VERCEL_TOKEN` is present. You can trigger it manually from the Actions tab (workflow name: "CI & Deploy") or on push to `main`.

Notes:
- API base URL the frontend expects is `http://localhost:8081` in development. For production, update the frontend's API calls to point to your deployed backend URL or use a relative proxy (e.g., set a runtime environment variable or rewrite in Vercel).
- If you want, I can attempt a Vercel deploy from this environment if you provide a `VERCEL_TOKEN`. Otherwise follow the GitHub Actions flow above.
