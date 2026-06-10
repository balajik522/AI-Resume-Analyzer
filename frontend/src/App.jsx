import React, { useState } from 'react'

function App() {
  const [file, setFile] = useState(null)
  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState(null)
  const [resumeFile, setResumeFile] = useState(null)
  const [jobFile, setJobFile] = useState(null)
  const [jobText, setJobText] = useState('')
  const [matchResult, setMatchResult] = useState(null)

  const submit = async (e) => {
    e.preventDefault()
    if (!file) return
    setLoading(true)
    setResult(null)
    try {
      const fd = new FormData()
      fd.append('file', file)
      const res = await fetch('http://localhost:8081/api/resume/upload', {
        method: 'POST',
        body: fd
      })

      if (!res.ok) {
        // try to read body text for more info
        let text
        try { text = await res.text() } catch (e) { text = '<no body>' }
        throw new Error(`Upload failed: ${res.status} ${res.statusText} - ${text}`)
      }

      const json = await res.json()
      setResult(json)
    } catch (err) {
      setResult({ error: err.message })
    } finally {
      setLoading(false)
    }
  }

  const submitMatch = async (e) => {
    e.preventDefault()
    if (!resumeFile) return
    setLoading(true)
    setMatchResult(null)
    try {
      const fd = new FormData()
      fd.append('resume', resumeFile)
      if (jobFile) {
        fd.append('job', jobFile)
      } else if (jobText && jobText.trim().length > 0) {
        fd.append('jobText', jobText)
      }
      const res = await fetch('http://localhost:8081/api/resume/match', {
        method: 'POST',
        body: fd
      })
      if (!res.ok) {
        let text
        try { text = await res.text() } catch (e) { text = '<no body>' }
        throw new Error(`Match failed: ${res.status} ${res.statusText} - ${text}`)
      }
      const json = await res.json()
      setMatchResult(json)
    } catch (err) {
      setMatchResult({ error: err.message })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <h1>AI Resume Analyzer</h1>

      <form onSubmit={submit}>
        <input type="file" accept=".pdf,.doc,.docx,.txt" onChange={e => setFile(e.target.files[0])} />
        <button type="submit" disabled={loading}>{loading ? 'Analyzing...' : 'Upload & Analyze'}</button>
      </form>

      {result && (
        <pre style={{ whiteSpace: 'pre-wrap', marginTop: '1rem', background: '#fff', padding: '1rem', borderRadius: 6 }}>
          {JSON.stringify(result, null, 2)}
        </pre>
      )}

      <hr />

      <h2>Match Resume to Job Description</h2>
      <form onSubmit={submitMatch}>
        <div style={{ marginBottom: 8 }}>
          <label style={{ marginRight: 8 }}>Resume: </label>
          <input type="file" accept=".pdf,.doc,.docx,.txt" onChange={e => setResumeFile(e.target.files[0])} />
        </div>
        <div style={{ marginBottom: 8 }}>
          <label style={{ marginRight: 8 }}>Job Description (paste text or upload file): </label>
          <div style={{ display: 'flex', gap: 8, alignItems: 'flex-start' }}>
            <textarea value={jobText} onChange={e => setJobText(e.target.value)} placeholder="Paste job description here" style={{ minHeight: 120, width: 400 }} />
            <div>
              <div style={{ marginBottom: 8 }}>Or upload:</div>
              <input type="file" accept=".pdf,.doc,.docx,.txt" onChange={e => setJobFile(e.target.files[0])} />
            </div>
          </div>
        </div>
        <button type="submit" disabled={loading}>{loading ? 'Matching...' : 'Match and Rate (0-10)'}</button>
      </form>

      {matchResult && (
        <div style={{ marginTop: '1rem' }}>
          <h3>Match Result</h3>
          <div>Score (out of 10): <strong>{matchResult.scoreOutOf10 ?? matchResult.score ?? 'N/A'}</strong></div>
          <div>Matched Skills: {matchResult.matchedSkills ? matchResult.matchedSkills.join(', ') : 'None'}</div>
          <div>Matched Keywords: {matchResult.matchedKeywords ? matchResult.matchedKeywords.join(', ') : 'None'}</div>
          <pre style={{ whiteSpace: 'pre-wrap', background: '#fff', padding: 10, borderRadius: 6 }}>{JSON.stringify(matchResult, null, 2)}</pre>
        </div>
      )}
    </div>
  )
}

export default App
